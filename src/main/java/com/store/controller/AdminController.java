package com.store.controller;

import com.store.model.*;
import com.store.view.AdminPane;
import com.store.util.IOHandler;
import com.store.util.Validator;
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

    public AdminController(AdminPane view, User user) {
        this.view = view;
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

        view.getUserTable().getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
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
        view.getCbIsActive().setSelected(u.isActive());
    }

    private void addUser() {
        User u = extractUser();
        if (u != null) {
            if(userList.stream().anyMatch(x -> x.getUsername().equalsIgnoreCase(u.getUsername()))) {
                showAlert("Error", "Username is already taken.");
                return;
            }
            userList.add(u);
            saveData();
            clearFields();
            calculateFinances();
            showAlert("Success", "New staff account registered.");
        }
    }

    private void updateUser() {
        User selected = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selected == null) return;
        User updated = extractUser();
        if (updated != null) {
            userList.set(userList.indexOf(selected), updated);
            saveData();
            clearFields();
            calculateFinances();
            showAlert("Success", "Employee permissions and data updated.");
        }
    }

    private User extractUser() {
        try {
            String role = view.getCbRole().getValue();
            String name = view.getTfName().getText();
            String user = view.getTfUsername().getText();
            String pass = view.getTfPassword().getText();
            double sal = Double.parseDouble(view.getTfSalary().getText());
            LocalDate dob = view.getDpDob().getValue();

            if(user.isEmpty() || pass.isEmpty() || role == null) {
                showAlert("Error", "Required fields missing.");
                return null;
            }

            User u;
            if ("Manager".equals(role)) u = new Manager(user, pass, name, dob, view.getTfPhone().getText(), view.getTfEmail().getText(), sal);
            else if ("Cashier".equals(role)) u = new Cashier(user, pass, name, dob, view.getTfPhone().getText(), view.getTfEmail().getText(), sal);
            else u = new Admin(user, pass, name, dob, view.getTfPhone().getText(), view.getTfEmail().getText(), sal);

            u.setActive(view.getCbIsActive().isSelected());
            return u;
        } catch (Exception e) {
            showAlert("Input Error", "Check numbers and salary format.");
            return null;
        }
    }

    private void calculateFinances() {
        // Costs
        double salaries = userList.stream().mapToDouble(User::getSalary).sum();
        ArrayList<Product> products = IOHandler.loadList(PRODUCTS_FILE);
        double stockValue = products.stream().mapToDouble(p -> p.getPurchasePrice() * p.getStockQuantity()).sum();

        // Income (Scanning Bills)
        double income = 0;
        File[] files = new File(".").listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith("Bill_") && isDateInRange(f.getName())) {
                    income += parseBill(f);
                }
            }
        }

        double costs = salaries + stockValue;
        view.getLblTotalSales().setText(String.format("%.2f $", income));
        view.getLblTotalCosts().setText(String.format("%.2f $", costs));
        view.getLblProfit().setText(String.format("%.2f $", income - costs));
    }

    private boolean isDateInRange(String name) {
        try {
            LocalDate d = LocalDate.parse(name.substring(5, 13), DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate s = view.getDpStart().getValue();
            LocalDate e = view.getDpEnd().getValue();
            return (s == null || !d.isBefore(s)) && (e == null || !d.isAfter(e));
        } catch (Exception ex) { return true; }
    }

    private double parseBill(File f) {
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String l = sc.nextLine();
                if (l.startsWith("TOTAL AMOUNT:")) return Double.parseDouble(l.replaceAll("[^0-9.]", ""));
            }
        } catch (Exception e) {} return 0;
    }

    private void deleteUser() {
        User s = view.getUserTable().getSelectionModel().getSelectedItem();
        if (s != null && userList.size() > 1) {
            userList.remove(s);
            saveData();
            calculateFinances();
            clearFields();
        }
    }

    private void saveData() { IOHandler.saveList(USERS_FILE, new ArrayList<>(userList)); }
    private void clearFields() {
        view.getTfUsername().clear(); view.getTfPassword().clear(); view.getTfName().clear();
        view.getTfPhone().clear(); view.getTfEmail().clear(); view.getTfSalary().clear();
        view.getDpDob().setValue(null); view.getCbIsActive().setSelected(true);
        view.getUserTable().getSelectionModel().clearSelection();
    }
    private void showAlert(String t, String c) { new Alert(Alert.AlertType.INFORMATION, c).show(); }
}