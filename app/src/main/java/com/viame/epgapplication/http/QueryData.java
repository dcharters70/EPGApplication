package com.viame.epgapplication.http;

import ae.co.comtrust.payment.IPG.SPIj.Transaction;

public class QueryData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String propFile = null; // This will contain the path of your spi.properties file.
	    propFile = new String(
	    System.getProperty("java.home")
	    + System.getProperty("file.separator")
	    + "lib"
	    + System.getProperty("file.separator")
	    + "raw/spi.properties");
	   
	    
	    try {
			 
	    	
		    Transaction transaction = new Transaction(propFile);  // This will create the transaction object by passing the properties file location.
		    transaction.initialize("QueryData","1.0"); // This will initiate the transaction object with QueryData call.
		    transaction.setProperty("Name", "QueryTransactionDetailsByOrderID");
		    transaction.setProperty("From", "0");
		    transaction.setProperty("To", "99");
		    transaction.setProperty("Store", "0000"); // Will be provided by EPG Support team. Configurable
		    transaction.setProperty("Terminal", "0000"); // Will be provided by EPG Support team. Configurable
		    transaction.setProperty("OrderID", "1234"); // Please provide the Order ID
		    
		    //transaction.setProperty("Customer", "Demo Merchant");
	    
		    transaction.execute(); 
		    boolean isAuthorizeSuccess =false;
		    boolean isCaptureSuccess =false;
		    
		    if (transaction.getResponseCode() == 0){
		    	if(null!=transaction.getProperty("RowsCount")){			    		
		    		int recordCount = Integer.parseInt(transaction.getProperty("RowsCount"));
		    		// If the Order ID is unique then only one Record will be available
		    		for(int i=0;i<recordCount;i++){			    		
		    			if(transaction.getProperty("R"+Integer.toString(i)+"/RequestType").equals("CC Authorization") && transaction.getProperty("R"+Integer.toString(i)+"/ResponseCode").equals("00") ){
		    					// then consider this transaction as successful
		    					isAuthorizeSuccess = true;			    								    				
		    			    	System.out.println("TransactionID "+transaction.getProperty("R"+Integer.toString(i)+"/TransactionID"));
		    			    	System.out.println("Amount "+transaction.getProperty("R"+Integer.toString(i)+"/Amount"));
		    			    	System.out.println("Date "+transaction.getProperty("R"+Integer.toString(i)+"/Date"));
		    			    	System.out.println("ApprovalCode "+transaction.getProperty("R"+Integer.toString(i)+"/ApprovalCode"));
		    			    	//break;			    				
		    			}
		    			
		    		}
		    	}
		    	
		    	
		    	
		    }
		    else{
		    	System.out.println("Failed "+transaction.getResponseDescription());
		    } 
		    
		    
		    
		    
	    } catch (Exception e) {
	        //throw new IllegalStateException("Could not add file handler.", e);
	    	e.printStackTrace();
	    }
	}

}
