package com.store.view;

import com.store.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class AdminPane extends VBox {
    // Элементы управления делаем private, но даем к ним доступ через getters
    private TableView<User> userTable;
    private TextField nameField, userField, passField;
    private ComboBox<String> roleBox;
    private Button addBtn, removeBtn;

    @SuppressWarnings("unchecked")
    public AdminPane() {
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);

        // Заголовок
        Label header = new Label("STAFF MANAGEMENT (ADMIN)");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 1. Таблица сотрудников
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setPrefHeight(300);

        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        userTable.getColumns().addAll(nameCol, userCol, roleCol);

        // 2. Форма добавления (GridPane для красоты)
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");

        nameField = new TextField(); nameField.setPromptText("Full Name");
        userField = new TextField(); userField.setPromptText("Username");
        passField = new TextField(); passField.setPromptText("Password");

        roleBox = new ComboBox<>(FXCollections.observableArrayList("Manager", "Cashier", "Administrator"));
        roleBox.setValue("Cashier");
        roleBox.setPrefWidth(150);

        form.add(new Label("Full Name:"), 0, 0); form.add(nameField, 1, 0);
        form.add(new Label("Username:"), 0, 1);  form.add(userField, 1, 1);
        form.add(new Label("Password:"), 2, 1);  form.add(passField, 3, 1);
        form.add(new Label("Role:"), 2, 0);      form.add(roleBox, 3, 0);

        // 3. Кнопки действий
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);

        addBtn = new Button("Register New Employee");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        removeBtn = new Button("Remove Selected User");
        removeBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

        actions.getChildren().addAll(addBtn, removeBtn);

        getChildren().addAll(header, userTable, new Separator(), new Label("Add New Staff:"), form, actions);
    }

    // --- Getters для Контроллера ---
    public TableView<User> getUserTable() { return userTable; }
    public TextField getNameField() { return nameField; }
    public TextField getUserField() { return userField; }
    public TextField getPassField() { return passField; }
    public ComboBox<String> getRoleBox() { return roleBox; }
    public Button getAddButton() { return addBtn; }
    public Button getRemoveButton() { return removeBtn; }
}