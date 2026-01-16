package com.store.view;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

public class AdminPane extends VBox {
    public AdminPane() {
        setSpacing(20);
        setPadding(new Insets(20));

        Label header = new Label("User Management (Admin)");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Object> userTable = new TableView<>();
        TableColumn<Object, String> nameCol = new TableColumn<>("Full Name");
        TableColumn<Object, String> roleCol = new TableColumn<>("Role");
        userTable.getColumns().addAll(nameCol, roleCol);

        HBox actions = new HBox(10);
        Button addBtn = new Button("Add Employee");
        Button deleteBtn = new Button("Remove");
        actions.getChildren().addAll(addBtn, deleteBtn);

        getChildren().addAll(header, userTable, actions);
    }
}