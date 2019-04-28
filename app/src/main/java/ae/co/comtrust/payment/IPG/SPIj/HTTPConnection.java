package ae.co.comtrust.payment.IPG.SPIj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPConnection extends Connection
{
  static String output_from_server;
  public static HttpURLConnection con;
  
  public HTTPConnection() {}
  
  public void connect(String address, int port) throws Exception
  {
    try
    {
      URL myUrl = new URL("http", address, port, "");
      URLConnection uc = myUrl.openConnection();
      con = (HttpURLConnection)uc;
      
      con.connect();
      con.disconnect();
    }
    catch (MalformedURLException e)
    {
      throw new Exception("URL problem occured, " + e.toString());
    }
    catch (Exception e)
    {
      throw e;
    }
  }
  
  public String send(String msg)
    throws Exception
  {
    try
    {
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false);
      con.setRequestMethod("POST");
      
      con.setRequestProperty("Content-Type", "text/xml-standard-api");
      con.setRequestProperty("Content-Length", String.valueOf(msg.getBytes("UTF-8").length));
      con.setRequestProperty("Accept", "text/xml-standard-api");
      con.setRequestProperty("User-Agent", "SPIj/1.0");
      
      OutputStream os = con.getOutputStream();
      OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
      osw.write(msg);
      
      osw.flush();
      osw.close();
      
      InputStreamReader in = new InputStreamReader(con.getInputStream(), "UTF-8");
      
      BufferedReader reader = new BufferedReader(in);
      
      int responseCode = con.getResponseCode();
      String responseMessage = con.getResponseMessage();
      if ((responseCode != 100) && (responseCode != 200)) {
        throw 
          new Exception(
          "Send to HTTP Server failed. HTTP Response Code: " + 
          responseCode + 
          "Response Message: " + 
          responseMessage);
      }
      int Length = con.getContentLength();
      
      StringBuffer buffer = new StringBuffer();
      int ch;
      while ((ch = reader.read()) > -1) {
        buffer.append((char)ch);
      }
      output_from_server = buffer.toString();
      
      in.close();
      os.close();
      
      return output_from_server;
    }
    catch (Exception e)
    {
      throw e;
    }
  }
  
  public void disconnect()
  {
    con.disconnect();
  }
}
