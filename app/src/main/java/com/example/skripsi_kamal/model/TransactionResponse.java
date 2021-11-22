package com.example.skripsi_kamal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionResponse<T> {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("transactionCode")
    @Expose
    private String transactionCode;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("subTotal")
    @Expose
    private String subTotal;
    @SerializedName("paymentId")
    @Expose
    private Integer paymentId;
    @SerializedName("paymentName")
    @Expose
    private String paymentName;
    @SerializedName("paymentVA")
    @Expose
    private String paymentVA;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("menu")
    @Expose
    private List<T> menu = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentVA() {
        return paymentVA;
    }

    public void setPaymentVA(String paymentVA) {
        this.paymentVA = paymentVA;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<T> getMenu() {
        return menu;
    }

    public void setMenu(List<T> menu) {
        this.menu = menu;
    }
}
