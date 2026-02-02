package com.store.controller;

import com.store.model.Product;
import com.store.model.User;
import com.store.view.ManagerView;
import com.store.util.IOHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

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

        // Load products using IOHandler
        this.products = FXCollections.observableArrayList(IOHandler.loadList(PRODUCTS_FILE));
        this.suppliers = FXCollections.observableArrayList(IOHandler.loadList(SUPPLIERS_FILE));
        this.categories = FXCollections.observableArrayList(IOHandler.loadList(CATEGORIES_FILE));

        if (categories.isEmpty()) {
            categories.addAll("Computers", "Smartphones", "Accessories");
            saveAllData();
        }

        view.getProductTable().setItems(products);
        view.getSupplierList().setItems(suppliers);
        view.getCbSupplier().setItems(suppliers);
        view.getCategoryList().setItems(categories);
        view.getCbCategory().setItems(categories);

        updateLowStockAlert();
        attachEvents();
    }

    private void attachEvents() {
        view.getBtnAddProduct().setOnAction(e -> addOrRestockProduct());
        view.getBtnUpdateProduct().setOnAction(e -> updateProduct());
        view.getBtnDeleteProduct().setOnAction(e -> deleteProduct());
        view.getBtnClearForm().setOnAction(e -> clearProductForm());

        view.getProductTable().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillProductForm(newVal);
        });

        view.getTfSearch().textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));
        view.getBtnAddSupplier().setOnAction(e -> addSupplier());
        view.getBtnRemoveSupplier().setOnAction(e -> removeSupplier());
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

    private void addOrRestockProduct() {
        Product newProduct = createProductFromForm();
        if (newProduct == null) return;

        for (Product existing : products) {
            if (existing.getName().equalsIgnoreCase(newProduct.getName())) {
                int addedQty = newProduct.getStockQuantity();
                existing.setStockQuantity(existing.getStockQuantity() + addedQty);
                existing.setPurchasePrice(newProduct.getPurchasePrice());
                existing.setSellingPrice(newProduct.getSellingPrice());
                existing.setSupplier(newProduct.getSupplier());
                existing.setCategory(newProduct.getCategory());

                saveAllData();
                view.getProductTable().refresh();
                clearProductForm();
                updateLowStockAlert();
                showAlert("Restock Successful", "Added " + addedQty + " units.");
                return;
            }
        }
        products.add(newProduct);
        saveAllData();
        clearProductForm();
        updateLowStockAlert();
        showAlert("Success", "New Product added.");
    }

    private void updateProduct() {
        Product selected = view.getProductTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select a product to update!");
            return;
        }
        Product updated = createProductFromForm();
        if (updated != null) {
            for (Product p : products) {
                if (p != selected && p.getName().equalsIgnoreCase(updated.getName())) {
                    showAlert("Error", "Name already exists! Use Add to restock.");
                    return;
                }
            }
            int index = products.indexOf(selected);
            products.set(index, updated);
            saveAllData();
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
                showAlert("Error", "Required fields missing!");
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
            showAlert("Error", "Invalid numbers!");
            return null;
        }
    }

    private void deleteProduct() {
        Product selected = view.getProductTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            products.remove(selected);
            saveAllData();
            clearProductForm();
        }
    }

    private void addSupplier() {
        String name = view.getTfNewSupplier().getText().trim();
        if (!name.isEmpty() && !suppliers.contains(name)) {
            suppliers.add(name);
            saveAllData();
            view.getTfNewSupplier().clear();
        } else if (suppliers.contains(name)) showAlert("Error", "Exists!");
    }

    private void removeSupplier() {
        String selected = view.getSupplierList().getSelectionModel().getSelectedItem();
        if (selected != null) {
            suppliers.remove(selected);
            saveAllData();
        }
    }

    private void addCategory() {
        String name = view.getTfNewCategory().getText().trim();
        if (!name.isEmpty() && !categories.contains(name)) {
            categories.add(name);
            saveAllData();
            view.getTfNewCategory().clear();
        } else if (categories.contains(name)) showAlert("Error", "Exists!");
    }

    private void removeCategory() {
        String selected = view.getCategoryList().getSelectionModel().getSelectedItem();
        if (selected != null) {
            categories.remove(selected);
            saveAllData();
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

    // SAVE ALL DATA USING IOHANDLER
    private void saveAllData() {
        IOHandler.saveList(PRODUCTS_FILE, new ArrayList<>(products));
        IOHandler.saveList(SUPPLIERS_FILE, new ArrayList<>(suppliers));
        IOHandler.saveList(CATEGORIES_FILE, new ArrayList<>(categories));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}