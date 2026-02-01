package com.store.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bill implements Serializable {
    private String billId;
    private String cashierName;
    private String productName;
    private int quantity;
    private double totalPrice;
    private String date;

    public Bill(String cashierName, String productName, int quantity, double totalPrice) {
        this.billId = String.valueOf(System.currentTimeMillis());
        this.cashierName = cashierName;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = LocalDateTime.now().format(dtf);
    }


    public String getReceiptContent() {
        return "================================\n" +
                "      ELECTRONIC STORE RECEIPT  \n" +
                "================================\n" +
                "Bill ID : " + billId + "\n" +
                "Date    : " + date + "\n" +
                "Cashier : " + cashierName + "\n" +
                "--------------------------------\n" +
                "ITEM          QTY      PRICE    \n" +
                String.format("%-12s  %-5d  $%-8.2f\n", productName, quantity, totalPrice) +
                "--------------------------------\n" +
                "GRAND TOTAL:           $" + totalPrice + "\n" +
                "================================";
    }

    public String getBillId() { return billId; }
}