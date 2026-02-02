package com.store.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String category;
    private String supplier;
    private double purchasePrice;
    private double sellingPrice;
    private int stockQuantity;
    private LocalDate purchaseDate;

    public Product(String name, String category, String supplier, double purchasePrice, double sellingPrice, int stockQuantity) {
        this.name = name;
        this.category = category;
        this.supplier = supplier;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.purchaseDate = LocalDate.now();
    }

    // --- GETTERS ---
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSupplier() { return supplier; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getSellingPrice() { return sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public LocalDate getPurchaseDate() { return purchaseDate; }

    // --- SETTERS (НОВЫЕ МЕТОДЫ, КОТОРЫХ НЕ ХВАТАЛО) ---
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    // Имя товара обычно не меняют, но на всякий случай можно добавить
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " (" + stockQuantity + ")";
    }
}