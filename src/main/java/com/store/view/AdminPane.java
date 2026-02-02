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
    private CheckBox cbIsActive;
    private Button btnAdd, btnUpdate, btnDelete, btnClear;

    private DatePicker dpStart, dpEnd;
    private Button btnCalculate;
    private Label lblTotalSales, lblTotalCosts, lblProfit;

    public AdminPane() {
        setPadding(new Insets(25));
        setSpacing(25);
        setStyle("-fx-background-color: #F9FAFB;"); // Sleek Light Gray

        // --- SECTION 1: USER TABLE CARD ---
        VBox tableCard = new VBox(15);
        tableCard.setPadding(new Insets(20));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        Label lblHeader = new Label("Staff Directory & Access Control");
        lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<User, String> colRole = new TableColumn<>("Position");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<User, Boolean> colStatus = new TableColumn<>("Status (Active)");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));

        userTable.getColumns().addAll(colName, colRole, colStatus);
        tableCard.getChildren().addAll(lblHeader, userTable);

        // --- SECTION 2: FORM & FINANCIALS ---
        HBox bottomSection = new HBox(25);
        bottomSection.setPrefHeight(380);

        // Employee Form
        VBox formCard = new VBox(20);
        formCard.setPadding(new Insets(25));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);");
        HBox.setHgrow(formCard, Priority.ALWAYS);

        GridPane form = new GridPane();
        form.setHgap(20); form.setVgap(15);

        tfUsername = styledField("Username");
        tfPassword = styledField("Password");
        tfName = styledField("Full Name");
        tfPhone = styledField("Phone Number");
        tfEmail = styledField("Email Address");
        tfSalary = styledField("Salary Amount");
        dpDob = new DatePicker(); dpDob.setPromptText("Birthday");
        dpDob.setMaxWidth(Double.MAX_VALUE);

        cbRole = new ComboBox<>();
        cbRole.getItems().addAll("Manager", "Cashier", "Administrator");
        cbRole.setPromptText("Select Role");
        cbRole.setMaxWidth(Double.MAX_VALUE);

        cbIsActive = new CheckBox("Enable System Access");
        cbIsActive.setSelected(true);
        cbIsActive.setStyle("-fx-font-weight: bold; -fx-text-fill: #059669;");

        form.addRow(0, new Label("Login:"), tfUsername, tfPassword, cbRole);
        form.addRow(1, new Label("Personal:"), tfName, dpDob, cbIsActive);
        form.addRow(2, new Label("HR Info:"), tfEmail, tfPhone, tfSalary);

        HBox actions = new HBox(15);
        btnAdd = new Button("Register New");
        btnAdd.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnUpdate = new Button("Update User");
        btnUpdate.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnDelete = new Button("Remove Account");
        btnDelete.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnClear = new Button("Clear");

        actions.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClear);
        formCard.getChildren().addAll(new Label("Profile Details"), form, new Separator(), actions);

        // Financial Analytics Card
        VBox statsCard = new VBox(15);
        statsCard.setPadding(new Insets(25));
        statsCard.setPrefWidth(350);
        statsCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        Label lblStatHeader = new Label("Financial Reporting");
        lblStatHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        dpStart = new DatePicker(); dpStart.setPromptText("Start Date");
        dpEnd = new DatePicker(); dpEnd.setPromptText("End Date");
        btnCalculate = new Button("Generate Analytics");
        btnCalculate.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCalculate.setMaxWidth(Double.MAX_VALUE);

        lblTotalSales = new Label("0.00 $"); lblTotalSales.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 15px;");
        lblTotalCosts = new Label("0.00 $"); lblTotalCosts.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 15px;");
        lblProfit = new Label("0.00 $"); lblProfit.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        statsCard.getChildren().addAll(lblStatHeader, dpStart, dpEnd, btnCalculate, new Separator(),
                new Label("Total Income:"), lblTotalSales,
                new Label("Total Costs:"), lblTotalCosts,
                new Label("Net Profit:"), lblProfit);

        bottomSection.getChildren().addAll(formCard, statsCard);
        getChildren().addAll(tableCard, bottomSection);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 8; -fx-border-color: #E5E7EB; -fx-border-radius: 5; -fx-background-radius: 5;");
        return tf;
    }

    // Getters
    public CheckBox getCbIsActive() { return cbIsActive; }
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
}