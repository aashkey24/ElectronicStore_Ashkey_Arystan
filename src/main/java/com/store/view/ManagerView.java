package com.store.view;

import com.store.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ManagerView extends VBox {
    // Inventory
    private TableView<Product> productTable;
    private TextField tfName, tfBuyPrice, tfSellPrice, tfQuantity, tfDiscount;
    private ComboBox<String> cbCategory, cbSupplier;
    private Button btnAddProduct, btnUpdateProduct, btnDeleteProduct, btnClearForm;
    private TextField tfSearch;
    private Label lblAlert;

    // Suppliers & Categories
    private ListView<String> supplierList, categoryList;
    private TextField tfNewSupplier, tfNewCategory;
    private Button btnAddSupplier, btnRemoveSupplier, btnAddCategory, btnRemoveCategory;

    // Stats
    private TableView<CashierMetric> cashierTable;
    private DatePicker dpStart, dpEnd;
    private Button btnRefreshStats;
    private Label lblTotalItems, lblTotalRevenue;

    public ManagerView() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #F3F4F6;");

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Managerial Control Center");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        lblAlert = new Label("Checking Inventory...");
        lblAlert.setStyle("-fx-padding: 5 15; -fx-background-radius: 15; -fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-font-weight: bold;");
        header.getChildren().addAll(title, sp, lblAlert);

        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab inventoryTab = new Tab("Inventory & Stock", createInventoryTab());
        Tab supplierTab = new Tab("Suppliers & Sectors", createSuppliersTab());
        Tab statsTab = new Tab("Performance Analytics", createStatsTab());

        tabPane.getTabs().addAll(inventoryTab, supplierTab, statsTab);
        tabPane.getTabs().forEach(t -> t.setClosable(false));

        getChildren().addAll(header, tabPane);
    }

    private Node createInventoryTab() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15, 0, 0, 0));

        tfSearch = new TextField(); tfSearch.setPromptText("Search by product name or sector...");
        tfSearch.setStyle("-fx-padding: 8; -fx-background-radius: 5;");

        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(productTable, Priority.ALWAYS);

        TableColumn<Product, String> cName = new TableColumn<>("Product");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, String> cCat = new TableColumn<>("Sector");
        cCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Product, Integer> cQty = new TableColumn<>("Stock");
        cQty.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        TableColumn<Product, Double> cSell = new TableColumn<>("Price ($)");
        cSell.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        TableColumn<Product, Double> cDisc = new TableColumn<>("Discount (%)");
        cDisc.setCellValueFactory(new PropertyValueFactory<>("discount"));

        productTable.getColumns().addAll(cName, cCat, cQty, cSell, cDisc);

        // Form
        GridPane form = new GridPane(); form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        tfName = new TextField(); tfBuyPrice = new TextField(); tfSellPrice = new TextField();
        tfQuantity = new TextField(); tfDiscount = new TextField();
        cbCategory = new ComboBox<>(); cbSupplier = new ComboBox<>();

        cbCategory.setPromptText("Sector"); cbSupplier.setPromptText("Supplier");
        tfDiscount.setPromptText("0.0");

        form.addRow(0, new Label("Name:"), tfName, new Label("Sector:"), cbCategory, new Label("Supplier:"), cbSupplier);
        form.addRow(1, new Label("Buy Price:"), tfBuyPrice, new Label("Sell Price:"), tfSellPrice, new Label("Qty:"), tfQuantity);
        form.addRow(2, new Label("Apply Discount (%):"), tfDiscount);

        HBox actions = new HBox(10);
        btnAddProduct = new Button("Supply/Add"); btnAddProduct.setStyle("-fx-background-color: #10B981; -fx-text-fill: white;");
        btnUpdateProduct = new Button("Update");
        btnDeleteProduct = new Button("Delete"); btnDeleteProduct.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white;");
        btnClearForm = new Button("Clear");
        actions.getChildren().addAll(btnAddProduct, btnUpdateProduct, btnDeleteProduct, btnClearForm);

        layout.getChildren().addAll(tfSearch, productTable, form, actions);
        return layout;
    }

    private Node createStatsTab() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));

        HBox filter = new HBox(10, new Label("Period:"), dpStart = new DatePicker(), dpEnd = new DatePicker(), btnRefreshStats = new Button("View Metrics"));
        filter.setAlignment(Pos.CENTER_LEFT);

        cashierTable = new TableView<>();
        cashierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<CashierMetric, String> c1 = new TableColumn<>("Cashier");
        c1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<CashierMetric, Integer> c2 = new TableColumn<>("Bills");
        c2.setCellValueFactory(new PropertyValueFactory<>("billCount"));
        TableColumn<CashierMetric, Double> c3 = new TableColumn<>("Revenue");
        c3.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        cashierTable.getColumns().addAll(c1, c2, c3);

        HBox footer = new HBox(20, lblTotalItems = new Label("Total Items: 0"), lblTotalRevenue = new Label("Total Revenue: 0.00 $"));
        footer.setStyle("-fx-font-weight: bold;");

        layout.getChildren().addAll(filter, cashierTable, footer);
        return layout;
    }

    private Node createSuppliersTab() {
        HBox layout = new HBox(20); layout.setPadding(new Insets(15));

        VBox sBox = new VBox(10, new Label("Manage Suppliers"), supplierList = new ListView<>(), tfNewSupplier = new TextField(), btnAddSupplier = new Button("Register Supplier"), btnRemoveSupplier = new Button("Remove"));
        VBox cBox = new VBox(10, new Label("Manage Sectors"), categoryList = new ListView<>(), tfNewCategory = new TextField(), btnAddCategory = new Button("Add Sector"), btnRemoveCategory = new Button("Remove"));

        HBox.setHgrow(sBox, Priority.ALWAYS); HBox.setHgrow(cBox, Priority.ALWAYS);
        layout.getChildren().addAll(sBox, cBox);
        return layout;
    }

    // Static Metric Class
    public static class CashierMetric {
        private String name; private int billCount; private double revenue;
        public CashierMetric(String n, int b, double r) { this.name = n; this.billCount = b; this.revenue = r; }
        public String getName() { return name; }
        public int getBillCount() { return billCount; }
        public double getRevenue() { return revenue; }
    }

    // Getters
    public TableView<Product> getProductTable() { return productTable; }
    public TextField getTfName() { return tfName; }
    public ComboBox<String> getCbCategory() { return cbCategory; }
    public ComboBox<String> getCbSupplier() { return cbSupplier; }
    public TextField getTfBuyPrice() { return tfBuyPrice; }
    public TextField getTfSellPrice() { return tfSellPrice; }
    public TextField getTfQuantity() { return tfQuantity; }
    public TextField getTfDiscount() { return tfDiscount; }
    public Button getBtnAddProduct() { return btnAddProduct; }
    public Button getBtnUpdateProduct() { return btnUpdateProduct; }
    public Button getBtnDeleteProduct() { return btnDeleteProduct; }
    public Button getBtnClearForm() { return btnClearForm; }
    public TextField getTfSearch() { return tfSearch; }
    public ListView<String> getSupplierList() { return supplierList; }
    public TextField getTfNewSupplier() { return tfNewSupplier; }
    public Button getBtnAddSupplier() { return btnAddSupplier; }
    public Button getBtnRemoveSupplier() { return btnRemoveSupplier; }
    public ListView<String> getCategoryList() { return categoryList; }
    public TextField getTfNewCategory() { return tfNewCategory; }
    public Button getBtnAddCategory() { return btnAddCategory; }
    public Button getBtnRemoveCategory() { return btnRemoveCategory; }
    public Label getLblAlert() { return lblAlert; }
    public TableView<CashierMetric> getCashierTable() { return cashierTable; }
    public DatePicker getDpStart() { return dpStart; }
    public DatePicker getDpEnd() { return dpEnd; }
    public Button getBtnRefreshStats() { return btnRefreshStats; }
    public Label getLblTotalItems() { return lblTotalItems; }
    public Label getLblTotalRevenue() { return lblTotalRevenue; }
}