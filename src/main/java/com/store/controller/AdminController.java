package com.store.controller;

import com.store.model.*;
import com.store.view.AdminPane;
import com.store.view.LoginView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AdminController {
    private AdminPane view;
    private ObservableList<User> userList;
    private final String USERS_FILE = "users.dat";
    private final String PRODUCTS_FILE = "products.dat";

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s]{7,15}$");

    public AdminController(AdminPane view) {
        this.view = view;
        this.userList = FXCollections.observableArrayList();
        loadUsers();

        view.getUserTable().setItems(userList);
        attachEvents();

        // --- ГЛАВНОЕ ИЗМЕНЕНИЕ ---
        // Считаем финансы сразу при запуске (за все время)
        calculateFinances();
    }

    private void attachEvents() {
        view.getBtnAdd().setOnAction(e -> addUser());
        view.getBtnUpdate().setOnAction(e -> updateUser());
        view.getBtnDelete().setOnAction(e -> deleteUser());
        view.getBtnClear().setOnAction(e -> clearFields());

        // Кнопка теперь работает как "Применить фильтр"
        view.getBtnCalculate().setOnAction(e -> calculateFinances());

        view.getMiLogout().setOnAction(e -> logout());

        view.getUserTable().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillForm(newVal);
        });
    }

    // ... (Методы fillForm, addUser, updateUser, extractUserFromForm, isDuplicate, deleteUser, countAdmins, clearFields - ОСТАЮТСЯ БЕЗ ИЗМЕНЕНИЙ, скопируйте их из прошлого кода или оставьте как есть) ...
    // Чтобы код влез в ответ, я пропущу блок управления юзерами, он не менялся.
    // Вставьте сюда методы: fillForm, addUser, updateUser, extractUserFromForm, isDuplicate, deleteUser, countAdmins, clearFields.

    // ---------------------------------------------------------
    // --- ОБНОВЛЕННАЯ ФИНАНСОВАЯ ЛОГИКА ---
    // ---------------------------------------------------------
    private void calculateFinances() {
        // 1. Расходы (Зарплаты + Склад) - это фиксированные расходы, они не зависят от даты чеков
        // (Хотя в идеале зарплату тоже надо считать по месяцам, но в рамках учебного проекта это обычно "текущий штат")
        double totalSalaries = userList.stream().mapToDouble(User::getSalary).sum();

        double stockValue = 0.0;
        File prodFile = new File(PRODUCTS_FILE);
        if (prodFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(prodFile))) {
                ArrayList<Product> products = (ArrayList<Product>) ois.readObject();
                stockValue = products.stream().mapToDouble(p -> p.getPurchasePrice() * p.getStockQuantity()).sum();
            } catch (Exception e) { e.printStackTrace(); }
        }

        // 2. Доходы (Продажи) - ТУТ ДОБАВЛЯЕМ ФИЛЬТР ПО ДАТЕ
        double totalSales = 0.0;
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();

        LocalDate start = view.getDpStart().getValue();
        LocalDate end = view.getDpEnd().getValue();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith("Bill_") && file.getName().endsWith(".txt")) {
                    // Проверяем дату файла
                    if (isBillWithinRange(file.getName(), start, end)) {
                        totalSales += extractTotalFromBill(file);
                    }
                }
            }
        }

        double totalCosts = totalSalaries + stockValue;
        double profit = totalSales - totalCosts;

        // Обновляем UI
        view.getLblTotalSales().setText(String.format("%.2f $", totalSales));
        view.getLblTotalCosts().setText(String.format("%.2f $ (Salary+Stock)", totalCosts));
        view.getLblProfit().setText(String.format("%.2f $", profit));

        if (profit >= 0) view.getLblProfit().setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
        else view.getLblProfit().setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
    }

    // Метод проверки даты чека по названию файла
    private boolean isBillWithinRange(String filename, LocalDate start, LocalDate end) {
        // Если даты не выбраны - считаем, что подходит (показываем всё)
        if (start == null && end == null) return true;

        try {
            // Формат Bill_yyyyMMdd_HHmmss.txt
            // Вырезаем yyyyMMdd (начинается с 5-го символа, длина 8)
            String datePart = filename.substring(5, 13);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate billDate = LocalDate.parse(datePart, dtf);

            // Логика сравнения
            if (start != null && billDate.isBefore(start)) return false;
            if (end != null && billDate.isAfter(end)) return false;

            return true;
        } catch (Exception e) {
            // Если имя файла странное, лучше его пропустить или включить (на ваше усмотрение)
            return false;
        }
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
        } catch (Exception e) {
            System.out.println("Error reading bill: " + file.getName());
        }
        return 0.0;
    }

    // ---------------------------------------------------------
    // Ниже дублирую методы управления юзерами, чтобы у вас был полный файл
    // ---------------------------------------------------------

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
        saveUsers();
        clearFields();
        // Пересчитываем финансы, так как добавилась новая зарплата
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
        saveUsers();
        clearFields();
        calculateFinances(); // Пересчитываем финансы
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

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() ||
                    phone.isEmpty() || email.isEmpty() || salaryStr.isEmpty() || dob == null) {
                showAlert("Validation Error", "All fields are required!", Alert.AlertType.ERROR);
                return null;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showAlert("Validation Error", "Invalid Email format!", Alert.AlertType.ERROR);
                return null;
            }
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                showAlert("Validation Error", "Invalid Phone Number!", Alert.AlertType.ERROR);
                return null;
            }
            double salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
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
            if (u.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                showAlert("Duplicate Error", "Email is already registered!", Alert.AlertType.ERROR);
                return true;
            }
            if (u.getPhone().equals(newUser.getPhone())) {
                showAlert("Duplicate Error", "Phone is already registered!", Alert.AlertType.ERROR);
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
            saveUsers();
            clearFields();
            calculateFinances(); // Пересчитываем финансы
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

    private void logout() {
        Stage stage = (Stage) view.getScene().getWindow();
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage);
        stage.setScene(new Scene(loginView, 800, 600));
        stage.setTitle("Login");
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(new ArrayList<>(userList));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                userList.setAll((ArrayList<User>) ois.readObject());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}