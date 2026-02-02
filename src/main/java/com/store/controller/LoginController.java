package com.store.controller;

import com.store.model.*;
import com.store.view.*;
import com.store.util.IOHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * Controller for the Login screen.
 * Handles authentication and redirects users to their respective dashboards.
 */
public class LoginController {
    private LoginView view;
    private Stage stage;
    private final String USERS_FILE = "users.dat";

    public LoginController(LoginView view, Stage stage) {
        this.view = view;
        this.stage = stage;

        // Setup button actions
        this.view.getLoginButton().setOnAction(e -> handleLogin());
        this.view.getPasswordField().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please enter both username and password.", Alert.AlertType.WARNING);
            return;
        }

        // Load latest users from file
        ArrayList<User> users = IOHandler.loadList(USERS_FILE);
        User foundUser = null;

        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                // Check if Administrator has revoked access
                if (!u.isActive()) {
                    showAlert("Access Denied", "Your account has been disabled by the Administrator.", Alert.AlertType.ERROR);
                    return;
                }
                foundUser = u;
                break;
            }
        }

        if (foundUser != null) {
            Node initialPane;

            // Navigate based on Role
            if (foundUser instanceof Admin || foundUser.getRole().equals("Administrator")) {
                AdminPane adminPane = new AdminPane();
                // FIXED: Passing both the view and the user object
                new AdminController(adminPane, foundUser);
                initialPane = adminPane;
            } else if (foundUser instanceof Manager || foundUser.getRole().equals("Manager")) {
                ManagerView managerView = new ManagerView();
                new ManagerController(managerView, foundUser);
                initialPane = managerView;
            } else {
                CashierPane cashierPane = new CashierPane();
                new CashierController(cashierPane, foundUser);
                initialPane = cashierPane;
            }

            // Set the center content of the dashboard
            DashboardView dashboard = new DashboardView(foundUser, initialPane);

            Scene scene = new Scene(dashboard, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Electronics Store - " + foundUser.getRole());
            stage.centerOnScreen();
        } else {
            showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
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