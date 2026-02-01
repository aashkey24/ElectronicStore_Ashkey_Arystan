package com.store.controller;

import com.store.model.*;
import com.store.util.IOHandler;
import com.store.view.AdminPane;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

import java.util.List;

public class AdminController {
    private AdminPane view;
    private List<User> userList; // Ссылка на общий список пользователей из Main

    public AdminController(AdminPane view, List<User> userList) {
        this.view = view;
        this.userList = userList;
        initController();
    }

    private void initController() {
        // 1. Загружаем данные в таблицу
        refreshTable();

        // 2. Обработка кнопки "Add"
        view.getAddButton().setOnAction(e -> handleAddUser());

        // 3. Обработка кнопки "Remove"
        view.getRemoveButton().setOnAction(e -> handleRemoveUser());
    }

    private void refreshTable() {
        view.getUserTable().setItems(FXCollections.observableArrayList(userList));
        view.getUserTable().refresh();
    }

    private void handleAddUser() {
        String name = view.getNameField().getText();
        String username = view.getUserField().getText();
        String pass = view.getPassField().getText();
        String role = view.getRoleBox().getValue();

        // Валидация
        if (name.isEmpty() || username.isEmpty() || pass.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all fields.");
            return;
        }

        // Проверка на дубликат логина (опционально, но полезно)
        for (User u : userList) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists!");
                return;
            }
        }

        // Создание правильного объекта
        User newUser;
        switch (role) {
            case "Administrator":
                newUser = new Admin(username, pass, name);
                break;
            case "Manager":
                newUser = new Manager(username, pass, name);
                break;
            default:
                newUser = new Cashier(username, pass, name);
                break;
        }

        // Сохранение
        userList.add(newUser);
        IOHandler.save("users.dat", userList);

        refreshTable();
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Success", "User " + username + " added successfully.");
    }

    private void handleRemoveUser() {
        User selected = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select a user to remove.");
            return;
        }

        // Защита: Нельзя удалить самого себя (или последнего админа) - опционально
        if (selected instanceof Admin && selected.getUsername().equals("admin")) {
            // Простая защита главного админа
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete the main Administrator.");
            return;
        }

        userList.remove(selected);
        IOHandler.save("users.dat", userList);
        refreshTable();
        showAlert(Alert.AlertType.INFORMATION, "Deleted", "User removed.");
    }

    private void clearFields() {
        view.getNameField().clear();
        view.getUserField().clear();
        view.getPassField().clear();
        view.getRoleBox().setValue("Cashier");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}