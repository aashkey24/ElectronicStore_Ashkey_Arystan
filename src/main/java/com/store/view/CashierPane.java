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
    private TableView<Product> productsTable;
    private TableView<Product> cartTable;

    private TextField tfSearch, tfQuantity;
    private Button btnAddToCart, btnRemoveFromCart, btnCheckout;
    private Label lblTotal;

    private ListView<String> historyList;
    private Label lblHistoryTotal;

    public CashierPane() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #F3F4F6;");

        VBox leftPane = new VBox(15);
        leftPane.setPadding(new Insets(15));
        leftPane.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        leftPane.setMinWidth(450);

        Label lblTitle = new Label("Store Inventory");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        tfSearch = new TextField();
        tfSearch.setPromptText("🔍 Quick search products...");
        tfSearch.setStyle("-fx-padding: 8; -fx-background-radius: 5; -fx-border-color: #D1D5DB; -fx-border-radius: 5;");

        productsTable = new TableView<>();
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(productsTable, Priority.ALWAYS);

        TableColumn<Product, String> colName = new TableColumn<>("Product");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Price ($)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Double> colDisc = new TableColumn<>("Disc %");
        colDisc.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colDisc.setPrefWidth(60);

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        colStock.setPrefWidth(60);

        productsTable.getColumns().addAll(colName, colPrice, colDisc, colStock);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        tfQuantity = new TextField();
        tfQuantity.setPromptText("Qty");
        tfQuantity.setPrefWidth(70);
        tfQuantity.setStyle("-fx-padding: 8;");

        btnAddToCart = new Button("Add to Cart");
        btnAddToCart.setPrefWidth(150);
        btnAddToCart.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8; -fx-cursor: hand;");

        actionBox.getChildren().addAll(new Label("Qty:"), tfQuantity, btnAddToCart);
        leftPane.getChildren().addAll(lblTitle, tfSearch, productsTable, actionBox);

        SplitPane rightSplit = new SplitPane();
        rightSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        rightSplit.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 10;");

        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(15));
        cartBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label lblCart = new Label("Current Sale");
        lblCart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        cartTable = new TableView<>();
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Product, String> cartItem = new TableColumn<>("Item");
        cartItem.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Integer> cartQty = new TableColumn<>("Qty");
        cartQty.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        TableColumn<Product, Double> cartPrice = new TableColumn<>("Total");
        cartPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        cartTable.getColumns().addAll(cartItem, cartQty, cartPrice);

        btnRemoveFromCart = new Button("Remove Item");
        btnRemoveFromCart.setStyle("-fx-text-fill: #EF4444; -fx-background-color: transparent; -fx-border-color: #EF4444; -fx-border-radius: 5;");

        lblTotal = new Label("Total: 0.00 $");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblTotal.setStyle("-fx-text-fill: #1F2937;");

        btnCheckout = new Button("COMPLETE TRANSACTION");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-font-size: 14px; -fx-cursor: hand;");

        cartBox.getChildren().addAll(lblCart, cartTable, btnRemoveFromCart, new Separator(), lblTotal, btnCheckout);

        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(15));
        historyBox.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10;");

        Label lblHist = new Label("Your Sales Today");
        lblHist.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        historyList = new ListView<>();
        VBox.setVgrow(historyList, Priority.ALWAYS);

        lblHistoryTotal = new Label("Bills Generated: 0");
        lblHistoryTotal.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;");

        historyBox.getChildren().addAll(lblHist, historyList, lblHistoryTotal);

        rightSplit.getItems().addAll(cartBox, historyBox);
        rightSplit.setDividerPositions(0.65);

        SplitPane mainSplit = new SplitPane(leftPane, rightSplit);
        mainSplit.setDividerPositions(0.45);
        mainSplit.setStyle("-fx-background-color: transparent;");

        setCenter(mainSplit);
    }

    public TableView<Product> getProductsTable() { return productsTable; }
    public TableView<Product> getCartTable() { return cartTable; }
    public TextField getTfSearch() { return tfSearch; }
    public TextField getTfQuantity() { return tfQuantity; }
    public Button getBtnAddToCart() { return btnAddToCart; }
    public Button getBtnRemoveFromCart() { return btnRemoveFromCart; }
    public Button getBtnCheckout() { return btnCheckout; }
    public Label getLblTotal() { return lblTotal; }
    public ListView<String> getHistoryList() { return historyList; }
    public Label getLblHistoryTotal() { return lblHistoryTotal; }
}