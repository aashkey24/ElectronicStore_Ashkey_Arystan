package com.store.view;

import com.store.controller.*;
import com.store.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class DashboardView extends BorderPane {
    private VBox menuBox;
    private Label titleLabel;
    private User currentUser;
    private List<Button> menuButtons = new ArrayList<>();

    private final String SIDEBAR_BG = "#111827";
    private final String IDLE_STYLE = "-fx-background-color: transparent; -fx-text-fill: #D1D5DB; -fx-padding: 12 20; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px;";
    private final String HOVER_STYLE = "-fx-background-color: #374151; -fx-text-fill: white; -fx-padding: 12 20; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px;";
    private final String ACTIVE_STYLE = "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-border-color: #60A5FA; -fx-border-width: 0 0 0 5;";

    public DashboardView(User user, Node initialContent) {
        this.currentUser = user;

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 0, 20, 0));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        VBox profileBox = new VBox(12);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 20, 0));

        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image(getClass().getResourceAsStream("/com/store/electronicstoreapp/img.png")));
            logo.setFitWidth(80);
            logo.setPreserveRatio(true);
        } catch (Exception e) {}

        Label nameLbl = new Label(user.getFullName());
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label roleLbl = new Label(user.getRole().toUpperCase());
        roleLbl.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-background-color: #374151; -fx-padding: 3 10; -fx-background-radius: 10;");
        profileBox.getChildren().addAll(logo, nameLbl, roleLbl);
        menuBox = new VBox(5);
        Label sectionHeader = new Label("  MAIN NAVIGATION");
        sectionHeader.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 5 10;");
        menuBox.getChildren().add(sectionHeader);
        Button defaultActive = null;
        if (user.getRole().equals("Administrator")) {
            defaultActive = createMenuButton("Staff & Security", "admin");
            createMenuButton("Inventory Management", "manager");
            createMenuButton("Store Checkout (POS)", "cashier");
        } else if (user.getRole().equals("Manager")) {
            defaultActive = createMenuButton("Inventory Control", "manager");
        } else {
            defaultActive = createMenuButton("Sales Terminal", "cashier");
        }

        if (defaultActive != null) setActiveButton(defaultActive);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand; -fx-background-radius: 0;");
        logoutBtn.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(profileBox, new Separator(), menuBox, spacer, logoutBtn);
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        String initialTitle = user.getRole().equals("Administrator") ? "System Administration" :
                (user.getRole().equals("Manager") ? "Inventory Management" : "Sales Terminal");
        titleLabel = new Label(initialTitle);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #111827;");
        topBar.getChildren().add(titleLabel);

        setLeft(sidebar);
        setTop(topBar);
        setCenter(initialContent);
    }

    private Button createMenuButton(String text, String type) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(IDLE_STYLE);

        btn.setOnMouseEntered(e -> { if(!btn.getStyle().contains("#2563EB")) btn.setStyle(HOVER_STYLE); });
        btn.setOnMouseExited(e -> { if(!btn.getStyle().contains("#2563EB")) btn.setStyle(IDLE_STYLE); });

        btn.setOnAction(e -> {
            setActiveButton(btn);
            switch (type) {
                case "admin":
                    AdminPane ap = new AdminPane();
                    new AdminController(ap, currentUser);
                    setCenter(ap);
                    titleLabel.setText("Employee Management & Security");
                    break;
                case "manager":
                    ManagerView mv = new ManagerView();
                    new ManagerController(mv, currentUser);
                    setCenter(mv);
                    titleLabel.setText("Stock & Inventory Control");
                    break;
                case "cashier":
                    CashierPane cp = new CashierPane();
                    new CashierController(cp, currentUser);
                    setCenter(cp);
                    titleLabel.setText("Active Sales Terminal");
                    break;
            }
        });

        menuButtons.add(btn);
        menuBox.getChildren().add(btn);
        return btn;
    }

    private void setActiveButton(Button target) {
        for (Button b : menuButtons) b.setStyle(IDLE_STYLE);
        target.setStyle(ACTIVE_STYLE);
    }

    private void handleLogout() {
        Stage stage = (Stage) this.getScene().getWindow();
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage);
        stage.getScene().setRoot(loginView);
        stage.setTitle("Staff Login");
        stage.centerOnScreen();
    }
}