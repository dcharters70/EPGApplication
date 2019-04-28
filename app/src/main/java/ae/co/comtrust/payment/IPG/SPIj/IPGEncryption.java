package ae.co.comtrust.payment.IPG.SPIj;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class IPGEncryption
{
  private String passPhrase = "Etisalat Payment Gateway";
  
  public IPGEncryption() {}
  
  public String encrypt(String plainText) throws TransactionException {

      try {
          MessageDigest digest = MessageDigest.getInstance("SHA");
      digest.update(passPhrase.getBytes());
      SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
      
      Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
      aes.init(1, key);
      byte[] ciphertext = aes.doFinal(plainText.getBytes());
      
      return new BASE64Encoder().encode(ciphertext);
    } catch (Exception e) {
      throw new TransactionException("Error: 9102. " + e.getMessage());
    }
  }
  
  String decrypt(String cypherText) {
      String output = "";
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA");
      digest.update(passPhrase.getBytes());
      SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
      
      Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
      aes.init(2, key);
      
      byte[] encString = new BASE64Decoder().decodeBuffer(cypherText);
      output = new String(aes.doFinal(encString),"UTF-8");
    } catch (Exception e) {
      //throw new TransactionException("Error: 9102. " + e.getMessage());
        e.printStackTrace();
    }
      return output;
  }
}
