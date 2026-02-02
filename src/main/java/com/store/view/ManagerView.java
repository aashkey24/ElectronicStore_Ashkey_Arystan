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
    // Вкладка товаров
    private TableView<Product> productTable;
    private TextField tfName, tfBuyPrice, tfSellPrice, tfQuantity;
    private ComboBox<String> cbCategory; // ТЕПЕРЬ ЭТО ВЫБОР
    private ComboBox<String> cbSupplier;
    private Button btnAddProduct, btnUpdateProduct, btnDeleteProduct, btnClearForm;
    private TextField tfSearch;
    private Label lblAlert;

    // Вкладка поставщиков
    private ListView<String> supplierList;
    private TextField tfNewSupplier;
    private Button btnAddSupplier, btnRemoveSupplier;

    // Вкладка категорий (НОВОЕ)
    private ListView<String> categoryList;
    private TextField tfNewCategory;
    private Button btnAddCategory, btnRemoveCategory;

    // Заглушка
    private MenuItem miLogoutStub = new MenuItem();

    public ManagerView() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #F3F4F6;");

        // --- HEADER ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Inventory Management");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #1F2937;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblAlert = new Label("Checking stock...");
        lblAlert.setStyle("-fx-padding: 8 15; -fx-background-radius: 20; -fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-weight: bold;");

        header.getChildren().addAll(title, spacer, lblAlert);

        // --- TABS ---
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab productsTab = new Tab("Products & Stock", createProductsTab());
        productsTab.setClosable(false);

        Tab suppliersTab = new Tab("Suppliers", createSuppliersTab());
        suppliersTab.setClosable(false);

        Tab categoriesTab = new Tab("Categories (Sectors)", createCategoriesTab());
        categoriesTab.setClosable(false);

        tabPane.getTabs().addAll(productsTab, suppliersTab, categoriesTab);

        getChildren().addAll(header, tabPane);
    }

    private Node createProductsTab() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15, 0, 0, 0));

        // 1. Поиск
        HBox tools = new HBox(10);
        tfSearch = new TextField();
        tfSearch.setPromptText("Search by Name...");
        tfSearch.setPrefWidth(300);
        tfSearch.setStyle("-fx-padding: 8; -fx-background-radius: 5; -fx-border-color: #D1D5DB;");
        tools.getChildren().add(tfSearch);

        // 2. Таблица
        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(productTable, Priority.ALWAYS);

        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, String> colSup = new TableColumn<>("Supplier");
        colSup.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        TableColumn<Product, Integer> colStock = new TableColumn<>("Qty");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        colStock.setStyle("-fx-font-weight: bold;");

        TableColumn<Product, Double> colBuy = new TableColumn<>("Buy ($)");
        colBuy.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));

        TableColumn<Product, Double> colSell = new TableColumn<>("Sell ($)");
        colSell.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        productTable.getColumns().addAll(colName, colCat, colSup, colStock, colBuy, colSell);

        // 3. Форма
        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(20));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");

        GridPane form = new GridPane();
        form.setHgap(15); form.setVgap(15);

        tfName = styledField("Name");

        // ВАЖНО: Теперь это ComboBox
        cbCategory = new ComboBox<>();
        cbCategory.setPromptText("Select Category");
        cbCategory.setPrefWidth(200);

        cbSupplier = new ComboBox<>();
        cbSupplier.setPromptText("Select Supplier");
        cbSupplier.setPrefWidth(200);

        tfQuantity = styledField("Quantity");
        tfBuyPrice = styledField("Buy Price");
        tfSellPrice = styledField("Sell Price");

        form.addRow(0, new Label("Info:"), tfName, cbCategory, cbSupplier);
        form.addRow(1, new Label("Pricing:"), tfQuantity, tfBuyPrice, tfSellPrice);

        HBox actions = new HBox(15);
        btnAddProduct = new Button("Add");
        btnAddProduct.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");

        btnUpdateProduct = new Button("Update");
        btnUpdateProduct.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold;");

        btnDeleteProduct = new Button("Delete");
        btnDeleteProduct.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");

        btnClearForm = new Button("Clear");

        actions.getChildren().addAll(btnAddProduct, btnUpdateProduct, btnDeleteProduct, btnClearForm);
        formCard.getChildren().addAll(form, new Separator(), actions);

        layout.getChildren().addAll(tools, productTable, formCard);
        return layout;
    }

    private Node createSuppliersTab() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        VBox listBox = new VBox(10);
        listBox.setPrefWidth(300);

        supplierList = new ListView<>();
        VBox.setVgrow(supplierList, Priority.ALWAYS);
        btnRemoveSupplier = new Button("Remove Selected");
        btnRemoveSupplier.setMaxWidth(Double.MAX_VALUE);

        listBox.getChildren().addAll(new Label("Suppliers List"), supplierList, btnRemoveSupplier);

        VBox addBox = new VBox(15);
        addBox.setPrefWidth(300);
        addBox.setPadding(new Insets(20));
        addBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        tfNewSupplier = styledField("New Supplier Name");
        btnAddSupplier = new Button("Add Supplier");
        btnAddSupplier.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddSupplier.setMaxWidth(Double.MAX_VALUE);

        addBox.getChildren().addAll(new Label("Add New Supplier"), tfNewSupplier, btnAddSupplier);

        layout.getChildren().addAll(listBox, addBox);
        return layout;
    }

    // НОВАЯ ВКЛАДКА: КАТЕГОРИИ
    private Node createCategoriesTab() {
        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        // Список категорий
        VBox listBox = new VBox(10);
        listBox.setPrefWidth(300);

        categoryList = new ListView<>();
        VBox.setVgrow(categoryList, Priority.ALWAYS);
        btnRemoveCategory = new Button("Remove Selected");
        btnRemoveCategory.setMaxWidth(Double.MAX_VALUE);

        listBox.getChildren().addAll(new Label("Product Categories (Sectors)"), categoryList, btnRemoveCategory);

        // Добавление категории
        VBox addBox = new VBox(15);
        addBox.setPrefWidth(300);
        addBox.setPadding(new Insets(20));
        addBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        tfNewCategory = styledField("New Category Name");
        btnAddCategory = new Button("Add Category");
        btnAddCategory.setStyle("-fx-background-color: #8B5CF6; -fx-text-fill: white; -fx-font-weight: bold;"); // Фиолетовый цвет
        btnAddCategory.setMaxWidth(Double.MAX_VALUE);

        addBox.getChildren().addAll(new Label("Add New Category"), tfNewCategory, btnAddCategory);

        layout.getChildren().addAll(listBox, addBox);
        return layout;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 6; -fx-border-color: #D1D5DB; -fx-border-radius: 4;");
        return tf;
    }

    // Getters
    public TableView<Product> getProductTable() { return productTable; }
    public TextField getTfName() { return tfName; }
    public ComboBox<String> getCbCategory() { return cbCategory; } // Теперь ComboBox
    public ComboBox<String> getCbSupplier() { return cbSupplier; }
    public TextField getTfBuyPrice() { return tfBuyPrice; }
    public TextField getTfSellPrice() { return tfSellPrice; }
    public TextField getTfQuantity() { return tfQuantity; }
    public TextField getTfSearch() { return tfSearch; }
    public Label getLblAlert() { return lblAlert; }

    public Button getBtnAddProduct() { return btnAddProduct; }
    public Button getBtnUpdateProduct() { return btnUpdateProduct; }
    public Button getBtnDeleteProduct() { return btnDeleteProduct; }
    public Button getBtnClearForm() { return btnClearForm; }

    public ListView<String> getSupplierList() { return supplierList; }
    public TextField getTfNewSupplier() { return tfNewSupplier; }
    public Button getBtnAddSupplier() { return btnAddSupplier; }
    public Button getBtnRemoveSupplier() { return btnRemoveSupplier; }

    // Новые геттеры для категорий
    public ListView<String> getCategoryList() { return categoryList; }
    public TextField getTfNewCategory() { return tfNewCategory; }
    public Button getBtnAddCategory() { return btnAddCategory; }
    public Button getBtnRemoveCategory() { return btnRemoveCategory; }

    public MenuItem getMiLogout() { return miLogoutStub; }
}