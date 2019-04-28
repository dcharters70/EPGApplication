package com.viame.epgapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Registration {

    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("ReturnPath")
    @Expose
    private String returnPath;
    @SerializedName("TransactionHint")
    @Expose
    private String transactionHint;
    @SerializedName("OrderID")
    @Expose
    private String orderID;
    @SerializedName("Store")
    @Expose
    private String store;
    @SerializedName("Terminal")
    @Expose
    private String terminal;
    @SerializedName("Channel")
    @Expose
    private String channel;
    @SerializedName("Amount")
    @Expose
    private String amount;
    @SerializedName("Customer")
    @Expose
    private String customer;
    @SerializedName("OrderName")
    @Expose
    private String orderName;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Password")
    @Expose
    private String password;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReturnPath() {
        return returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }

    public String getTransactionHint() {
        return transactionHint;
    }

    public void setTransactionHint(String transactionHint) {
        this.transactionHint = transactionHint;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
