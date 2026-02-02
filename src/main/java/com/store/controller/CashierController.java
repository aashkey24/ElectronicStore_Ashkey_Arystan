package com.store.controller;

import com.store.model.Product;
import com.store.model.User;
import com.store.model.Bill;
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

        // LOAD USING IOHANDLER
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

    private void loadTodayHistory() {
        File folder = new File(".");
        File[] files = folder.listFiles();
        ObservableList<String> history = FXCollections.observableArrayList();
        int count = 0;

        if (files != null) {
            String todayStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            for (File f : files) {
                if (f.getName().startsWith("Bill_" + todayStr) && f.getName().endsWith(".txt")) {
                    if (isCreatedByCurrentCashier(f)) {
                        double amount = extractTotal(f);
                        history.add(f.getName() + " | " + String.format("%.2f $", amount));
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
                if (sc.nextLine().contains("Cashier: " + currentUser.getFullName())) return true;
            }
        } catch (Exception e) { return false; }
        return false;
    }

    private double extractTotal(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("TOTAL AMOUNT:")) {
                    return Double.parseDouble(line.replace("TOTAL AMOUNT:", "").replace("$", "").trim().replace(",", "."));
                }
            }
        } catch (Exception e) {}
        return 0.0;
    }

    private void addToCart() {
        Product selected = view.getProductsTable().getSelectionModel().getSelectedItem();
        String qtyText = view.getTfQuantity().getText();
        if (selected == null || qtyText.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) return;
            if (qty > selected.getStockQuantity()) {
                showAlert("Stock Error", "Not enough stock!");
                return;
            }

            boolean found = false;
            for(Product p : cart) {
                if(p.getName().equals(selected.getName())) {
                    if (p.getStockQuantity() + qty > selected.getStockQuantity()) {
                        showAlert("Error", "Exceeds stock!");
                        return;
                    }
                    p.setStockQuantity(p.getStockQuantity() + qty);
                    found = true;
                    view.getCartTable().refresh();
                    break;
                }
            }
            if (!found) {
                cart.add(new Product(selected.getName(), selected.getCategory(), selected.getSupplier(),
                        selected.getPurchasePrice(), selected.getSellingPrice(), qty));
            }
            updateTotal();
        } catch (NumberFormatException e) { showAlert("Error", "Invalid quantity."); }
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
        for (Product cartItem : cart) {
            for (Product stockItem : allProducts) {
                if (stockItem.getName().equals(cartItem.getName())) {
                    stockItem.setStockQuantity(stockItem.getStockQuantity() - cartItem.getStockQuantity());
                    break;
                }
            }
        }

        // SAVE USING IOHANDLER
        IOHandler.saveList(PRODUCTS_FILE, new ArrayList<>(allProducts));

        printBill();
        cart.clear();
        updateTotal();
        view.getProductsTable().refresh();
        loadTodayHistory();
        showAlert("Success", "Bill printed!");
    }

    private void printBill() {
        double total = cart.stream().mapToDouble(p -> p.getSellingPrice() * p.getStockQuantity()).sum();

        // creating object bill
        Bill billObject = new Bill(new ArrayList<>(cart), currentUser, total);

        // print using method in bill class
        try (PrintWriter writer = new PrintWriter(new FileWriter(billObject.getFileName()))) {
            writer.print(billObject.getFormattedBill());
        } catch (IOException e) {
            showAlert("Error", "Print failed: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}