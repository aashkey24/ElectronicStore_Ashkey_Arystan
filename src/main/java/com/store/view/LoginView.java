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

public class LoginView extends GridPane { // Наследуем GridPane, как в твоем оригинале

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginView() {
        setAlignment(Pos.CENTER);

        // Твой оригинальный градиентный фон
        setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);");

        // Белая карточка по центру
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Эффект тени
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.gray(0.2));
        shadow.setRadius(20);
        card.setEffect(shadow);

        // Логотип
        ImageView logoView = new ImageView();
        try {
            // Путь к твоему ресурсу
            Image logo = new Image(getClass().getResourceAsStream("/com/store/electronicstoreapp/img.png"));
            logoView.setImage(logo);
            logoView.setFitHeight(80);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            // Если картинки нет, программа не вылетит
        }

        Label title = new Label("Staff Login");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #34495e;");

        // Поля ввода (Твои стили)
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        usernameField.setPrefWidth(280);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        passwordField.setPrefWidth(280);

        // Кнопка (Твой стиль)
        loginButton = new Button("SIGN IN");
        loginButton.setPrefWidth(280);
        loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        // Эффекты наведения (Твои стили)
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5;"));

        card.getChildren().addAll(logoView, title, usernameField, passwordField, loginButton);

        // Добавляем карточку в сетку
        add(card, 0, 0);
    }

    // Геттеры для контроллера
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getLoginButton() { return loginButton; }
}