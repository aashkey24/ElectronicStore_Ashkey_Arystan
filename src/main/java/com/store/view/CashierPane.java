package com.store.view;

import com.store.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CashierPane extends BorderPane {
    // Left: Products
    private TableView<Product> productsTable;
    private TextField tfSearch, tfQuantity;
    private Button btnAddToCart;

    // Right: Cart (Top) & History (Bottom)
    private TableView<Product> cartTable;
    private Button btnRemoveFromCart, btnCheckout;
    private Label lblTotal;

    // NEW: Today's History
    private ListView<String> historyList; // Простой список "Чек #1 - 50$"
    private Label lblHistoryTotal; // "Total Bills Today: 5"

    public CashierPane() {
        setStyle("-fx-background-color: #F3F4F6;");

        // --- LEFT SIDE: PRODUCTS ---
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(15));
        leftPane.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");

        Label lblProd = new Label("Available Products");
        lblProd.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        tfSearch = new TextField();
        tfSearch.setPromptText("Search Product...");
        tfSearch.setStyle("-fx-padding: 8; -fx-border-color: #D1D5DB; -fx-background-radius: 4;");

        productsTable = new TableView<>();
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(productsTable, Priority.ALWAYS);

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Price ($)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        productsTable.getColumns().addAll(colName, colPrice, colStock);

        HBox cartActions = new HBox(10);
        tfQuantity = new TextField();
        tfQuantity.setPromptText("Qty");
        tfQuantity.setPrefWidth(60);
        btnAddToCart = new Button("Add to Cart");
        btnAddToCart.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");
        cartActions.getChildren().addAll(tfQuantity, btnAddToCart);

        leftPane.getChildren().addAll(lblProd, tfSearch, productsTable, cartActions);

        // --- RIGHT SIDE ---
        SplitPane rightSplit = new SplitPane();
        rightSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        rightSplit.setStyle("-fx-background-color: transparent;");

        // Top Right: Current Cart
        VBox cartPane = new VBox(10);
        cartPane.setPadding(new Insets(15));
        cartPane.setStyle("-fx-background-color: white;");

        Label lblCart = new Label("Current Bill (Cart)");
        lblCart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        cartTable = new TableView<>();
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        TableColumn<Product, String> cartName = new TableColumn<>("Item");
        cartName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> cartPrice = new TableColumn<>("Price");
        cartPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Integer> cartQty = new TableColumn<>("Qty");
        cartQty.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        cartTable.getColumns().addAll(cartName, cartPrice, cartQty);

        btnRemoveFromCart = new Button("Remove Item");
        lblTotal = new Label("Total: 0.00 $");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        btnCheckout = new Button("Checkout & Print");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");

        cartPane.getChildren().addAll(lblCart, cartTable, btnRemoveFromCart, new Separator(), lblTotal, btnCheckout);

        // Bottom Right: Today's History
        VBox historyPane = new VBox(10);
        historyPane.setPadding(new Insets(15));
        historyPane.setStyle("-fx-background-color: #F9FAFB;");

        Label lblHist = new Label("Today's Sales History");
        lblHist.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        historyList = new ListView<>();
        VBox.setVgrow(historyList, Priority.ALWAYS);

        lblHistoryTotal = new Label("Bills Generated Today: 0");
        lblHistoryTotal.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;");

        historyPane.getChildren().addAll(lblHist, historyList, lblHistoryTotal);

        rightSplit.getItems().addAll(cartPane, historyPane);
        rightSplit.setDividerPositions(0.6); // 60% корзина, 40% история

        // Main Layout
        SplitPane mainSplit = new SplitPane();
        mainSplit.getItems().addAll(leftPane, rightSplit);
        mainSplit.setDividerPositions(0.4); // 40% продукты, 60% право

        setCenter(mainSplit);
    }

    // Getters
    public TableView<Product> getProductsTable() { return productsTable; }
    public TableView<Product> getCartTable() { return cartTable; }
    public TextField getTfSearch() { return tfSearch; }
    public TextField getTfQuantity() { return tfQuantity; }
    public Button getBtnAddToCart() { return btnAddToCart; }
    public Button getBtnRemoveFromCart() { return btnRemoveFromCart; }
    public Button getBtnCheckout() { return btnCheckout; }
    public Label getLblTotal() { return lblTotal; }

    // New Getters
    public ListView<String> getHistoryList() { return historyList; }
    public Label getLblHistoryTotal() { return lblHistoryTotal; }
}