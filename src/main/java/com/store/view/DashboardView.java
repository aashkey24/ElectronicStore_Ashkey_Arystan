package com.store.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardView {
    private BorderPane root;
    private VBox sidebar;
    private Button logoutBtn;
    private StackPane contentArea;
    private Map<String, Button> navButtons = new HashMap<>();

    public DashboardView(String role, String name) {
        root = new BorderPane();
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(200);

        Label userLabel = new Label("User: " + name);
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        sidebar.getChildren().addAll(userLabel, new Separator());

        // Динамические кнопки на основе роли
        if (role.equals("Administrator")) addNavBtn("Manage Staff");
        if (role.equals("Manager") || role.equals("Administrator")) addNavBtn("Inventory");
        if (role.equals("Cashier") || role.equals("Administrator")) addNavBtn("New Sale");

        logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(spacer, logoutBtn);

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.getChildren().add(new Label("Welcome! Please select an action from the menu."));

        root.setLeft(sidebar);
        root.setCenter(contentArea);
    }

    private void addNavBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand;");
        sidebar.getChildren().add(b);
        navButtons.put(text, b);
    }

    public BorderPane getRoot() { return root; }
    public Button getLogoutBtn() { return logoutBtn; }
    public Map<String, Button> getNavButtons() { return navButtons; }
    public void setCenterContent(javafx.scene.Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }
}