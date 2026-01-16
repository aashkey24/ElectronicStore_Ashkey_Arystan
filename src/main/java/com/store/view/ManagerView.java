package com.store.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ManagerView {
    private Stage stage;

    public ManagerView(Stage stage) {
        this.stage = stage;
    }

    public void showLoginScene(javafx.event.EventHandler<javafx.event.ActionEvent> loginHandler,
                               TextField userField, PasswordField passField) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        layout.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("ELECTRONIC STORE");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: white; -fx-font-weight: bold;");

        userField.setPromptText("Username");
        userField.setMaxWidth(250);
        passField.setPromptText("Password");
        passField.setMaxWidth(250);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        loginBtn.setPrefWidth(250);
        loginBtn.setOnAction(loginHandler);

        layout.getChildren().addAll(title, new Label(" "), userField, passField, loginBtn);

        Scene scene = new Scene(layout, 450, 400);
        stage.setScene(scene);
        stage.setTitle("Login - Electronic Store");
        stage.show();
    }

    public void showMainDashboard(String role, String name, Pane content) {
        BorderPane root = new BorderPane();
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(200);

        Label userLabel = new Label("User: " + name);
        Label roleLabel = new Label("Role: " + role);
        userLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;");
        roleLabel.setStyle("-fx-text-fill: #bdc3c7;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(userLabel, roleLabel, new Separator(), logoutBtn);

        root.setLeft(sidebar);
        root.setCenter(content);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }
}