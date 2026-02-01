package com.store.controller;

import com.store.model.Product;
import com.store.model.User;
import com.store.view.*;
import javafx.scene.control.Alert;

import java.util.List;

public class DashboardController {
    private DashboardView view;
    private User currentUser;
    private List<User> allUsers;
    private List<Product> allProducts;
    private Runnable logoutHandler; // Ссылка на метод выхода в Main

    public DashboardController(DashboardView view, User currentUser,
                               List<User> allUsers, List<Product> allProducts,
                               Runnable logoutHandler) {
        this.view = view;
        this.currentUser = currentUser;
        this.allUsers = allUsers;
        this.allProducts = allProducts;
        this.logoutHandler = logoutHandler;

        initController();
    }

    private void initController() {
        // 1. Логика Logout
        view.getLogoutBtn().setOnAction(e -> {
            if (logoutHandler != null) logoutHandler.run();
        });

        // 2. Логика кнопок меню (проверяем, существует ли кнопка для этой роли)

        if (view.getNavButton("Manage Staff") != null) {
            view.getNavButton("Manage Staff").setOnAction(e -> showAdminPanel());
        }

        if (view.getNavButton("Inventory Management") != null) {
            view.getNavButton("Inventory Management").setOnAction(e -> showManagerPanel());
        }

        if (view.getNavButton("New Sale (POS)") != null) {
            view.getNavButton("New Sale (POS)").setOnAction(e -> showCashierPanel());
        }
    }

    // --- Переключение экранов ---

    private void showAdminPanel() {
        AdminPane p = new AdminPane();
        new AdminController(p, allUsers); // Подключаем контроллер админа
        view.setCenter(p);
    }

    private void showManagerPanel() {
        ManagerView p = new ManagerView();
        new InventoryController(p, allProducts); // Подключаем контроллер менеджера
        view.setCenter(p);
    }

    private void showCashierPanel() {
        CashierPane p = new CashierPane();
        new CashierController(p, allProducts, currentUser); // Подключаем контроллер кассира
        view.setCenter(p);
    }
}