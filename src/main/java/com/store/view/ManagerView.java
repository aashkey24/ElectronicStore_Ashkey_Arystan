package com.store.view;

import com.store.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class ManagerView extends VBox {
    private TableView<Product> productTable;
    private TextField nameField, categoryField, priceField, stockField;
    private Button addBtn, restockBtn, deleteBtn;
    private Label alertLabel; // Лейбл для предупреждений

    @SuppressWarnings("unchecked")
    public ManagerView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #fdfefe;");

        // Заголовок
        Label header = new Label("INVENTORY MANAGEMENT (MANAGER)");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

        // Бонус: Лейбл для Low Stock Alert
        alertLabel = new Label();
        alertLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // 1. Таблица продуктов
        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.setPrefHeight(300);

        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price ($)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock Qty");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        productTable.getColumns().addAll(nameCol, catCol, priceCol, stockCol);

        // 2. Форма управления
        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER);

        nameField = new TextField(); nameField.setPromptText("Name");
        categoryField = new TextField(); categoryField.setPromptText("Category");
        priceField = new TextField(); priceField.setPromptText("Price");
        stockField = new TextField(); stockField.setPromptText("Qty");
        stockField.setPrefWidth(60);

        form.getChildren().addAll(nameField, categoryField, priceField, stockField);

        // 3. Кнопки
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);

        addBtn = new Button("Add New Product");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        restockBtn = new Button("Restock Selected (+Qty)");
        restockBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");

        deleteBtn = new Button("Delete Product");
        deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

        actions.getChildren().addAll(addBtn, restockBtn, deleteBtn);

        getChildren().addAll(header, alertLabel, productTable, new Separator(), new Label("Product Controls:"), form, actions);
    }

    // Getters
    public TableView<Product> getProductTable() { return productTable; }
    public TextField getNameField() { return nameField; }
    public TextField getCategoryField() { return categoryField; }
    public TextField getPriceField() { return priceField; }
    public TextField getStockField() { return stockField; }
    public Button getAddButton() { return addBtn; }
    public Button getRestockButton() { return restockBtn; }
    public Button getDeleteButton() { return deleteBtn; }
    public Label getAlertLabel() { return alertLabel; }
}