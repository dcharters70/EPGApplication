package ae.co.comtrust.payment.IPG.SPIj;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class RemoteTransactionImpl
        extends UnicastRemoteObject
        implements RemoteTransaction {
    Transaction transaction;

    public RemoteTransactionImpl()
            throws RemoteException, TransactionException, IOException {
        transaction = new Transaction();
    }

    public RemoteTransactionImpl(String configFilePath)
            throws RemoteException, IOException, TransactionException {
        transaction = new Transaction(configFilePath);
    }

    public RemoteTransactionImpl(int arg0)
            throws RemoteException, TransactionException, IOException {
        super(arg0);
        transaction = new Transaction();
    }

    public RemoteTransactionImpl(int arg0, RMIClientSocketFactory arg1, RMIServerSocketFactory arg2)
            throws RemoteException, TransactionException, IOException {
        super(arg0, arg1, arg2);
        transaction = new Transaction();
    }

    public void execute()
            throws TransactionException, RemoteException {
        transaction.execute();
    }

    public String getConnectionAddress()
            throws TransactionException, RemoteException {
        return transaction.getConnectionAddress();
    }

    public int getConnectionPort()
            throws TransactionException, RemoteException {
        return transaction.getConnectionPort();
    }

    public boolean getConnectionSecure()
            throws TransactionException, RemoteException {
        return transaction.getConnectionSecure();
    }

    public int getConnectionTimeout()
            throws TransactionException, RemoteException {
        return transaction.getConnectionTimeout();
    }

    public String getCustomer()
            throws TransactionException, RemoteException {
        return transaction.getCustomer();
    }

    public int getLanguage()
            throws TransactionException, RemoteException {
        return transaction.getLanguage();
    }

    public String getOrderIDFormat()
            throws TransactionException, RemoteException {
        return transaction.getOrderIDFormat();
    }

    public String getMerchantKeystorePassword()
            throws TransactionException, RemoteException {
        return transaction.getMerchantKeystorePassword();
    }

    public String getMerchantKeystore()
            throws TransactionException, RemoteException {
        return transaction.getMerchantKeystore();
    }

    public String getProperty(String propertyName)
            throws TransactionException, RemoteException {
        return transaction.getProperty(propertyName);
    }

    public int getResponseClass()
            throws TransactionException, RemoteException {
        return transaction.getResponseClass();
    }

    public int getResponseCode()
            throws TransactionException, RemoteException {
        return transaction.getResponseCode();
    }

    public String getResponseDescription()
            throws TransactionException, RemoteException {
        return transaction.getResponseDescription();
    }

    public String getResponseMessage()
            throws TransactionException, RemoteException {
        return transaction.getResponseMessage();
    }

    public String getStore()
            throws TransactionException, RemoteException {
        return transaction.getStore();
    }

    public String getTerminal()
            throws TransactionException, RemoteException {
        return transaction.getTerminal();
    }

    public String getTrustedKeystore()
            throws TransactionException, RemoteException {
        return transaction.getTrustedKeystore();
    }

    public String getTrustedKeystorePassword()
            throws TransactionException, RemoteException {
        return transaction.getTrustedKeystorePassword();
    }

    public void initialize(String transactionType)
            throws TransactionException, RemoteException {
        transaction.initialize(transactionType);
    }

    public void initialize(String transactionType, String version)
            throws TransactionException, RemoteException {
        transaction.initialize(transactionType, version);
    }

    public void setConnectionAddress(String dnsName)
            throws TransactionException, RemoteException {
        transaction.setConnectionAddress(dnsName);
    }

    public void setConnectionPort(int port)
            throws TransactionException, RemoteException {
        transaction.setConnectionPort(port);
    }

    public void setConnectionSecure(boolean secure)
            throws TransactionException, RemoteException {
        transaction.setConnectionSecure(secure);
    }

    public void setConnectionTimeout(int timeout)
            throws TransactionException, RemoteException {
        transaction.setConnectionTimeout(timeout);
    }

    public void setCustomer(String customer)
            throws TransactionException, RemoteException {
        transaction.setCustomer(customer);
    }

    public void setLanguage(int language)
            throws TransactionException, RemoteException {
        transaction.setLanguage(language);
    }

    public void setOrderIDFormat(String orderIDFormat)
            throws TransactionException, RemoteException {
        transaction.setOrderIDFormat(orderIDFormat);
    }

    public void setMerchantKeystore(String storeName)
            throws TransactionException, RemoteException {
        transaction.setMerchantKeystore(storeName);
    }

    public void setMerchantKeystorePassword(String password)
            throws TransactionException, RemoteException {
        transaction.setMerchantKeystorePassword(password);
    }

    public void setProperty(String propertyName, String propertyValue)
            throws TransactionException, RemoteException {
        transaction.setProperty(propertyName, propertyValue);
    }

    public void setStore(String store)
            throws TransactionException, RemoteException {
        transaction.setStore(store);
    }

    public void setTerminal(String terminal)
            throws TransactionException, RemoteException {
        transaction.setTerminal(terminal);
    }

    public void setTrustedKeystore(String storeName)
            throws TransactionException, RemoteException {
        transaction.setTrustedKeystore(storeName);
    }

    public void setTrustedKeystorePassword(String password)
            throws TransactionException, RemoteException {
        transaction.setTrustedKeystorePassword(password);
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        try {
            RemoteTransactionImpl obj = new RemoteTransactionImpl();

            Naming.rebind("RemoteTransactionServer", obj);
            System.out.println("RemoteTransactionServer bound in registry");
        } catch (Exception e) {
            System.out.println("RemoteTransactionImpl err: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
