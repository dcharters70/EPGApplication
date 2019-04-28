package ae.co.comtrust.payment.IPG.SPIj;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class XMLTransaction
{
  static String version;
  String xml = null;
  Element root = null;
  static final String space = " ";
  static final String start = "<";
  static final String end = ">";
  static final String startSlash = "</";
  static final String equal = "=";
  static final String amp = "&";
  static final String quot = "\"";
  static final String apos = "'";

  public XMLTransaction(String version_)
  {
    version = version_;
    this.xml = new String();
  }

  public String constructOutput(String transactionType, Map map)
          throws Exception
  {
    Document doc = null;
    if (map.size() == 0) {
      throw new Exception("Data is empty");
    }
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      doc = db.newDocument();
      this.root = doc.createElement(transactionType);
      if (version != null) {
        this.root.setAttribute("version", version);
      }
      Set set = map.keySet();
      Iterator iterator = set.iterator();
      String element = null;
      Element xmlElement = null;
      String value = null;
      Text xmlValue = null;
      while (iterator.hasNext())
      {
        element = (String)iterator.next();
        value = (String)map.get(element);

        StringTokenizer subElements = new StringTokenizer(element, "/");
        if (subElements.countTokens() > 1)
        {
          Element firstElement = doc.createElement(subElements
                  .nextToken());
          xmlElement = firstElement;
          Element prevElement = null;
          boolean parentFound = false;
          while (subElements.hasMoreTokens())
          {
            prevElement = xmlElement;
            xmlElement = doc.createElement(subElements.nextToken());
            Node trueParent = isParentAlreadyExist(prevElement, this.root);
            if (trueParent == null)
            {
              prevElement.appendChild(xmlElement);
            }
            else
            {
              prevElement = (Element)trueParent;
              parentFound = true;

              Node secondTrueParent = isParentAlreadyExist(xmlElement, prevElement);
              if (secondTrueParent == null) {
                prevElement.appendChild(xmlElement);
              }
            }
          }
          if ((value.indexOf("<") != -1) ||
                  (value.indexOf(">") != -1) ||
                  (value.indexOf("&") != -1) ||
                  (value.indexOf("\"") != -1) ||
                  (value.indexOf("'") != -1)) {
            xmlValue = doc.createCDATASection(value);
          } else {
            xmlValue = doc.createTextNode(value);
          }
          xmlElement.appendChild(xmlValue);
          prevElement.appendChild(xmlElement);
          if (!parentFound) {
            this.root.appendChild(firstElement);
          }
        }
        else
        {
          xmlElement = doc.createElement(element);
          if ((value.indexOf("<") != -1) ||
                  (value.indexOf(">") != -1) ||
                  (value.indexOf("&") != -1) ||
                  (value.indexOf("\"") != -1) ||
                  (value.indexOf("'") != -1)) {
            xmlValue = doc.createCDATASection(value);
          } else {
            xmlValue = doc.createTextNode(value);
          }
          xmlElement.appendChild(xmlValue);
          this.root.appendChild(xmlElement);
        }
      }
    }
    catch (Exception e)
    {
      throw new TransactionException("Failed to create XML: " +
              e.toString());
    }
    constructOutput(this.root);
    return getOutput();
  }

  private Node isParentAlreadyExist(Element parentNode, Element rootNode)
  {
    if (parentNode != null)
    {
      Node parentInRoot = getElement(rootNode, parentNode.getNodeName());
      return parentInRoot;
    }
    return null;
  }

  private Node getElement(Node rootNode, String strTagName)
  {
    String strValue = null;
    boolean nodeFound = false;
    NodeList children = null;
    if (rootNode != null)
    {
      if ((rootNode.getNodeType() == 1) &&
              (rootNode.getNodeName().equals(strTagName))) {
        return rootNode;
      }
      if (!nodeFound)
      {
        children = rootNode.getChildNodes();
        for (int idx = 0; idx < children.getLength(); idx++)
        {
          Node node = children.item(idx);
          if ((node.getNodeType() == 1) &&
                  (node.getNodeName().equals(strTagName))) {
            return node;
          }
        }
      }
      if (!nodeFound) {
        for (int idx = 0; idx < children.getLength(); idx++)
        {
          Node node = children.item(idx);
          Node requestedNode = getElement(node, strTagName);
          if (requestedNode != null) {
            return requestedNode;
          }
        }
      }
    }
    return null;
  }

  void constructOutput(Element element)
  {
    String name = null;
    String value = null;
    NodeList children = null;

    String elementName = element.getTagName();
    String elementValue = null;

    this.xml = (this.xml + "<" + elementName);
    NamedNodeMap atts = element.getAttributes();
    for (int i = 0; i < atts.getLength(); i++)
    {
      Node att = atts.item(i);
      name = att.getNodeName();
      if (att.getNodeType() == 3) {
        value = att.getNodeValue();
      } else if (att.getNodeType() == 4) {
        value = "<![CDATA[" + att.getNodeValue() + "]]>";
      } else {
        value = att.getNodeValue();
      }
      this.xml = (this.xml + " " + name + "=" + "\"" + value + "\"");
    }
    this.xml += ">";
    children = element.getChildNodes();
    for (int i = children.getLength() - 1; i > -1; i--)
    {
      Node child = children.item(i);
      if (child.getNodeType() == 1) {
        constructOutput((Element)child);
      } else if (child.getNodeType() == 3) {
        elementValue = child.getNodeValue();
      } else if (child.getNodeType() == 4) {
        elementValue = "<![CDATA[" + child.getNodeValue() + "]]>";
      }
      if (elementValue != null) {
        this.xml += elementValue;
      }
    }
    this.xml = (this.xml + "</" + elementName + ">");
  }

  String getOutput()
  {
    return this.xml;
  }

  public Map destructOutput(String response)
          throws Exception
  {
    Map map = new HashMap();
    Document doc = null;
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      doc = db.parse(new InputSource(
              new StringReader(response)));
    }
    catch (Exception e)
    {
      throw new TransactionException("Problem parsing the file." +
              e.toString());
    }
    Element root = doc.getDocumentElement();
    Attr attr = root.getAttributeNode("version");
    if (attr != null) {
      map.put("ResponseVersion", attr.getValue());
    }
    NodeList children = root.getChildNodes();
    Node child = null;
    for (int i = 0; i < children.getLength(); i++)
    {
      boolean deep = false;
      Node node = children.item(i);
      Node firstNode = node;
      if (node.getNodeType() == 1 &&  firstNode.getNextSibling() != null) {
        for (; (firstNode.getChildNodes().item(0) != null) &&
                (firstNode.getChildNodes().item(0).getNodeType() == 1);)
        {
          deep = true;
          firstNode = firstNode.getChildNodes().item(0);
          child = firstNode.getChildNodes().item(0);
          addChild(map,
                  node.getNodeName() + "/" + firstNode.getNodeName(),
                  child);

          firstNode = firstNode.getNextSibling();
          child = firstNode.getChildNodes().item(0);
          addChild(
                  map,
                  node.getNodeName() + "/" +
                          firstNode.getNodeName(), child);
        }
      }
      if (!deep)
      {
        child = node.getChildNodes().item(0);
        addChild(map, node.getNodeName(), child);
      }
    }
    return map;
  }

  public Map destructOutput(String response, boolean recursion)
          throws Exception
  {
    if (!recursion) {
      return destructOutput(response);
    }
    Map map = new HashMap();
    Document doc = null;
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      doc = db.parse(new InputSource(new StringReader(response)));
    }
    catch (Exception e)
    {
      throw new TransactionException("Problem parsing the file." +
              e.toString());
    }
    Element root = doc.getDocumentElement();
    Attr attr = root.getAttributeNode("version");
    if (attr != null) {
      map.put("ResponseVersion", attr.getValue());
    }
    map.putAll(recursiveOutput(root.getChildNodes(), ""));
    return map;
  }

  private Map recursiveOutput(NodeList children, String parentHierarchy)
          throws Exception
  {
    Map map = new HashMap();
    Node child = null;
    StringBuffer allChildren = new StringBuffer();
    if (!parentHierarchy.matches("")) {
      parentHierarchy = parentHierarchy + "/";
    }
    for (int i = 0; i < children.getLength(); i++)
    {
      boolean deep = false;
      Node node = children.item(i);
      if (node.getNodeType() == 1)
      {
        deep = true;
        map.putAll(recursiveOutput(node.getChildNodes(),
                parentHierarchy + node.getNodeName()));
        if (allChildren.length() > 0) {
          allChildren.append(", ");
        }
        allChildren.append(parentHierarchy + node.getNodeName());
      }
      if (deep)
      {
        child = node.getChildNodes().item(0);
        if ((child != null) && (child.getNodeValue() != null)) {
          addChild(map, parentHierarchy + node.getNodeName(), child);
        }
      }
    }
    return map;
  }

  private void addChild(Map map, String childName, Node child)
  {
    if (child != null)
    {
      if ((child.getNodeType() == 3) ||
              (child.getNodeType() == 4)) {
        map.put(childName, child.getNodeValue());
      }
    }
    else {
      map.put(childName, "");
    }
  }
}
