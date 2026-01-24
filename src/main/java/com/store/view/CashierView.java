package com.store.view;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

public class CashierView extends VBox {
    public CashierView() {

        setSpacing(20);
        setPadding(new Insets(20));

        Label header = new Label("New Sale (Cashier)");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);

        TextField prodName = new TextField();
        TextField prodQty = new TextField();

        form.add(new Label("Product Name:"), 0, 0);
        form.add(prodName, 1, 0);
        form.add(new Label("Quantity:"), 0, 1);
        form.add(prodQty, 1, 1);

        Button addBtn = new Button("Add to Bill");
        Button printBtn = new Button("Print Bill (.txt)");

        getChildren().addAll(header, form, addBtn, printBtn);
    }
}