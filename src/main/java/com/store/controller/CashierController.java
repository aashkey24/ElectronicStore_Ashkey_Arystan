package com.store.controller;

import com.store.model.Bill;
import com.store.model.Product;
import com.store.model.User;
import com.store.util.IOHandler;
import com.store.view.CashierPane;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.util.List;

public class CashierController {
    private CashierPane view;
    private List<Product> products;
    private User currentCashier;

    public CashierController(CashierPane view, List<Product> products, User currentCashier) {
        this.view = view;
        this.products = products;
        this.currentCashier = currentCashier;

        initController();
    }

    private void initController() {
        // 1. Загружаем продукты в выпадающий список
        view.getProductBox().setItems(FXCollections.observableArrayList(products));

        // 2. Вешаем логику на кнопку (Event Handler)
        view.getSellButton().setOnAction(e -> handleSale());
    }

    private void handleSale() {
        Product selected = view.getProductBox().getValue();
        String qtyText = view.getQuantityField().getText();

        // Валидация ввода
        if (selected == null || qtyText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a product and enter quantity.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);

            // Проверка наличия на складе
            if (selected.getStockQuantity() >= qty) {

                // А. Обновляем модель (Списываем товар)
                selected.setStockQuantity(selected.getStockQuantity() - qty);
                IOHandler.save("products.dat", products); // Сохраняем изменения в файл

                // Б. Считаем сумму и создаем чек
                double total = selected.getPrice() * qty;
                Bill newBill = new Bill(currentCashier.getFullName(), selected.getName(), qty, total);

                // В. Печатаем чек в файл .txt (Требование проекта)
                String fileName = "Bill_" + newBill.getBillId();
                IOHandler.printBill(newBill.getReceiptContent(), fileName);

                // Г. Успех
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Sale Complete!\nReceipt saved as: " + fileName + ".txt");

                // Сброс полей
                view.getQuantityField().clear();
                view.getProductBox().getSelectionModel().clearSelection();

                // Обновляем список (чтобы обновилось кол-во на складе в UI)
                view.getProductBox().setItems(FXCollections.observableArrayList(products));

            } else {
                showAlert(Alert.AlertType.WARNING, "Out of Stock",
                        "Only " + selected.getStockQuantity() + " items left in stock.");
            }

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Quantity must be a valid number.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}