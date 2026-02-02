package com.store.view;

import com.store.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardView extends BorderPane {
    private Button logoutBtn;
    private VBox sidebar;
    private Label titleLabel;

    public DashboardView(User user, Node centerContent) {
        sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #1F2937;");
        VBox profileBox = new VBox(10);
        profileBox.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image(getClass().getResourceAsStream("/com/store/electronicstoreapp/img.png")));
            logo.setFitWidth(80);
            logo.setPreserveRatio(true);
        } catch (Exception e) {}

        Label nameLbl = new Label(user.getFullName());
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label roleLbl = new Label(user.getRole().toUpperCase());
        roleLbl.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px; -fx-background-color: #374151; -fx-padding: 2 8 2 8; -fx-background-radius: 10;");

        profileBox.getChildren().addAll(logo, nameLbl, roleLbl);

        // MENU
        Label menuTitle = new Label("MAIN MENU");
        menuTitle.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10px; -fx-font-weight: bold;");

        // LOGOUT BUTTON
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10;");

        sidebar.getChildren().addAll(profileBox, new Separator(), spacer, logoutBtn);

        // TOP BAR
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        titleLabel = new Label(user.getRole() + " Dashboard");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #1F2937;");

        topBar.getChildren().add(titleLabel);

        setLeft(sidebar);
        setTop(topBar);
        setCenter(centerContent); // AdminPane, ManagerView or CashierPane
    }

    public Button getLogoutBtn() { return logoutBtn; }
}