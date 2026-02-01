package com.store.view;

import com.store.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CashierPane extends VBox {
    // Делаем элементы доступными для контроллера
    private ComboBox<Product> productBox;
    private TextField quantityField;
    private Button sellBtn;
    private Label header;

    public CashierPane() {
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #ecf0f1;"); // Светлый фон

        header = new Label("CASHIER POINT OF SALE");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Форма ввода
        GridPane form = new GridPane();
        form.setHgap(15); form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        productBox = new ComboBox<>();
        productBox.setPromptText("Select Product...");
        productBox.setPrefWidth(250);

        quantityField = new TextField();
        quantityField.setPromptText("Qty");
        quantityField.setPrefWidth(80);

        form.add(new Label("Select Item:"), 0, 0);
        form.add(productBox, 1, 0);
        form.add(new Label("Quantity:"), 0, 1);
        form.add(quantityField, 1, 1);

        // Кнопка
        sellBtn = new Button("CONFIRM SALE & PRINT BILL");
        sellBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        sellBtn.setPadding(new Insets(10, 20, 10, 20));

        getChildren().addAll(header, form, sellBtn);
    }

    // Геттеры, чтобы Controller мог работать с этими полями
    public ComboBox<Product> getProductBox() { return productBox; }
    public TextField getQuantityField() { return quantityField; }
    public Button getSellButton() { return sellBtn; }
}