package com.viame.epgapplication.http;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.net.URI;

import ae.co.comtrust.payment.IPG.SPIj.*;

public class RegistrationTest
{
 public static final void main(String[] aArgs){


	try {

		String SPI_FILE_PATH = new File(Environment.getExternalStorageDirectory(),"/spi.properties").getAbsolutePath();

		Transaction transaction = new Transaction(SPI_FILE_PATH);
		transaction.initialize("Registration","1.0");
		transaction.setProperty("Customer", "Demo Merchant");
		transaction.setProperty("Amount", "1213.23");
		transaction.setProperty("OrderName", "Test");
		transaction.setProperty("OrderInfo", "Test - Long description");
		transaction.setProperty("Currency","AED");
		transaction.setProperty("OrderID","TEST{Y}{m}{d}{Od3}");
		transaction.setProperty("TransactionHint", "CPT:N");
		//transaction.setProperty("ExtraData/Account", "Something here");
		//transaction.setProperty("ExtraData/BillingPeriod", "2004/12");
		transaction.setProperty("ReturnPath", "http://localhost:8080/demo_merchant3d/finalize.jsp");

	  	transaction.execute();
	  	System.out.println("ResponseCode is "+transaction.getResponseCode());
	  	System.out.println("ResponseDescription is "+transaction.getResponseDescription());
      	if (transaction.getResponseCode() == 0) {
      			  	System.out.println("TransactionID "+
      			  		transaction.getProperty("TransactionID"));

      	}
	} catch (Exception e) {
		System.out.println(e.toString());	
		return;
	}

}

}