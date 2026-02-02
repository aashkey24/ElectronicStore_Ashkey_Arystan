package com.store.controller;

import com.store.model.Bill;
import com.store.model.Product;
import com.store.model.User;
import com.store.view.CashierPane;
import com.store.util.IOHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CashierController {
    private CashierPane view;
    private User currentUser;
    private ObservableList<Product> allProducts;
    private ObservableList<Product> cart;
    private final String PRODUCTS_FILE = "products.dat";

    public CashierController(CashierPane view, User user) {
        this.view = view;
        this.currentUser = user;
        this.allProducts = FXCollections.observableArrayList(IOHandler.loadList(PRODUCTS_FILE));
        this.cart = FXCollections.observableArrayList();

        view.getProductsTable().setItems(allProducts);
        view.getCartTable().setItems(cart);

        loadTodayHistory();
        attachEvents();
    }

    private void attachEvents() {
        view.getBtnAddToCart().setOnAction(e -> addToCart());
        view.getBtnRemoveFromCart().setOnAction(e -> removeFromCart());
        view.getBtnCheckout().setOnAction(e -> checkout());

        view.getTfSearch().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                view.getProductsTable().setItems(allProducts);
            } else {
                ObservableList<Product> filtered = allProducts.stream()
                        .filter(p -> p.getName().toLowerCase().contains(newVal.toLowerCase()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                view.getProductsTable().setItems(filtered);
            }
        });
    }

    private void addToCart() {
        Product selected = view.getProductsTable().getSelectionModel().getSelectedItem();
        String qtyText = view.getTfQuantity().getText();

        if (selected == null || qtyText.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) return;

            if (qty > selected.getStockQuantity()) {
                showAlert("Stock Error", "Available: " + selected.getStockQuantity());
                return;
            }

            // ПРИМЕНЕНИЕ СКИДКИ: Берем цену со скидкой
            double finalPrice = selected.getDiscountedPrice();

            // Ищем в корзине
            boolean exists = false;
            for (Product p : cart) {
                if (p.getName().equals(selected.getName())) {
                    if (p.getStockQuantity() + qty > selected.getStockQuantity()) {
                        showAlert("Error", "Exceeds stock!"); return;
                    }
                    p.setStockQuantity(p.getStockQuantity() + qty);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                // Создаем копию для корзины с ПРИМЕНЕННОЙ ценой
                Product itemForCart = new Product(selected.getName(), selected.getCategory(),
                        selected.getSupplier(), selected.getPurchasePrice(),
                        finalPrice, qty);
                cart.add(itemForCart);
            }

            view.getCartTable().refresh();
            updateTotal();
            view.getTfQuantity().clear();

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid quantity.");
        }
    }

    private void checkout() {
        if (cart.isEmpty()) return;

        // 1. Уменьшаем сток
        for (Product cartItem : cart) {
            for (Product stockItem : allProducts) {
                if (stockItem.getName().equals(cartItem.getName())) {
                    stockItem.setStockQuantity(stockItem.getStockQuantity() - cartItem.getStockQuantity());
                    break;
                }
            }
        }

        // 2. Сохраняем базу
        IOHandler.saveList(PRODUCTS_FILE, new ArrayList<>(allProducts));

        // 3. Печатаем чек (используя класс Bill)
        printBill();

        // 4. Очистка
        cart.clear();
        updateTotal();
        view.getProductsTable().refresh();
        loadTodayHistory();
        showAlert("Success", "Transaction Completed!");
    }

    private void printBill() {
        double total = cart.stream().mapToDouble(p -> p.getSellingPrice() * p.getStockQuantity()).sum();

        // Используем модель Bill для форматирования
        Bill billObj = new Bill(new ArrayList<>(cart), currentUser, total);

        try (PrintWriter writer = new PrintWriter(new FileWriter(billObj.getFileName()))) {
            writer.print(billObj.getFormattedBill());
        } catch (IOException e) {
            showAlert("Error", "Bill printing failed.");
        }
    }

    private void loadTodayHistory() {
        File folder = new File(".");
        File[] files = folder.listFiles();
        ObservableList<String> history = FXCollections.observableArrayList();
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith("Bill_" + today) && f.getName().endsWith(".txt")) {
                    if (isMine(f)) {
                        history.add(f.getName() + " | " + extractTotal(f) + " $");
                    }
                }
            }
        }
        view.getHistoryList().setItems(history);
        view.getLblHistoryTotal().setText("Today's Bills: " + history.size());
    }

    private boolean isMine(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) if (sc.nextLine().contains("Cashier: " + currentUser.getFullName())) return true;
        } catch (Exception e) {} return false;
    }

    private String extractTotal(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String l = sc.nextLine();
                if (l.startsWith("TOTAL AMOUNT:")) return l.replaceAll("[^0-9.]", "");
            }
        } catch (Exception e) {} return "0.00";
    }

    private void removeFromCart() {
        Product selected = view.getCartTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            cart.remove(selected);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(p -> p.getSellingPrice() * p.getStockQuantity()).sum();
        view.getLblTotal().setText("Total: " + String.format("%.2f", total) + " $");
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.INFORMATION, content).showAndWait();
    }
}