package com.viame.epgapplication.http;

import ae.co.comtrust.payment.IPG.SPIj.*;

public class FullReversalTest
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
		transaction.initialize("Reversal","1.0");
		transaction.setProperty("Customer", "Demo Merchant");
		// The following must be replaced with the TransactionID of a previously
		// authorized transaction
		transaction.setProperty("TransactionID","115649796038");
		
	  	transaction.execute();
	  	System.out.println("ResponseCode is "+transaction.getResponseCode());
	  	System.out.println("ResponseDescription is "+transaction.getResponseDescription());
 
	} catch (Exception e) {
		System.out.println(e.toString());	
		return;
	}

}

}