package com.store.view;

import com.store.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminPane extends VBox {
    private TableView<User> userTable;
    private TextField tfUsername, tfPassword, tfName, tfPhone, tfEmail, tfSalary;
    private DatePicker dpDob;
    private ComboBox<String> cbRole;
    private Button btnAdd, btnUpdate, btnDelete, btnClear;
    private DatePicker dpStart, dpEnd;
    private Button btnCalculate;
    private Label lblTotalSales, lblTotalCosts, lblProfit;

    private MenuItem miLogoutStub = new MenuItem();

    public AdminPane() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #F3F4F6;");

        // Table
        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(15));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        // expanding the table
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        Label lblTable = new Label("Employee Management");
        lblTable.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // expand inner table
        VBox.setVgrow(userTable, Priority.ALWAYS);

        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Double> colSalary = new TableColumn<>("Salary ($)");
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<User, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        userTable.getColumns().addAll(colName, colRole, colSalary, colPhone);
        tableCard.getChildren().addAll(lblTable, userTable);

        //
        HBox bottomSection = new HBox(20); // We put the Form and Statistics side by side (left and right)
        bottomSection.setPrefHeight(320);

        // INPUT FORM
        VBox formCard = new VBox(15);
        formCard.setPadding(new Insets(20));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        HBox.setHgrow(formCard, Priority.ALWAYS);

        GridPane form = new GridPane();
        form.setHgap(15); form.setVgap(15);

        tfUsername = styledField("Username");
        tfPassword = styledField("Password");
        tfName = styledField("Full Name");
        tfPhone = styledField("Phone");
        tfEmail = styledField("Email");
        tfSalary = styledField("Salary");
        dpDob = new DatePicker(); dpDob.setPromptText("Date of Birth");

        cbRole = new ComboBox<>();
        cbRole.getItems().addAll("Manager", "Cashier", "Administrator");
        cbRole.setValue("Cashier");

        form.addRow(0, new Label("Account:"), tfUsername, tfPassword, cbRole);
        form.addRow(1, new Label("Personal:"), tfName, dpDob, tfPhone);
        form.addRow(2, new Label("Info:"), tfEmail, tfSalary);

        HBox actions = new HBox(15);
        btnAdd = new Button("Add");
        btnAdd.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");
        btnUpdate = new Button("Update");
        btnUpdate.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDelete = new Button("Delete");
        btnDelete.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");
        btnClear = new Button("Clear");

        actions.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClear);
        formCard.getChildren().addAll(new Label("Edit Details"), form, new Separator(), actions);

        // STATISTICS
        VBox statsCard = new VBox(15);
        statsCard.setPadding(new Insets(20));
        statsCard.setPrefWidth(350);
        statsCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label lblStats = new Label("Financial Overview");
        lblStats.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        dpStart = new DatePicker(); dpStart.setPromptText("From"); dpStart.setMaxWidth(Double.MAX_VALUE);
        dpEnd = new DatePicker(); dpEnd.setPromptText("To"); dpEnd.setMaxWidth(Double.MAX_VALUE);
        btnCalculate = new Button("Generate Report");
        btnCalculate.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white;");
        btnCalculate.setMaxWidth(Double.MAX_VALUE);

        GridPane results = new GridPane();
        results.setHgap(10); results.setVgap(10);

        lblTotalSales = new Label("0.0 $"); lblTotalSales.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        lblTotalCosts = new Label("0.0 $"); lblTotalCosts.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblProfit = new Label("0.0 $"); lblProfit.setStyle("-fx-text-fill: blue; -fx-font-weight: bold; -fx-font-size: 14px;");

        results.addRow(0, new Label("Sales:"), lblTotalSales);
        results.addRow(1, new Label("Costs:"), lblTotalCosts);
        results.addRow(2, new Label("Profit:"), lblProfit);

        statsCard.getChildren().addAll(lblStats, dpStart, dpEnd, btnCalculate, new Separator(), results);

        bottomSection.getChildren().addAll(formCard, statsCard);
        getChildren().addAll(tableCard, bottomSection);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 6; -fx-border-color: #D1D5DB; -fx-border-radius: 4;");
        return tf;
    }
    public TableView<User> getUserTable() { return userTable; }
    public TextField getTfUsername() { return tfUsername; }
    public TextField getTfPassword() { return tfPassword; }
    public TextField getTfName() { return tfName; }
    public TextField getTfPhone() { return tfPhone; }
    public TextField getTfEmail() { return tfEmail; }
    public TextField getTfSalary() { return tfSalary; }
    public DatePicker getDpDob() { return dpDob; }
    public ComboBox<String> getCbRole() { return cbRole; }
    public Button getBtnAdd() { return btnAdd; }
    public Button getBtnUpdate() { return btnUpdate; }
    public Button getBtnDelete() { return btnDelete; }
    public Button getBtnClear() { return btnClear; }
    public Button getBtnCalculate() { return btnCalculate; }
    public Label getLblTotalSales() { return lblTotalSales; }
    public Label getLblTotalCosts() { return lblTotalCosts; }
    public Label getLblProfit() { return lblProfit; }
    public DatePicker getDpStart() { return dpStart; }
    public DatePicker getDpEnd() { return dpEnd; }
    public MenuItem getMiLogout() { return miLogoutStub; }
}