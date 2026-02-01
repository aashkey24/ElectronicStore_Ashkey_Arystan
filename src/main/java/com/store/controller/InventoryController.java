package com.store.controller;

import com.store.model.Product;
import com.store.util.IOHandler;
import com.store.view.ManagerView;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.util.List;

public class InventoryController {
    private ManagerView view;
    private List<Product> productList;

    public InventoryController(ManagerView view, List<Product> productList) {
        this.view = view;
        this.productList = productList;
        initController();
    }

    private void initController() {
        // 1. Загрузка данных
        refreshTable();
        checkLowStock(); // Запуск проверки при входе

        // 2. События кнопок
        view.getAddButton().setOnAction(e -> handleAddProduct());
        view.getRestockButton().setOnAction(e -> handleRestock());
        view.getDeleteButton().setOnAction(e -> handleDelete());
    }

    // --- Логика ---

    private void handleAddProduct() {
        try {
            String name = view.getNameField().getText();
            String cat = view.getCategoryField().getText();
            double price = Double.parseDouble(view.getPriceField().getText());
            int stock = Integer.parseInt(view.getStockField().getText());

            if (name.isEmpty() || cat.isEmpty()) throw new IllegalArgumentException();

            // Создаем и добавляем
            Product newProd = new Product(name, cat, price, stock);
            productList.add(newProd);

            // Сохраняем в файл
            saveData();

            refreshTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product added!");

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please check all fields. Price/Stock must be numbers.");
        }
    }

    private void handleRestock() {
        Product selected = view.getProductTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select a product to restock.");
            return;
        }

        try {
            // Берем количество из поля ввода (если там есть число) или добавляем фикс. значение
            String qtyStr = view.getStockField().getText();
            int qtyToAdd = qtyStr.isEmpty() ? 5 : Integer.parseInt(qtyStr); // По умолчанию +5, если поле пустое

            selected.setStockQuantity(selected.getStockQuantity() + qtyToAdd);

            saveData();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Restocked", "Added " + qtyToAdd + " items to " + selected.getName());

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Enter a valid quantity in 'Qty' field to restock.");
        }
    }

    private void handleDelete() {
        Product selected = view.getProductTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            productList.remove(selected);
            saveData();
            refreshTable();
        }
    }

    // --- Вспомогательные методы ---

    private void refreshTable() {
        view.getProductTable().setItems(FXCollections.observableArrayList(productList));
        view.getProductTable().refresh();
        checkLowStock(); // Перепроверяем склад при каждом обновлении
    }

    private void saveData() {
        IOHandler.save("products.dat", productList);
    }

    private void clearFields() {
        view.getNameField().clear();
        view.getCategoryField().clear();
        view.getPriceField().clear();
        view.getStockField().clear();
    }

    // БОНУСНАЯ ФИЧА: Low Stock Alert
    private void checkLowStock() {
        long lowStockCount = productList.stream().filter(p -> p.getStockQuantity() < 3).count();
        if (lowStockCount > 0) {
            view.getAlertLabel().setText("⚠️ WARNING: " + lowStockCount + " products are low on stock (<3)!");
        } else {
            view.getAlertLabel().setText("");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}