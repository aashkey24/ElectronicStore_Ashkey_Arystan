package com.store.view;

import com.store.model.*;
import com.store.util.IOHandler;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class AdminView {

    private List<User> users;
    private List<Product> products;
    // Нам нужен список чеков, чтобы считать НАСТОЯЩИЙ доход
    // Если его нет, передай пока пустой список или загрузи его
    private List<Bill> bills;

    public AdminView(List<User> users, List<Product> products) {
        this.users = users;
        this.products = products;
        // Попытаемся загрузить историю продаж (если Абдулазиз сохраняет их в sales.dat)
        this.bills = IOHandler.load("sales.dat");
    }

    public VBox getView() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label header = new Label("ADMIN PANEL: User Management");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 1. СПИСОК СОТРУДНИКОВ
        ListView<User> userList = new ListView<>(FXCollections.observableArrayList(users));
        userList.setPrefHeight(200);

        // 2. КНОПКИ УПРАВЛЕНИЯ (Требование: Modify, Delete, Revoke Access)
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        Button blockBtn = new Button("Block / Unblock Access");
        Button updateBtn = new Button("Update Salary");

        // Логика удаления
        deleteBtn.setOnAction(e -> {
            User selected = userList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                users.remove(selected);
                saveAndRefresh(userList);
            }
        });

        // Логика блокировки
        // Внутри AdminView.java

        blockBtn.setOnAction(e -> {
            User selected = userList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            // 1. Меняем статус
            selected.setBlocked(!selected.isBlocked());

            // 2. Сохраняем в файл
            IOHandler.save("users.dat", users);

            // 3. ВОТ ЭТОЙ СТРОКИ ТЕБЕ НЕ ХВАТАЕТ или она не работает
            userList.refresh(); // <-- ЭТО ВАЖНО! Она обновляет текст в списке

            // 4. (Опционально) Показываем сообщение
            String status = selected.isBlocked() ? "BLOCKED" : "UNBLOCKED";
            showAlert("Status Change", "User " + selected.getUsername() + " is now " + status);
        });

        // Логика изменения зарплаты
        updateBtn.setOnAction(e -> {
            User selected = userList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getSalary()));
                dialog.setHeaderText("Change salary for " + selected.getFullName());
                dialog.showAndWait().ifPresent(newVal -> {
                    try {
                        selected.setSalary(Double.parseDouble(newVal));
                        saveAndRefresh(userList);
                    } catch (NumberFormatException ex) {
                        // Игнорируем кривой ввод
                    }
                });
            }
        });

        HBox actionBtns = new HBox(10, deleteBtn, blockBtn, updateBtn);

        // --- ФОРМА ДОБАВЛЕНИЯ (Как была, сокращенно) ---
        // ... (Тут твой старый код добавления с полями) ...
        // Я его пропущу, чтобы не засорять ответ, оставь как было у тебя в прошлом ответе.


        // --- ЧАСТЬ 3: ФИНАНСОВЫЙ ОТЧЕТ (Financial Oversight) ---
        Separator sep = new Separator();
        Label financeLabel = new Label("Financial Report");
        financeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label incomeLabel = new Label("Total Income (Sales): $0.0");
        Label costsLabel = new Label("Total Costs (Salaries + Stock): $0.0");
        Label profitLabel = new Label("Net Profit: $0.0");

        Button calcBtn = new Button("Generate Report");
        calcBtn.setOnAction(e -> {
            calculateFinances(incomeLabel, costsLabel, profitLabel);
        });

        pane.getChildren().addAll(header, userList, actionBtns, sep, financeLabel, calcBtn, incomeLabel, costsLabel, profitLabel);
        return pane;
    }

    // Метод сохранения и обновления таблицы
    private void saveAndRefresh(ListView<User> listView) {
        IOHandler.save("users.dat", users);
        listView.setItems(FXCollections.observableArrayList(users));
        listView.refresh(); // Важно, чтобы обновился текст (BLOCKED)
    }

    // Метод подсчета (Твоя главная фича)
    private void calculateFinances(Label incomeLbl, Label costsLbl, Label profitLbl) {
        // 1. Считаем ДОХОДЫ (Income) - сумма всех чеков
        // (Для этого Абдулазиз должен сохранять sales.dat через IOHandler)
        this.bills = IOHandler.load("sales.dat");
        double totalSales = 0;
        for (Bill b : bills) {
            totalSales += b.getTotalAmount();
        }

        // 2. Считаем РАСХОДЫ (Costs) - Зарплаты + Закупка товаров
        double totalSalaries = 0;
        for (User u : users) {
            totalSalaries += u.getSalary();
        }

        double totalStockCost = 0;
        for (Product p : products) {
            // Предположим, мы купили товар за 70% от цены продажи (маржа)
            // Или добавь поле purchasePrice в Product.java
            totalStockCost += (p.getPrice() * 0.7) * p.getStockQuantity();
        }

        double totalCosts = totalSalaries + totalStockCost;
        double profit = totalSales - totalCosts;

        // Вывод
        incomeLbl.setText("Total Income (Sales): $" + String.format("%.2f", totalSales));
        costsLbl.setText("Total Costs: $" + String.format("%.2f", totalCosts));
        profitLbl.setText("Net Profit: $" + String.format("%.2f", profit));

        if (profit < 0) profitLbl.setStyle("-fx-text-fill: red;");
        else profitLbl.setStyle("-fx-text-fill: green;");

    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}