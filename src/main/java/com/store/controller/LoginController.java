package com.store.controller;
import com.store.view.DashboardView;
import com.store.model.User;
import com.store.view.AdminPane;
import com.store.view.CashierPane;
import com.store.view.LoginView;
import com.store.view.ManagerView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class LoginController {
    private LoginView view;
    private Stage primaryStage;
    private ArrayList<User> users;
    private static final String USERS_FILE = "users.dat";

    public LoginController(LoginView view, Stage primaryStage) {
        this.view = view;
        this.primaryStage = primaryStage;
        this.users = loadUsers();

        // Привязываем действие к кнопке
        this.view.getLoginButton().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();

        User foundUser = null;
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                foundUser = u;
                break;
            }
        }

        if (foundUser != null) {
            openDashboard(foundUser);
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private void openDashboard(User user) {
        // 1. Создаем контент в зависимости от роли
        javafx.scene.Node content = null;

        switch (user.getRole()) {
            case "Administrator":
                AdminPane adminPane = new AdminPane();
                new AdminController(adminPane); // Подключаем логику
                content = adminPane;
                break;

            case "Manager":
                ManagerView managerView = new ManagerView();
                new ManagerController(managerView, user);
                content = managerView;
                break;

            case "Cashier":
                CashierPane cashierPane = new CashierPane();
                new CashierController(cashierPane, user);
                content = cashierPane;
                break;
        }

        // 2. Создаем Dashboard и кладем в него контент
        DashboardView dashboard = new DashboardView(user, content);

        // 3. Настраиваем кнопку Logout в Dashboard
        dashboard.getLogoutBtn().setOnAction(e -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView, primaryStage);
            primaryStage.setScene(new Scene(loginView, 900, 600));
        });

        // 4. Показываем
        // ... внутри метода openDashboard

        // 4. Показываем
        Scene scene = new Scene(dashboard, 1200, 800); // Увеличили базовый размер
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // <--- ДОБАВЬТЕ ЭТУ СТРОКУ
        primaryStage.centerOnScreen();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<User>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}