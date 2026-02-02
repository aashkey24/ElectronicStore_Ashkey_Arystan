package com.store.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name, category, supplier;
    private double purchasePrice, sellingPrice, discount;
    private int stockQuantity;
    private LocalDate purchaseDate;

    public Product(String name, String category, String supplier, double purchasePrice, double sellingPrice, int stockQuantity) {
        this.name = name;
        this.category = category;
        this.supplier = supplier;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.discount = 0.0;
        this.purchaseDate = LocalDate.now();
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSupplier() { return supplier; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getSellingPrice() { return sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public double getDiscount() { return discount; }

    // Setters
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public void setCategory(String category) { this.category = category; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public double getDiscountedPrice() {
        return sellingPrice * (1 - (discount / 100.0));
    }
}