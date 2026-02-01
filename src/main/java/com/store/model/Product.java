package com.store.model;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String category; // Добавили поле
    private double price;
    private int stockQuantity;

    // Обновленный конструктор принимает 4 параметра
    public Product(String name, String category, double price, int stockQuantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getName() { return name; }

    // Геттер для категории (обязателен для TableView)
    public String getCategory() { return category; }

    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        // Обновили toString, чтобы он показывал и категорию
        return String.format("%s (%s) | $%.2f | Qty: %d", name, category, price, stockQuantity);
    }
}