package com.store.controller;

import com.store.model.*;
import com.store.view.AdminPane;
import com.store.util.IOHandler; // <--- Импортируем
import com.store.util.Validator; // <--- Импортируем

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminController {
    private AdminPane view;
    private ObservableList<User> userList;
    private final String USERS_FILE = "users.dat";
    private final String PRODUCTS_FILE = "products.dat";

    public AdminController(AdminPane view) {
        this.view = view;
        // LOAD WITH IOHANDLER
        this.userList = FXCollections.observableArrayList(IOHandler.loadList(USERS_FILE));
        view.getUserTable().setItems(userList);
        attachEvents();
        calculateFinances();
    }

    private void attachEvents() {
        view.getBtnAdd().setOnAction(e -> addUser());
        view.getBtnUpdate().setOnAction(e -> updateUser());
        view.getBtnDelete().setOnAction(e -> deleteUser());
        view.getBtnClear().setOnAction(e -> clearFields());
        view.getBtnCalculate().setOnAction(e -> calculateFinances());

        view.getUserTable().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillForm(newVal);
        });
    }

    private void fillForm(User u) {
        view.getTfUsername().setText(u.getUsername());
        view.getTfPassword().setText(u.getPassword());
        view.getTfName().setText(u.getFullName());
        view.getTfPhone().setText(u.getPhone());
        view.getTfEmail().setText(u.getEmail());
        view.getTfSalary().setText(String.valueOf(u.getSalary()));
        view.getDpDob().setValue(u.getDateOfBirth());
        view.getCbRole().setValue(u.getRole());
    }

    private void addUser() {
        User tempUser = extractUserFromForm();
        if (tempUser == null) return;
        if (isDuplicate(tempUser, null)) return;

        userList.add(tempUser);
        saveData(); // Using our wrapper method
        clearFields();
        calculateFinances();
        showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
    }

    private void updateUser() {
        User selected = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select a user to update!", Alert.AlertType.ERROR);
            return;
        }
        User updatedUser = extractUserFromForm();
        if (updatedUser == null) return;
        if (isDuplicate(updatedUser, selected)) return;

        int index = userList.indexOf(selected);
        userList.set(index, updatedUser);
        saveData();
        clearFields();
        calculateFinances();
        showAlert("Success", "User updated successfully!", Alert.AlertType.INFORMATION);
    }

    private User extractUserFromForm() {
        try {
            String username = view.getTfUsername().getText().trim();
            String password = view.getTfPassword().getText().trim();
            String name = view.getTfName().getText().trim();
            String phone = view.getTfPhone().getText().trim();
            String email = view.getTfEmail().getText().trim();
            LocalDate dob = view.getDpDob().getValue();
            String role = view.getCbRole().getValue();
            String salaryStr = view.getTfSalary().getText().trim();

            // USING VALIDATOR
            if (Validator.isEmpty(username) || Validator.isEmpty(password) || Validator.isEmpty(name) || dob == null) {
                showAlert("Validation Error", "All fields are required!", Alert.AlertType.ERROR);
                return null;
            }
            if (!Validator.isValidEmail(email)) {
                showAlert("Validation Error", "Invalid Email format!", Alert.AlertType.ERROR);
                return null;
            }
            if (!Validator.isValidPhone(phone)) {
                showAlert("Validation Error", "Invalid Phone Number!", Alert.AlertType.ERROR);
                return null;
            }

            double salary = Double.parseDouble(salaryStr);
            if (!Validator.isPositive(salary)) {
                showAlert("Validation Error", "Salary cannot be negative!", Alert.AlertType.ERROR);
                return null;
            }

            switch (role) {
                case "Manager": return new Manager(username, password, name, dob, phone, email, salary);
                case "Cashier": return new Cashier(username, password, name, dob, phone, email, salary);
                default: return new Admin(username, password, name, dob, phone, email, salary);
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Salary must be a valid number!", Alert.AlertType.ERROR);
            return null;
        }
    }

    private boolean isDuplicate(User newUser, User currentUser) {
        for (User u : userList) {
            if (currentUser != null && u == currentUser) continue;
            if (u.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                showAlert("Duplicate Error", "Username is taken!", Alert.AlertType.ERROR);
                return true;
            }
        }
        return false;
    }

    private void deleteUser() {
        User selected = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getRole().equals("Administrator") && countAdmins() <= 1) {
                showAlert("Action Denied", "Cannot delete the last Administrator!", Alert.AlertType.ERROR);
                return;
            }
            userList.remove(selected);
            saveData();
            clearFields();
            calculateFinances();
        } else {
            showAlert("Error", "Select a user to delete.", Alert.AlertType.WARNING);
        }
    }

    private long countAdmins() {
        return userList.stream().filter(u -> u.getRole().equals("Administrator")).count();
    }

    private void clearFields() {
        view.getTfUsername().clear();
        view.getTfPassword().clear();
        view.getTfName().clear();
        view.getTfPhone().clear();
        view.getTfEmail().clear();
        view.getTfSalary().clear();
        view.getDpDob().setValue(null);
        view.getUserTable().getSelectionModel().clearSelection();
    }

    private void calculateFinances() {
        double totalSalaries = userList.stream().mapToDouble(User::getSalary).sum();

        // LOAD WITH IOHANDLER
        ArrayList<Product> products = IOHandler.loadList(PRODUCTS_FILE);
        double stockValue = products.stream().mapToDouble(p -> p.getPurchasePrice() * p.getStockQuantity()).sum();

        double totalSales = 0.0;
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();

        LocalDate start = view.getDpStart().getValue();
        LocalDate end = view.getDpEnd().getValue();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith("Bill_") && file.getName().endsWith(".txt")) {
                    if (isBillWithinRange(file.getName(), start, end)) {
                        totalSales += extractTotalFromBill(file);
                    }
                }
            }
        }

        double totalCosts = totalSalaries + stockValue;
        double profit = totalSales - totalCosts;

        view.getLblTotalSales().setText(String.format("%.2f $", totalSales));
        view.getLblTotalCosts().setText(String.format("%.2f $ (Salary+Stock)", totalCosts));
        view.getLblProfit().setText(String.format("%.2f $", profit));

        if (profit >= 0) view.getLblProfit().setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
        else view.getLblProfit().setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
    }

    private boolean isBillWithinRange(String filename, LocalDate start, LocalDate end) {
        if (start == null && end == null) return true;
        try {
            String datePart = filename.substring(5, 13);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate billDate = LocalDate.parse(datePart, dtf);
            if (start != null && billDate.isBefore(start)) return false;
            if (end != null && billDate.isAfter(end)) return false;
            return true;
        } catch (Exception e) { return false; }
    }

    private double extractTotalFromBill(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("TOTAL AMOUNT:")) {
                    String amountStr = line.replace("TOTAL AMOUNT:", "").replace("$", "").trim();
                    amountStr = amountStr.replace(",", ".");
                    return Double.parseDouble(amountStr);
                }
            }
        } catch (Exception e) { }
        return 0.0;
    }

    private void saveData() {
        // SAVE THE LIST WITH ONE LINE
        IOHandler.saveList(USERS_FILE, new ArrayList<>(userList));
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}