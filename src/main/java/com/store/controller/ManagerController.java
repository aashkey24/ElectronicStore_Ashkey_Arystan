package com.store.controller;

import com.store.model.Product;
import com.store.model.User;
import com.store.view.ManagerView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ManagerController {
    private ManagerView view;
    private User currentUser;
    private ObservableList<Product> products;
    private ObservableList<String> suppliers;
    private ObservableList<String> categories;

    private final String PRODUCTS_FILE = "products.dat";
    private final String SUPPLIERS_FILE = "suppliers.dat";
    private final String CATEGORIES_FILE = "categories.dat";

    public ManagerController(ManagerView view, User user) {
        this.view = view;
        this.currentUser = user;
        this.products = FXCollections.observableArrayList();
        this.suppliers = FXCollections.observableArrayList();
        this.categories = FXCollections.observableArrayList();

        loadData();

        // Привязываем данные
        view.getProductTable().setItems(products);
        view.getSupplierList().setItems(suppliers);
        view.getCbSupplier().setItems(suppliers);
        view.getCategoryList().setItems(categories);
        view.getCbCategory().setItems(categories);

        updateLowStockAlert();
        attachEvents();
    }

    private void attachEvents() {
        // --- PRODUCTS ---
        view.getBtnAddProduct().setOnAction(e -> addOrRestockProduct()); // <--- ИЗМЕНИЛИ МЕТОД
        view.getBtnUpdateProduct().setOnAction(e -> updateProduct());
        view.getBtnDeleteProduct().setOnAction(e -> deleteProduct());
        view.getBtnClearForm().setOnAction(e -> clearProductForm());

        view.getProductTable().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillProductForm(newVal);
        });

        view.getTfSearch().textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));

        // --- SUPPLIERS ---
        view.getBtnAddSupplier().setOnAction(e -> addSupplier());
        view.getBtnRemoveSupplier().setOnAction(e -> removeSupplier());

        // --- CATEGORIES ---
        view.getBtnAddCategory().setOnAction(e -> addCategory());
        view.getBtnRemoveCategory().setOnAction(e -> removeCategory());
    }

    private void fillProductForm(Product p) {
        view.getTfName().setText(p.getName());
        view.getCbCategory().setValue(p.getCategory());
        view.getCbSupplier().setValue(p.getSupplier());
        view.getTfQuantity().setText(String.valueOf(p.getStockQuantity()));
        view.getTfBuyPrice().setText(String.valueOf(p.getPurchasePrice()));
        view.getTfSellPrice().setText(String.valueOf(p.getSellingPrice()));
    }

    // === ГЛАВНОЕ ИЗМЕНЕНИЕ: LOGIC RESTOCK ===
    private void addOrRestockProduct() {
        Product newProduct = createProductFromForm();
        if (newProduct == null) return; // Ошибка валидации

        // 1. Ищем, есть ли товар с таким именем
        for (Product existing : products) {
            if (existing.getName().equalsIgnoreCase(newProduct.getName())) {

                // --- ЛОГИКА RESTOCK (ПОПОЛНЕНИЕ) ---
                int addedQty = newProduct.getStockQuantity();
                int newTotal = existing.getStockQuantity() + addedQty;

                existing.setStockQuantity(newTotal);

                // Обновляем цены и инфо, если они изменились в новой партии
                existing.setPurchasePrice(newProduct.getPurchasePrice());
                existing.setSellingPrice(newProduct.getSellingPrice());
                existing.setSupplier(newProduct.getSupplier());
                existing.setCategory(newProduct.getCategory());

                saveData();
                view.getProductTable().refresh(); // Обновляем таблицу визуально
                clearProductForm();
                updateLowStockAlert();

                showAlert("Restock Successful",
                        "Added " + addedQty + " units to '" + existing.getName() + "'.\n" +
                                "New Total Stock: " + newTotal);
                return; // Выходим, чтобы не добавлять дубликат
            }
        }

        // 2. Если не нашли совпадений - добавляем как новый
        products.add(newProduct);
        saveData();
        clearProductForm();
        updateLowStockAlert();
        showAlert("Success", "New Product added to Inventory.");
    }

    private void updateProduct() {
        Product selected = view.getProductTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select a product to update!");
            return;
        }

        Product updated = createProductFromForm();
        if (updated != null) {
            // Если мы меняем имя на такое, которое УЖЕ есть (но не у самого себя), это дубликат
            for (Product p : products) {
                if (p != selected && p.getName().equalsIgnoreCase(updated.getName())) {
                    showAlert("Error", "Product name '" + updated.getName() + "' already exists! Use Add to restock.");
                    return;
                }
            }

            int index = products.indexOf(selected);
            products.set(index, updated);
            saveData();
            clearProductForm();
            updateLowStockAlert();
            showAlert("Success", "Product updated.");
        }
    }

    private Product createProductFromForm() {
        try {
            String name = view.getTfName().getText().trim();
            String cat = view.getCbCategory().getValue();
            String sup = view.getCbSupplier().getValue();

            if (name.isEmpty() || cat == null || sup == null) {
                showAlert("Error", "Name, Category and Supplier are required!");
                return null;
            }

            int qty = Integer.parseInt(view.getTfQuantity().getText().trim());
            double buy = Double.parseDouble(view.getTfBuyPrice().getText().trim());
            double sell = Double.parseDouble(view.getTfSellPrice().getText().trim());

            if (qty < 0 || buy < 0 || sell < 0) {
                showAlert("Error", "Values cannot be negative!");
                return null;
            }

            return new Product(name, cat, sup, buy, sell, qty);
        } catch (NumberFormatException e) {
            showAlert("Error", "Prices and Quantity must be valid numbers!");
            return null;
        }
    }

    private void deleteProduct() {
        Product selected = view.getProductTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            products.remove(selected);
            saveData();
            clearProductForm();
        }
    }

    // --- SUPPLIERS ---
    private void addSupplier() {
        String name = view.getTfNewSupplier().getText().trim();
        if (!name.isEmpty() && !suppliers.contains(name)) {
            suppliers.add(name);
            saveData();
            view.getTfNewSupplier().clear();
        } else if (suppliers.contains(name)) {
            showAlert("Error", "Supplier already exists!");
        }
    }

    private void removeSupplier() {
        String selected = view.getSupplierList().getSelectionModel().getSelectedItem();
        if (selected != null) {
            suppliers.remove(selected);
            saveData();
        }
    }

    // --- CATEGORIES ---
    private void addCategory() {
        String name = view.getTfNewCategory().getText().trim();
        if (!name.isEmpty() && !categories.contains(name)) {
            categories.add(name);
            saveData();
            view.getTfNewCategory().clear();
        } else if (categories.contains(name)) {
            showAlert("Error", "Category already exists!");
        }
    }

    private void removeCategory() {
        String selected = view.getCategoryList().getSelectionModel().getSelectedItem();
        if (selected != null) {
            categories.remove(selected);
            saveData();
        }
    }

    private void filterProducts(String query) {
        if (query == null || query.isEmpty()) {
            view.getProductTable().setItems(products);
        } else {
            ObservableList<Product> filtered = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            view.getProductTable().setItems(filtered);
        }
    }

    private void updateLowStockAlert() {
        long count = products.stream().filter(p -> p.getStockQuantity() < 5).count();
        if (count > 0) {
            view.getLblAlert().setText("⚠️ Warning: " + count + " items low on stock!");
            view.getLblAlert().setStyle("-fx-padding: 8 15; -fx-background-radius: 20; -fx-background-color: #FECACA; -fx-text-fill: #B91C1C; -fx-font-weight: bold;");
        } else {
            view.getLblAlert().setText("✅ Inventory Healthy");
            view.getLblAlert().setStyle("-fx-padding: 8 15; -fx-background-radius: 20; -fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold;");
        }
    }

    private void clearProductForm() {
        view.getTfName().clear();
        view.getCbCategory().setValue(null);
        view.getCbSupplier().setValue(null);
        view.getTfQuantity().clear();
        view.getTfBuyPrice().clear();
        view.getTfSellPrice().clear();
        view.getProductTable().getSelectionModel().clearSelection();
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCTS_FILE))) {
            oos.writeObject(new ArrayList<>(products));
        } catch (IOException e) { e.printStackTrace(); }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SUPPLIERS_FILE))) {
            oos.writeObject(new ArrayList<>(suppliers));
        } catch (IOException e) { e.printStackTrace(); }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATEGORIES_FILE))) {
            oos.writeObject(new ArrayList<>(categories));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File pFile = new File(PRODUCTS_FILE);
        if (pFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pFile))) {
                products.setAll((ArrayList<Product>) ois.readObject());
            } catch (Exception e) { e.printStackTrace(); }
        }

        File sFile = new File(SUPPLIERS_FILE);
        if (sFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sFile))) {
                suppliers.setAll((ArrayList<String>) ois.readObject());
            } catch (Exception e) { e.printStackTrace(); }
        }

        File cFile = new File(CATEGORIES_FILE);
        if (cFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cFile))) {
                categories.setAll((ArrayList<String>) ois.readObject());
            } catch (Exception e) { e.printStackTrace(); }
        }

        if (categories.isEmpty()) {
            categories.addAll("Computers", "Smartphones", "Accessories", "Home Appliances");
            saveData();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}