package com.store.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Bill implements Serializable {
    private List<Product> items;
    private User cashier;
    private double totalAmount;
    private LocalDateTime timestamp;

    public Bill(List<Product> items, User cashier, double totalAmount) {
        this.items = items;
        this.cashier = cashier;
        this.totalAmount = totalAmount;
        this.timestamp = LocalDateTime.now();
    }
    public String getFormattedBill() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        sb.append("=== ELECTRONICS STORE BILL ===\n");
        sb.append("Cashier: ").append(cashier.getFullName()).append("\n");
        sb.append("Date: ").append(timestamp.format(dtf)).append("\n");
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %-10s %-10s%n", "Item", "Qty", "Price"));
        sb.append("--------------------------------\n");

        for (Product p : items) {
            double lineTotal = p.getSellingPrice() * p.getStockQuantity();
            sb.append(String.format("%-20s %-10d %-10.2f%n", p.getName(), p.getStockQuantity(), lineTotal));
        }

        sb.append("--------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT: %.2f $%n", totalAmount));
        sb.append("================================\n");

        return sb.toString();
    }

    public String getFileName() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "Bill_" + timestamp.format(dtf) + ".txt";
    }
}