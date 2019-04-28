package ae.co.comtrust.payment.IPG.SPIj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class Transaction {
    protected boolean initialized;
    protected boolean executed;
    protected boolean isValid;
    protected Set privateSet;
    protected Set nonPrivateSet;
    protected Map earlyBinding;
    protected Map lateBinding;
    private static String supportedSPIProtocol = "1";
    protected String version;
    protected String transactionType;
    private final int default_connection_timeout = 3000;


    public String FinalResponse;


    private int NumOfRetries;


    private int MilliSecondsDelayInRetries;


    private String tlsVersion;


    public Transaction()
            throws IOException, TransactionException {
        this(System.getProperty("java.home") + System.getProperty("file.separator") + "lib" + System.getProperty("file.separator") + "SPI.properties");
    }


    public Transaction(String configFilePath)
            throws IOException, TransactionException {
        initialized = false;
        executed = false;
        isValid = true;

        privateSet = new HashSet();
        privateSet.add("ConnectionAddress");
        privateSet.add("ConnectionPort");
        privateSet.add("ConnectionTimeout");
        privateSet.add("ConnectionSecure");
        privateSet.add("MerchantKeystore");
        privateSet.add("MerchantKeystorePassword");
        privateSet.add("TrustedKeystore");
        privateSet.add("TrustedKeystorePassword");

        nonPrivateSet = new HashSet();
        nonPrivateSet.add("Customer");
        nonPrivateSet.add("Store");
        nonPrivateSet.add("Terminal");
        nonPrivateSet.add("OrderIDFormat");
        nonPrivateSet.add("Language");

        earlyBinding = new HashMap();
        lateBinding = new HashMap();


        Properties properties = new Properties();

        if (configFilePath == null)
            configFilePath = "config file location not specified";
        if (configFilePath != null) {
            try {
                properties.load(new FileInputStream(configFilePath));
            } catch (IOException e) {
                throw new TransactionException(e.toString());
            }
        }

        Enumeration<?> enu = properties
                .propertyNames();


        while (enu.hasMoreElements()) {
            String element = (String) enu.nextElement();

            if ((privateSet.contains(element)) ||
                    (nonPrivateSet.contains(element))) {
                if ((element.equals("MerchantKeystorePassword")) ||
                        (element.equals("TrustedKeystorePassword"))) {
                    earlyBinding.put(element, new IPGEncryption()
                            .decrypt(properties.getProperty(element)));
                } else {
                    earlyBinding.put(element,
                            properties.getProperty(element));
                }
            }
        }

        NumOfRetries = 0;
        MilliSecondsDelayInRetries = 0;

        if ((properties.containsKey("NumOfRetries")) &&
                (properties.containsKey("MilliSecondsDelayInRetries"))) {
            String numOfRetriesStr = properties.getProperty("NumOfRetries");
            String milliSecDelayStr = properties
                    .getProperty("MilliSecondsDelayInRetries");

            if ((numOfRetriesStr != null) && (milliSecDelayStr != null)) {
                NumOfRetries = Integer.parseInt(numOfRetriesStr);
                MilliSecondsDelayInRetries =
                        Integer.parseInt(milliSecDelayStr);
            }
        }

        if (properties.containsKey("TLSVersion")) {
            tlsVersion = properties.getProperty("TLSVersion");
        }


        if (!earlyBinding.containsKey("ConnectionSecure"))
            earlyBinding.put("ConnectionSecure", "true");
        if (!earlyBinding.containsKey("ConnectionTimeout")) {
            earlyBinding.put("ConnectionTimeout", "30");
        }
    }


    public void execute()
            throws TransactionException {
        String response = null;

        if (!isValid)
            throw new TransactionException(
                    "Error: 9050. Transaction object is invalid");
        if (!initialized) {
            throw new TransactionException("Error: 9051. Transaction object is not initialized");
        }
        if (executed) {
            throw new TransactionException("Error: 9100. Transaction object already execute.\n");
        }

        Map request = buildRequest();
        XMLTransaction xmlTransaction = new XMLTransaction(version);
        String msg = null;
        try {
            msg = xmlTransaction.constructOutput(transactionType, request);
        } catch (Exception e) {
            throw new TransactionException("Error: 9020.Error while constructing XML request " +
                    e.toString());
        }


        try {
            response = sendRequest(msg);
        } catch (Exception e) {
            throw new TransactionException(e.toString());
        }


        Map newProps = null;
        try {
            if (response != null) {
                FinalResponse = response;
                newProps = xmlTransaction.destructOutput(response, true);
                lateBinding.putAll(newProps);
            }
        } catch (Exception e) {
            lateBinding.put("ResponseCode", "9040");
            lateBinding.put("ResponseClass", "9000");
            lateBinding.put("ResponseDescription", e.toString());
            lateBinding.put("ResponseMessage",
                    "Error parsing returned xml from server");
            lateBinding.put("ResponseClassDescription", "XML Problem.");
        }

        String protocol = null;
        if (version != null)
            protocol = version.substring(0, version.indexOf("."));
        if ((version == null) || (protocol.equals("0"))) {
            lateBinding.put("ResponseClass", lateBinding.get("ResponseCode"));
            lateBinding.put("ResponseMessage",
                    lateBinding.get("ResponseDescription"));
        }

        String tempcust = "Dubai eGovernment";

        if ((getCustomer().trim().toLowerCase().equals(tempcust.trim().toLowerCase())) &&
                (lateBinding.containsKey("PaymentPage"))) {
            String paymentPage = getProperty("PaymentPage");

            paymentPage = paymentPage + "&id=DeG";
            lateBinding.remove("PaymentPage");
            lateBinding.put("PaymentPage", paymentPage);
        }


        executed = true;
    }


    public String getConnectionAddress()
            throws TransactionException {
        return getSpecific("ConnectionAddress");
    }


    public int getConnectionPort()
            throws TransactionException {
        return Integer.parseInt(getSpecific("ConnectionPort"));
    }


    public boolean getConnectionSecure()
            throws TransactionException {
        String res = getSpecific("ConnectionSecure");
        return Boolean.valueOf(res).booleanValue();
    }


    public int getConnectionTimeout()
            throws TransactionException {
        return Integer.parseInt(getSpecific("ConnectionTimeout"));
    }


    public String getCustomer()
            throws TransactionException {
        return getSpecific("Customer");
    }


    public int getLanguage()
            throws TransactionException {
        return Integer.parseInt(getSpecific("Language"));
    }


    public String getOrderIDFormat()
            throws TransactionException {
        return getSpecific("OrderIDFormat");
    }


    public String getMerchantKeystorePassword()
            throws TransactionException {
        return getSpecific("MerchantKeystorePassword");
    }


    public String getMerchantKeystore()
            throws TransactionException {
        return getSpecific("MerchantKeystore");
    }


    public String getProperty(String propertyName)
            throws TransactionException {
        if (!isValid)
            throw new TransactionException(
                    "Error: 9050. Transaction object is invalid");
        if (!initialized) {
            throw new TransactionException("Error: 9051. Transaction object is not initialized");
        }

        if (privateSet.contains(propertyName)) {
            if (lateBinding.containsKey(propertyName)) {
                return (String) lateBinding.get(propertyName);
            }
            return " Property " + propertyName + "does not exist ";
        }


        if (lateBinding.containsKey(propertyName))
            return (String) lateBinding.get(propertyName);
        if ((nonPrivateSet.contains(propertyName)) &&
                (earlyBinding.containsKey(propertyName))) {
            return (String) earlyBinding.get(propertyName);
        }
        return " Property " + propertyName + "does not exist ";
    }


    public int getResponseClass()
            throws TransactionException {
        return Integer.parseInt(getSpecific("ResponseClass"));
    }


    public int getResponseCode()
            throws TransactionException {
        return Integer.parseInt(getSpecific("ResponseCode"));
    }


    public String getResponseDescription()
            throws TransactionException {
        return getSpecific("ResponseDescription");
    }


    public String getResponseMessage()
            throws TransactionException {
        return getSpecific("ResponseMessage");
    }


    public String getStore()
            throws TransactionException {
        return getSpecific("Store");
    }


    public String getTerminal()
            throws TransactionException {
        return getSpecific("Terminal");
    }


    public String getTrustedKeystore()
            throws TransactionException {
        return getSpecific("TrustedKeystore");
    }


    public String getTrustedKeystorePassword()
            throws TransactionException {
        return getSpecific("TrustedKeystorePassword");
    }


    public void initialize(String transactionType)
            throws TransactionException {
        initialize(transactionType, supportedSPIProtocol + ".0");
    }


    public void initialize(String transactionType, String version)
            throws TransactionException {
        if (version == null) {
            this.version = null;
        } else {
            String str = version.substring(0, version.indexOf("."));
        }


        this.version = version;
        this.transactionType = transactionType;
        initialized = true;
        executed = false;
        lateBinding.clear();
    }


    public void setConnectionAddress(String dnsName)
            throws TransactionException {
        setSpecific("ConnectionAddress", dnsName);
    }


    public void setConnectionPort(int port)
            throws TransactionException {
        setSpecific("ConnectionPort", Integer.toString(port));
    }


    public void setConnectionSecure(boolean secure)
            throws TransactionException {
        if (secure) {
            setSpecific("ConnectionSecure", "true");
        } else {
            setSpecific("ConnectionSecure", "false");
        }
    }


    public void setConnectionTimeout(int timeout)
            throws TransactionException {
        setSpecific("ConnectionTimeout", Integer.toString(timeout));
    }


    public void setCustomer(String customer)
            throws TransactionException {
        setSpecific("Customer", customer);
    }


    public void setLanguage(int language)
            throws TransactionException {
        setSpecific("Language", Integer.toString(language));
    }


    public void setOrderIDFormat(String orderIDFormat)
            throws TransactionException {
        setSpecific("OrderIDFormat", orderIDFormat);
    }


    public void setMerchantKeystore(String storeName)
            throws TransactionException {
        setSpecific("MerchantKeystore", storeName);
    }


    public void setMerchantKeystorePassword(String password)
            throws TransactionException {
        setSpecific("MerchantKeystorePassword", password);
    }


    public void setProperty(String propertyName, String propertyValue)
            throws TransactionException {
        if (!isValid)
            throw new TransactionException(
                    "Error: 9050. Transaction object is invalid");
        if (!initialized) {
            throw new TransactionException("Error: 9051. Transaction object is not initialized");
        }
        if (executed) {
            throw new TransactionException("Error: 9100. Transaction object already execute.\n");
        }
        try {
            lateBinding.put(propertyName, propertyValue);
        } catch (Exception e) {
            isValid = false;
            throw new TransactionException(e.toString());
        }
    }


    public void setStore(String store)
            throws TransactionException {
        setSpecific("Store", store);
    }


    public void setTerminal(String terminal)
            throws TransactionException {
        setSpecific("Terminal", terminal);
    }


    public void setTrustedKeystore(String storeName)
            throws TransactionException {
        setSpecific("TrustedKeystore", storeName);
    }


    public void setTrustedKeystorePassword(String password)
            throws TransactionException {
        setSpecific("TrustedKeystorePassword", password);
    }


    void setSpecific(String property, String value)
            throws TransactionException {
        if ((property == null) || (property.equals(""))) {
            throw new TransactionException("Error: 9056. Property name is empty or null");
        }
        if (value == null) {
            throw new TransactionException("Error: 9057. Property value is empty");
        }
        if (!isValid)
            throw new TransactionException(
                    "Error: 9050. Transaction object is invalid");
        if ((executed) && (!privateSet.contains(property))) {
            throw new TransactionException("Error: 9058.Cannot call set" +
                    property + " after calling execute.");
        }
        try {
            earlyBinding.put(property, value);
        } catch (Exception e) {
            isValid = false;
            throw new TransactionException("Error: 9059.Unable to set " +
                    property + " " + e.toString());
        }
    }


    String getSpecific(String property)
            throws TransactionException {
        if (!isValid)
            throw new TransactionException(
                    "Error: 9050. Transaction object is invalid");
        if (privateSet.contains(property))
            return (String) earlyBinding.get(property);
        if ((nonPrivateSet.contains(property)) &&
                (lateBinding.containsKey(property)))
            return (String) lateBinding.get(property);
        if ((nonPrivateSet.contains(property)) &&
                (earlyBinding.containsKey(property)))
            return (String) earlyBinding.get(property);
        if (nonPrivateSet.contains(property)) {
            return " Property " + property + "does not exist ";
        }


        if (lateBinding.containsKey(property)) {
            return (String) lateBinding.get(property);
        }
        return " Property " + property + "does not exist ";
    }


    Map buildRequest()
            throws TransactionException {
        Map request = new HashMap();


        Set set = earlyBinding.keySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Object element = iterator.next();
            if ((!privateSet.contains(element)) &&
                    (!lateBinding.containsKey(element)))
                request.put(element, earlyBinding.get(element));
        }
        set = lateBinding.keySet();
        iterator = set.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            request.put(element, lateBinding.get(element));
        }
        return request;
    }


    String sendRequest(String xml)
            throws TransactionException, Exception {
        String response = null;
        Connection con = null;
        String trustedKeystore = null;
        String trustedKeystorePassword = null;
        int connectionTimeout = 0;

        if (!earlyBinding.containsKey("ConnectionAddress")) {
            throw new TransactionException("Error: 9001.ConnectionAddress is not set.\n");
        }
        String connectionAddress =
                (String) earlyBinding.get("ConnectionAddress");

        if (!earlyBinding.containsKey("ConnectionPort")) {
            throw new TransactionException("Error: 9002.ConnectionPort is not set.\n");
        }
        int connectionPort = Integer.parseInt(
                (String) earlyBinding.get("ConnectionPort"));

        if (!earlyBinding.containsKey("ConnectionSecure")) {
            throw new TransactionException("Error: 9003.ConnectionSecure is not set.\n");
        }


        if (Boolean.valueOf((String) earlyBinding.get("ConnectionSecure")).booleanValue()) {
            if (!earlyBinding.containsKey("MerchantKeystore"))
                throw new TransactionException(
                        "MerchantKeystore is not set.\n");
            String merchantKeystore =
                    (String) earlyBinding.get("MerchantKeystore");

            if (!earlyBinding.containsKey("MerchantKeystorePassword"))
                throw new TransactionException(
                        "MerchantKeystorePassword is not set.\n");
            String merchantKeystorePassword =
                    (String) earlyBinding.get("MerchantKeystorePassword");

            if (!earlyBinding.containsKey("TrustedKeystore")) {
                trustedKeystore = new String(System.getProperty("java.home") +
                        System.getProperty("file.separator") + "lib" +
                        System.getProperty("file.separator") + "security" +
                        System.getProperty("file.separator") + "cacerts");
            } else {
                trustedKeystore = (String) earlyBinding.get("TrustedKeystore");
            }
            if (!earlyBinding.containsKey("TrustedKeystorePassword")) {
                trustedKeystorePassword = null;
            } else {
                trustedKeystorePassword =
                        (String) earlyBinding.get("TrustedKeystorePassword");
            }
            if (!earlyBinding.containsKey("ConnectionTimeout")) {
                connectionTimeout = 3000;
            } else {
                connectionTimeout = Integer.parseInt(
                        (String) earlyBinding.get("ConnectionTimeout"));
            }
            con = new SSLConnection();
            try {
                boolean IOExceptionOccurred = false;
                int totalRetriesPerformed = 0;
                for (; ; ) {
                    boolean continueLoop = false;
                    try {
                        ((SSLConnection) con).connect(connectionAddress, connectionPort,
                                connectionTimeout, merchantKeystore, merchantKeystorePassword, trustedKeystore, trustedKeystorePassword, tlsVersion);

                    } catch (IOException ioExp) {

                        continueLoop = false;
                        System.out.println("Exception occured while making connection. Checking for retry configurations");

                        if ((NumOfRetries > 0) && (totalRetriesPerformed < NumOfRetries)) {
                            totalRetriesPerformed++;
                            System.out.println("Retry # " + totalRetriesPerformed);

                            try {
                                int totalMiliSeconds = MilliSecondsDelayInRetries;
                                Thread.sleep(totalMiliSeconds);
                            } catch (Exception localException1) {
                            }


                            continueLoop = true;
                        }
                    }
                    if (!continueLoop) {
                        lateBinding.put("ResponseCode", "9111");
                        lateBinding.put("ResponseClass", "9000");
                        lateBinding.put("ResponseDescription", "Creating connection error: ");
                        lateBinding.put("ResponseMessage", "Error while creating connection for sending request to server");
                        lateBinding.put("ResponseClassDescription", "Connectivity Problem.");

                        IOExceptionOccurred = true;
                    }
                }

            } catch (UnknownHostException cException) {
                lateBinding.put("ResponseCode", "9101");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription", "Host error: " +
                        cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (IOException cException) {
                String message = cException.getMessage();
                String errorCode = "9102";

                if (message.startsWith("<ErrorCode:911")) {
                    errorCode = message.substring(0, message.indexOf('>'));
                    message = message.replaceAll(errorCode, "");
                    errorCode = errorCode.replaceAll("<ErrorCode:", "");
                    errorCode = errorCode.replaceAll(">", "");
                }

                lateBinding.put("ResponseCode", errorCode);
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "Processing request error: " + cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (KeyStoreException cException) {
                lateBinding.put("ResponseCode", "9103");
                lateBinding.put("ResponseClass", "9000");
                lateBinding
                        .put("ResponseDescription",
                                "SSL Security Keystore error: " +
                                        cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (NoSuchAlgorithmException cException) {
                lateBinding.put("ResponseCode", "9104");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put(
                        "ResponseDescription",
                        "SSL encryption/decryption error: " +
                                cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (NoSuchProviderException cException) {
                lateBinding.put("ResponseCode", "9105");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "SSL Implementation error: " + cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (CertificateException cException) {
                lateBinding.put("ResponseCode", "9106");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "SSL Certificate error: " + cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (UnrecoverableKeyException cException) {
                lateBinding.put("ResponseCode", "9107");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "SSL security key error: " + cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (KeyManagementException cException) {
                lateBinding.put("ResponseCode", "9108");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "SSL security key error: " + cException.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (Exception e) {
                lateBinding.put("ResponseCode", "9109");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "General error in execute " + e.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
                e.printStackTrace();
            }
        } else {
            con = new HTTPConnection();
            try {
                ((HTTPConnection) con).connect(connectionAddress,
                        connectionPort);
                response = con.send(xml);
            } catch (MalformedURLException e) {
                lateBinding.put("ResponseCode", "9201");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put("ResponseDescription",
                        "URL error: " + e.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (IOException e) {
                lateBinding.put("ResponseCode", "9202");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put(
                        "ResponseDescription",
                        "Processing error while sending request: " +
                                e.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            } catch (Exception e) {
                lateBinding.put("ResponseCode", "9203");
                lateBinding.put("ResponseClass", "9000");
                lateBinding.put(
                        "ResponseDescription",
                        "General error while sending request to server: " +
                                e.toString());
                lateBinding.put("ResponseMessage",
                        "Error while sending request to server");
                lateBinding.put("ResponseClassDescription",
                        "Connectivity Problem.");
            }
        }
        label2000:
        con.disconnect();
        return response;
    }
}
