package ae.co.comtrust.payment.IPG.SPIj;

import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface RemoteTransaction
  extends Remote
{
  public abstract void execute()
    throws TransactionException, RemoteException;
  
  public abstract String getConnectionAddress()
    throws TransactionException, RemoteException;
  
  public abstract int getConnectionPort()
    throws TransactionException, RemoteException;
  
  public abstract boolean getConnectionSecure()
    throws TransactionException, RemoteException;
  
  public abstract int getConnectionTimeout()
    throws TransactionException, RemoteException;
  
  public abstract String getCustomer()
    throws TransactionException, RemoteException;
  
  public abstract int getLanguage()
    throws TransactionException, RemoteException;
  
  public abstract String getOrderIDFormat()
    throws TransactionException, RemoteException;
  
  public abstract String getMerchantKeystorePassword()
    throws TransactionException, RemoteException;
  
  public abstract String getMerchantKeystore()
    throws TransactionException, RemoteException;
  
  public abstract String getProperty(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract int getResponseClass()
    throws TransactionException, RemoteException;
  
  public abstract int getResponseCode()
    throws TransactionException, RemoteException;
  
  public abstract String getResponseDescription()
    throws TransactionException, RemoteException;
  
  public abstract String getResponseMessage()
    throws TransactionException, RemoteException;
  
  public abstract String getStore()
    throws TransactionException, RemoteException;
  
  public abstract String getTerminal()
    throws TransactionException, RemoteException;
  
  public abstract String getTrustedKeystore()
    throws TransactionException, RemoteException;
  
  public abstract String getTrustedKeystorePassword()
    throws TransactionException, RemoteException;
  
  public abstract void initialize(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void initialize(String paramString1, String paramString2)
    throws TransactionException, RemoteException;
  
  public abstract void setConnectionAddress(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setConnectionPort(int paramInt)
    throws TransactionException, RemoteException;
  
  public abstract void setConnectionSecure(boolean paramBoolean)
    throws TransactionException, RemoteException;
  
  public abstract void setConnectionTimeout(int paramInt)
    throws TransactionException, RemoteException;
  
  public abstract void setCustomer(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setLanguage(int paramInt)
    throws TransactionException, RemoteException;
  
  public abstract void setOrderIDFormat(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setMerchantKeystore(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setMerchantKeystorePassword(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setProperty(String paramString1, String paramString2)
    throws TransactionException, RemoteException;
  
  public abstract void setStore(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setTerminal(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setTrustedKeystore(String paramString)
    throws TransactionException, RemoteException;
  
  public abstract void setTrustedKeystorePassword(String paramString)
    throws TransactionException, RemoteException;
}
