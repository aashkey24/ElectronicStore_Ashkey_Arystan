package com.store.model;

import java.io.Serializable;
import java.util.Date;

// implements Serializable ОБЯЗАТЕЛЬНО, чтобы сохранять в sales.dat
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billId;
    private String cashierName;
    private String productName;
    private int quantity;
    private double totalAmount; // Вот это поле нам нужно!
    private Date date;

    public Bill(String cashierName, String productName, int quantity, double totalAmount) {
        // Генерируем уникальный ID чека (например, по времени)
        this.billId = String.valueOf(System.currentTimeMillis());
        this.cashierName = cashierName;
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.date = new Date();
    }

    // --- ВОТ ЭТОГО МЕТОДА НЕ ХВАТАЛО ---
    public double getTotalAmount() {
        return totalAmount;
    }
    // ------------------------------------

    public String getBillId() { return billId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public Date getDate() { return date; }

    // Метод для печати красивого чека (для Абдулазиза)
    public String getReceiptContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ELECTRONIC STORE RECEIPT =====\n");
        sb.append("Bill ID: ").append(billId).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Cashier: ").append(cashierName).append("\n");
        sb.append("------------------------------------\n");
        sb.append("ITEM: ").append(productName).append("\n");
        sb.append("QTY:  ").append(quantity).append("\n");
        sb.append("TOTAL: $").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("====================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Bill#" + billId + " ($" + totalAmount + ")";
    }
}