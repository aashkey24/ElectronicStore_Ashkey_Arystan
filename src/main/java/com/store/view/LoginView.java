package com.store.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends GridPane { // TO WORK WITH SETSCENE EXTEND GRIDPANE

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginView() {
        setAlignment(Pos.CENTER);

        setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);");

        // WHITE CARD IN CENTER
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // SHADOW EFFECT
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.gray(0.2));
        shadow.setRadius(20);
        card.setEffect(shadow);

        // LOGO
        ImageView logoView = new ImageView();
        try {
            Image logo = new Image(getClass().getResourceAsStream("/com/store/electronicstoreapp/img.png"));
            logoView.setImage(logo);
            logoView.setFitHeight(80);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { /* Игнорируем */ }

        Label title = new Label("Staff Login");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #34495e;");

        // TEXT FILEDS
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        usernameField.setPrefWidth(280);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        passwordField.setPrefWidth(280);

        // BUTTON
        loginButton = new Button("SIGN IN");
        loginButton.setPrefWidth(280);
        loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        // CLICK EFFECT
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5;"));

        card.getChildren().addAll(logoView, title, usernameField, passwordField, loginButton);

        // ADD IN THE CENTER
        add(card, 0, 0);
    }

    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getLoginButton() { return loginButton; }
}