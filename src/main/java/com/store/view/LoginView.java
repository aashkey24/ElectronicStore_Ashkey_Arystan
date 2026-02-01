package com.store.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

        // --- ВСТАВКА ЛОГОТИПА ---
        ImageView logoView = new ImageView();
        try {
            Image logo = new Image(getClass().getResourceAsStream("/com/store/electronicstoreapp/img.png"));
            logoView.setImage(logo);
            logoView.setFitHeight(100); // Высота логотипа (можно менять)
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Logo image missing");
        }
        // ------------------------

        Label title = new Label("ELECTRONIC STORE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");


        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);
        usernameField.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        loginButton = new Button("SIGN IN");
        loginButton.setMinWidth(250);
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        root.getChildren().addAll(logoView, title, usernameField, passwordField, loginButton);
    }

    public VBox getRoot() { return root; }
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public Button getLoginButton() { return loginButton; }
}