package ae.co.comtrust.payment.IPG.SPIj;

public abstract class Connection
{
  public Connection() {}
  
  public abstract String send(String paramString)
    throws Exception;
  
  public abstract void disconnect()
    throws Exception;
}
