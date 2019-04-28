package com.viame.epgapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("PaymentPortal")
    @Expose
    private String paymentPortal;
    @SerializedName("PaymentPage")
    @Expose
    private String paymentPage;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseClass")
    @Expose
    private String responseClass;
    @SerializedName("ResponseDescription")
    @Expose
    private String responseDescription;
    @SerializedName("ResponseClassDescription")
    @Expose
    private String responseClassDescription;
    @SerializedName("TransactionID")
    @Expose
    private String transactionID;
    @SerializedName("Balance")
    @Expose
    private Balance balance;
    @SerializedName("Amount")
    @Expose
    private Amount amount;
    @SerializedName("Fees")
    @Expose
    private Fees fees;
    @SerializedName("Payer")
    @Expose
    private Object payer;
    @SerializedName("UniqueID")
    @Expose
    private String uniqueID;

    public String getPaymentPortal() {
        return paymentPortal;
    }

    public void setPaymentPortal(String paymentPortal) {
        this.paymentPortal = paymentPortal;
    }

    public String getPaymentPage() {
        return paymentPage;
    }

    public void setPaymentPage(String paymentPage) {
        this.paymentPage = paymentPage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(String responseClass) {
        this.responseClass = responseClass;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getResponseClassDescription() {
        return responseClassDescription;
    }

    public void setResponseClassDescription(String responseClassDescription) {
        this.responseClassDescription = responseClassDescription;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Fees getFees() {
        return fees;
    }

    public void setFees(Fees fees) {
        this.fees = fees;
    }

    public Object getPayer() {
        return payer;
    }

    public void setPayer(Object payer) {
        this.payer = payer;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

}
