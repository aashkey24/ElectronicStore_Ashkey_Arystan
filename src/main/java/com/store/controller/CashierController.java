package com.store.controller;

import com.store.model.Product;
import com.store.model.User;
import com.store.view.CashierPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.*;
import java.time.LocalDate;
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
        this.allProducts = FXCollections.observableArrayList();
        this.cart = FXCollections.observableArrayList();

        loadProducts();

        view.getProductsTable().setItems(allProducts);
        view.getCartTable().setItems(cart);

        loadTodayHistory(); // <-- ЗАГРУЗКА ИСТОРИИ
        attachEvents();
    }

    private void attachEvents() {
        view.getBtnAddToCart().setOnAction(e -> addToCart());
        view.getBtnRemoveFromCart().setOnAction(e -> removeFromCart());
        view.getBtnCheckout().setOnAction(e -> checkout());

        // Поиск
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

    // --- ЛОГИКА ИСТОРИИ (ВАЖНО!) ---
    private void loadTodayHistory() {
        File folder = new File(".");
        File[] files = folder.listFiles();
        ObservableList<String> history = FXCollections.observableArrayList();
        int count = 0;

        if (files != null) {
            String todayStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            for (File f : files) {
                // Ищем файлы Bill_YYYYMMDD_...
                if (f.getName().startsWith("Bill_" + todayStr) && f.getName().endsWith(".txt")) {
                    // Проверяем, кто создал чек (читаем 2-ю строку файла)
                    if (isCreatedByCurrentCashier(f)) {
                        double amount = extractTotal(f);
                        history.add(f.getName() + " | Total: " + String.format("%.2f", amount) + " $");
                        count++;
                    }
                }
            }
        }
        view.getHistoryList().setItems(history);
        view.getLblHistoryTotal().setText("Bills Generated Today: " + count);
    }

    private boolean isCreatedByCurrentCashier(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                // В методе printBill мы пишем "Cashier: [Name]"
                if (line.contains("Cashier: " + currentUser.getFullName())) {
                    return true;
                }
            }
        } catch (Exception e) { return false; }
        return false;
    }

    private double extractTotal(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("TOTAL AMOUNT:")) {
                    String s = line.replace("TOTAL AMOUNT:", "").replace("$", "").trim().replace(",", ".");
                    return Double.parseDouble(s);
                }
            }
        } catch (Exception e) {}
        return 0.0;
    }

    // --- ЛОГИКА КОРЗИНЫ ---
    private void addToCart() {
        Product selected = view.getProductsTable().getSelectionModel().getSelectedItem();
        String qtyText = view.getTfQuantity().getText();

        if (selected == null || qtyText.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) return;
            if (qty > selected.getStockQuantity()) {
                showAlert("Stock Error", "Not enough stock! Available: " + selected.getStockQuantity());
                return;
            }

            // Проверяем, есть ли уже в корзине, если да - увеличиваем кол-во
            boolean found = false;
            for(Product p : cart) {
                if(p.getName().equals(selected.getName())) {
                    if (p.getStockQuantity() + qty > selected.getStockQuantity()) {
                        showAlert("Error", "Total quantity exceeds stock!");
                        return;
                    }
                    p.setStockQuantity(p.getStockQuantity() + qty);
                    found = true;
                    view.getCartTable().refresh();
                    break;
                }
            }

            if (!found) {
                Product cartItem = new Product(selected.getName(), selected.getCategory(),
                        selected.getSupplier(), selected.getPurchasePrice(),
                        selected.getSellingPrice(), qty);
                cart.add(cartItem);
            }
            updateTotal();

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid quantity.");
        }
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

    private void checkout() {
        if (cart.isEmpty()) return;

        // Обновляем склад
        for (Product cartItem : cart) {
            for (Product stockItem : allProducts) {
                if (stockItem.getName().equals(cartItem.getName())) {
                    int newStock = stockItem.getStockQuantity() - cartItem.getStockQuantity();
                    stockItem.setStockQuantity(newStock);
                    break;
                }
            }
        }
        saveProducts();
        printBill();

        cart.clear();
        updateTotal();
        view.getProductsTable().refresh();
        loadTodayHistory(); // ОБНОВЛЯЕМ ИСТОРИЮ СРАЗУ
        showAlert("Success", "Bill printed!");
    }

    private void printBill() {
        double total = cart.stream().mapToDouble(p -> p.getSellingPrice() * p.getStockQuantity()).sum();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String filename = "Bill_" + LocalDateTime.now().format(dtf) + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== ELECTRONICS STORE BILL ===");
            writer.println("Cashier: " + currentUser.getFullName()); // Эту строку мы ищем при фильтрации
            writer.println("Date: " + LocalDateTime.now());
            writer.println("--------------------------------");
            writer.printf("%-20s %-10s %-10s%n", "Item", "Qty", "Price");
            writer.println("--------------------------------");
            for (Product p : cart) {
                writer.printf("%-20s %-10d %-10.2f%n", p.getName(), p.getStockQuantity(), (p.getSellingPrice() * p.getStockQuantity()));
            }
            writer.println("--------------------------------");
            writer.printf("TOTAL AMOUNT: %.2f $%n", total);
            writer.println("================================");
        } catch (IOException e) {
            showAlert("Error", "Could not print bill: " + e.getMessage());
        }
    }

    private void saveProducts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCTS_FILE))) {
            oos.writeObject(new ArrayList<>(allProducts));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        File file = new File(PRODUCTS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                allProducts.setAll((ArrayList<Product>) ois.readObject());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}