package com.store.controller;

import com.store.model.*;
import com.store.view.ManagerView;
import com.store.util.IOHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManagerController {
    private ManagerView view;
    private ObservableList<Product> products;
    private ObservableList<String> suppliers, categories;

    private final String PRODUCTS_FILE = "products.dat";
    private final String USERS_FILE = "users.dat";
    private final String SUPPLIERS_FILE = "suppliers.dat";
    private final String CATEGORIES_FILE = "categories.dat";

    public ManagerController(ManagerView view, User user) {
        this.view = view;
        this.products = FXCollections.observableArrayList(IOHandler.loadList(PRODUCTS_FILE));
        this.suppliers = FXCollections.observableArrayList(IOHandler.loadList(SUPPLIERS_FILE));
        this.categories = FXCollections.observableArrayList(IOHandler.loadList(CATEGORIES_FILE));

        view.getProductTable().setItems(products);
        view.getSupplierList().setItems(suppliers);
        view.getCbSupplier().setItems(suppliers);
        view.getCategoryList().setItems(categories);
        view.getCbCategory().setItems(categories);

        updateInventoryStatus();
        analyzeCashiers();
        attachEvents();
    }

    private void attachEvents() {
        view.getBtnAddProduct().setOnAction(e -> addOrRestock());
        view.getBtnUpdateProduct().setOnAction(e -> updateProduct());
        view.getBtnDeleteProduct().setOnAction(e -> deleteProduct());
        view.getBtnClearForm().setOnAction(e -> clearForm());
        view.getBtnRefreshStats().setOnAction(e -> analyzeCashiers());

        view.getBtnAddSupplier().setOnAction(e -> {
            String s = view.getTfNewSupplier().getText().trim();
            if(!s.isEmpty() && !suppliers.contains(s)) {
                suppliers.add(s); saveLists(); view.getTfNewSupplier().clear();
            }
        });

        view.getBtnAddCategory().setOnAction(e -> {
            String c = view.getTfNewCategory().getText().trim();
            if(!c.isEmpty() && !categories.contains(c)) {
                categories.add(c); saveLists(); view.getTfNewCategory().clear();
            }
        });

        view.getProductTable().getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) fillForm(newVal);
        });
    }

    private void addOrRestock() {
        try {
            String name = view.getTfName().getText().trim();
            String cat = view.getCbCategory().getValue();
            String sup = view.getCbSupplier().getValue();
            int qty = Integer.parseInt(view.getTfQuantity().getText().trim());
            double buy = Double.parseDouble(view.getTfBuyPrice().getText().trim());
            double sell = Double.parseDouble(view.getTfSellPrice().getText().trim());
            double disc = view.getTfDiscount().getText().isEmpty() ? 0 : Double.parseDouble(view.getTfDiscount().getText().trim());

            if (name.isEmpty() || cat == null || sup == null) return;

            for (Product p : products) {
                if (p.getName().equalsIgnoreCase(name)) {
                    p.setStockQuantity(p.getStockQuantity() + qty);
                    p.setPurchasePrice(buy); p.setSellingPrice(sell);
                    p.setDiscount(disc);
                    finishEdit(); return;
                }
            }
            Product p = new Product(name, cat, sup, buy, sell, qty);
            p.setDiscount(disc);
            products.add(p);
            finishEdit();
        } catch (Exception e) { showAlert("Error", "Check input values."); }
    }

    private void analyzeCashiers() {
        ArrayList<User> users = IOHandler.loadList(USERS_FILE);
        ObservableList<ManagerView.CashierMetric> metrics = FXCollections.observableArrayList();
        double totalRev = 0; int totalItems = 0;

        LocalDate start = view.getDpStart().getValue();
        LocalDate end = view.getDpEnd().getValue();

        for (User u : users) {
            if (u.getRole().equals("Cashier")) {
                int count = 0; double rev = 0;
                File[] files = new File(".").listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.getName().startsWith("Bill_") && isDateMatch(f.getName(), start, end)) {
                            if (checkCashierInFile(f, u.getFullName())) {
                                count++;
                                rev += getRevFromFile(f);
                                totalItems += getItemsFromFile(f);
                            }
                        }
                    }
                }
                metrics.add(new ManagerView.CashierMetric(u.getFullName(), count, rev));
                totalRev += rev;
            }
        }
        view.getCashierTable().setItems(metrics);
        view.getLblTotalRevenue().setText("Total Revenue: " + String.format("%.2f", totalRev) + " $");
        view.getLblTotalItems().setText("Items Sold: " + totalItems);
    }

    // Utils
    private boolean isDateMatch(String filename, LocalDate s, LocalDate e) {
        if (s == null && e == null) return true;
        try {
            LocalDate d = LocalDate.parse(filename.substring(5, 13), DateTimeFormatter.ofPattern("yyyyMMdd"));
            return (s == null || !d.isBefore(s)) && (e == null || !d.isAfter(e));
        } catch (Exception ex) { return false; }
    }

    private boolean checkCashierInFile(File f, String name) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) if (sc.nextLine().contains("Cashier: " + name)) return true;
        } catch (Exception e) {} return false;
    }

    private double getRevFromFile(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String l = sc.nextLine();
                if (l.startsWith("TOTAL AMOUNT:")) return Double.parseDouble(l.replaceAll("[^0-9.]", ""));
            }
        } catch (Exception e) {} return 0;
    }

    private int getItemsFromFile(File f) {
        int c = 0;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String l = sc.nextLine();
                if (l.contains("  ") && !l.contains("===") && !l.contains("Item") && !l.contains("TOTAL")) c++;
            }
        } catch (Exception e) {} return c;
    }

    private void updateInventoryStatus() {
        long low = products.stream().filter(p -> p.getStockQuantity() < 5).count();
        if (low > 0) {
            view.getLblAlert().setText("⚠️ Low Stock: " + low + " items");
            view.getLblAlert().setStyle("-fx-padding: 5 15; -fx-background-radius: 15; -fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
        } else {
            view.getLblAlert().setText("✅ Inventory Healthy");
            view.getLblAlert().setStyle("-fx-padding: 5 15; -fx-background-radius: 15; -fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
        }
    }

    private void finishEdit() {
        IOHandler.saveList(PRODUCTS_FILE, new ArrayList<>(products));
        view.getProductTable().refresh();
        clearForm();
        updateInventoryStatus();
    }

    private void saveLists() {
        IOHandler.saveList(SUPPLIERS_FILE, new ArrayList<>(suppliers));
        IOHandler.saveList(CATEGORIES_FILE, new ArrayList<>(categories));
    }

    private void fillForm(Product p) {
        view.getTfName().setText(p.getName());
        view.getCbCategory().setValue(p.getCategory());
        view.getCbSupplier().setValue(p.getSupplier());
        view.getTfQuantity().setText(String.valueOf(p.getStockQuantity()));
        view.getTfBuyPrice().setText(String.valueOf(p.getPurchasePrice()));
        view.getTfSellPrice().setText(String.valueOf(p.getSellingPrice()));
        view.getTfDiscount().setText(String.valueOf(p.getDiscount()));
    }

    private void clearForm() {
        view.getTfName().clear(); view.getTfQuantity().clear();
        view.getTfBuyPrice().clear(); view.getTfSellPrice().clear();
        view.getTfDiscount().clear();
        view.getCbCategory().setValue(null); view.getCbSupplier().setValue(null);
    }

    private void updateProduct() { /* Logic similar to addOrRestock but for the selected index */ }
    private void deleteProduct() { /* Logic similar to admin delete */ }
    private void showAlert(String t, String c) { new Alert(Alert.AlertType.ERROR, c).show(); }
}