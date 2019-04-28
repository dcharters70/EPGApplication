package com.viame.epgapplication.http;

import ae.co.comtrust.payment.IPG.SPIj.Transaction;

public class AuthorizationTest
{
 public static final void main(String[] aArgs){

	 String sAssets = "/sdcard/spi.properties";
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
        propFile = sAssets;
		Transaction transaction = new Transaction(propFile);
		transaction.initialize("Authorization","1.0");
		transaction.setProperty("Customer", "Demo Merchant");
		transaction.setProperty("Amount", "12.23");
		transaction.setProperty("Currency", "AED");
		transaction.setProperty("CardNumber", "999000000000011");
		transaction.setProperty("ExpiryDate", "2012-12");
		//transaction.setProperty("OrderName", ""+request.getParameter("OrderName"));
		//transaction.setProperty("OrderInfo", ""+request.getParameter("OrderInfo"));
		//transaction.setProperty("OrderID",""+request.getParameter("OrderID"));
		transaction.setProperty("TransactionHint", "CPT:Y");
		
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
