package com.store.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private VBox root;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginView() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("ELECTRONIC STORE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);
        usernameField.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        // Поля ввода
        PasswordField pField = new PasswordField();
        TextField pTextField = new TextField();
        pTextField.setManaged(false);
        pTextField.setVisible(false);

// Кнопка-переключатель
        Button eyeBtn = new Button("👁");

        eyeBtn.setOnAction(e -> {
            if (pField.isVisible()) {
                pTextField.setText(pField.getText());
                pTextField.setVisible(true);
                pTextField.setManaged(true);
                pField.setVisible(false);
                pField.setManaged(false);
            } else {
                pField.setText(pTextField.getText());
                pField.setVisible(true);
                pField.setManaged(true);
                pTextField.setVisible(false);
                pTextField.setManaged(false);
            }
        });

        loginButton = new Button("SIGN IN");
        loginButton.setMinWidth(250);
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        root.getChildren().addAll(title, usernameField, passwordField, loginButton);
    }

    public VBox getRoot() { return root; }
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public Button getLoginButton() { return loginButton; }
}