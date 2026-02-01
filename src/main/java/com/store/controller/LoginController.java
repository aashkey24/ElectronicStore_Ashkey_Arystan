package com.store.controller;

import com.store.model.User;
import com.store.view.LoginView;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.function.Consumer;

public class LoginController {
    private LoginView view;
    private List<User> users;
    private Consumer<User> onSuccess; // "Callback" для возврата успешного юзера в Main

    public LoginController(LoginView view, List<User> users, Consumer<User> onSuccess) {
        this.view = view;
        this.users = users;
        this.onSuccess = onSuccess;

        initController();
    }

    private void initController() {
        view.getLoginButton().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        boolean found = false;
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                // Если успех - вызываем callback и передаем юзера в Main
                onSuccess.accept(user);
                found = true;
                break;
            }
        }

        if (!found) {
            showAlert("Login Failed", "Invalid Username or Password.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}