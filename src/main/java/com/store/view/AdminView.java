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
    private List<Product> products; // Нужно для подсчета финансов

    public AdminView(List<User> users, List<Product> products) {
        this.users = users;
        this.products = products;
    }

    // В файле com.store.view.AdminView.java

    public VBox getView() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label header = new Label("SYSTEM ADMINISTRATION");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // --- ЧАСТЬ 1: Управление сотрудниками ---
        Label staffLabel = new Label("Staff Management");

        ListView<User> userList = new ListView<>(FXCollections.observableArrayList(users));
        userList.setPrefHeight(200);

        // 1. СОЗДАЕМ ПОЛЯ ВВОДА (Добавили Phone и Salary)
        TextField nameField = new TextField(); nameField.setPromptText("Full Name");
        TextField userField = new TextField(); userField.setPromptText("Username");
        PasswordField passField = new PasswordField(); passField.setPromptText("Password");

        TextField phoneField = new TextField(); phoneField.setPromptText("Phone (e.g. 555-0102)"); // НОВОЕ
        TextField salaryField = new TextField(); salaryField.setPromptText("Salary (e.g. 2500.0)"); // НОВОЕ

        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList("Manager", "Cashier", "Admin"));
        roleBox.setValue("Cashier");

        Button addBtn = new Button("Register User");
        addBtn.setOnAction(e -> {
            // Простая валидация
            if (userField.getText().isEmpty() || salaryField.getText().isEmpty()) {
                showAlert("Error", "Please fill all fields");
                return;
            }

            try {
                // 2. СЧИТЫВАЕМ НОВЫЕ ДАННЫЕ
                double salary = Double.parseDouble(salaryField.getText());
                String phone = phoneField.getText();

                User newUser;
                String role = roleBox.getValue();

                // 3. ИСПРАВЛЕННЫЕ КОНСТРУКТОРЫ (Теперь 5 аргументов!)
                if (role.equals("Manager")) {
                    newUser = new Manager(userField.getText(), passField.getText(), nameField.getText(), phone, salary, "General");
                } else if (role.equals("Admin")) {
                    newUser = new Admin(userField.getText(), passField.getText(), nameField.getText(), phone, salary);
                } else {
                    newUser = new Cashier(userField.getText(), passField.getText(), nameField.getText(), phone, salary);
                }

                users.add(newUser);
                IOHandler.save("users.dat", users);
                userList.setItems(FXCollections.observableArrayList(users));

                // Очистка полей
                nameField.clear(); userField.clear(); passField.clear(); phoneField.clear(); salaryField.clear();

            } catch (NumberFormatException ex) {
                showAlert("Error", "Salary must be a number (e.g. 2000.50)");
            }
        });

        // Добавляем новые поля в HBox, чтобы их было видно
        HBox controls = new HBox(10, nameField, userField, passField, phoneField, salaryField, roleBox);
        HBox controls2 = new HBox(10, addBtn); // Кнопку отдельно, чтобы было красивее

        // --- ЧАСТЬ 2: Финансовый отчет ---
        Separator sep = new Separator();
        Label financeLabel = new Label("Financial Oversight");

        Label incomeLabel = new Label("Total Potential Income: $0.0");
        Label costLabel = new Label("Total Salaries: $0.0");

        Button calcBtn = new Button("Calculate Financials");
        calcBtn.setOnAction(e -> {
            double totalStockValue = 0;
            for (Product p : products) {
                totalStockValue += (p.getPrice() * p.getStockQuantity());
            }

            double totalSalaries = 0;
            for (User u : users) {
                // Теперь метод getSalary() точно работает
                totalSalaries += u.getSalary();
            }

            incomeLabel.setText("Total Stock Value: $" + String.format("%.2f", totalStockValue));
            costLabel.setText("Total Monthly Salaries: $" + String.format("%.2f", totalSalaries));
        });

        pane.getChildren().addAll(
                header,
                staffLabel, userList, controls, controls2,
                sep,
                financeLabel, calcBtn, incomeLabel, costLabel
        );

        return pane;
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.INFORMATION, content).show();
    }
}