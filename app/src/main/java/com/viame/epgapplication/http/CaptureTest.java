package com.viame.epgapplication.http;

import ae.co.comtrust.payment.IPG.SPIj.*;

public class CaptureTest
{
 public static final void main(String[] aArgs){

	String propFile = null;
	if (aArgs.length == 0) {
		propFile = 	new String(System.getProperty("java.home")
				+ System.getProperty("file.separator")
				+ "lib"
				+ System.getProperty("file.separator")
				+ "raw/spi.properties");
				System.out.println(propFile);
	} else 
		propFile = aArgs[0];
	try {
		Transaction transaction = new Transaction(propFile);
		transaction.initialize("Capture","1.0");
		transaction.setProperty("Customer", "Demo Merchant");
		transaction.setProperty("Amount", "12.23");
		transaction.setProperty("Currency", "AED");
		
		//-- replace the value mentioned here with the one returned in reponse to the 
		//-- Authorization request
		transaction.setProperty("TransactionID", "110870476064");
		
	  	transaction.execute();
	  	System.out.println("ResponseCode is "+transaction.getResponseCode());
	  	System.out.println("ResponseDescription is "+transaction.getResponseDescription());
      	if (transaction.getResponseCode() == 0) {
      			  	System.out.println("Balance: "+
      			  		transaction.getProperty("Balance"));
      	}
	} catch (Exception e) {
		System.out.println(e.toString());	
		return;
	}

}

}