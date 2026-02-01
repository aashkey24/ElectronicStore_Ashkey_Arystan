package com.store.main;
import javafx.scene.image.Image;
import com.store.model.*;
import com.store.util.IOHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ElectronicStoreApp extends Application {
    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Stage stage;
    private User currentUser;
//smth
@Override
public void start(Stage primaryStage) {
    this.stage = primaryStage;

    // --- ДОБАВЛЯЕМ ЛОГОТИП КАК ИКОНКУ ПРИЛОЖЕНИЯ ---
    try {
        // Убедитесь, что путь совпадает с папкой resources
        Image icon = new Image(getClass().getResourceAsStream("/com/store/electronicstoreapp/img.png"));
        primaryStage.getIcons().add(icon);
    } catch (Exception e) {
        System.out.println("Logo not found: " + e.getMessage());
    }
    // -----------------------------------------------

    loadData();
    showLogin();
}

    @SuppressWarnings("unchecked")
    private void loadData() {
        // Загрузка пользователей
        List<User> loadedUsers = (List<User>) IOHandler.load("users.dat");

        if (loadedUsers != null && !loadedUsers.isEmpty()) {
            users = loadedUsers;
        } else {
            // Создаем ВСЕХ сотрудников по умолчанию
            users.add(new Admin("admin", "admin1234", "System Administrator"));
            users.add(new Manager("manager", "manager123", "Ilias Manager"));
            users.add(new Cashier("cashier", "cashier123", "Abdulaziz Cashier"));

            IOHandler.save("users.dat", users);
        }

        // Загрузка товаров
        List<Product> loadedProds = (List<Product>) IOHandler.load("products.dat");
        if (loadedProds != null) {
            products = loadedProds;
        } else {
            // ИСПРАВЛЕНИЕ: Добавляем категорию (2-й параметр) в конструктор
            products.add(new Product("iPhone 15", "Smartphone", 999.99, 10));
            products.add(new Product("Laptop HP", "Computers", 550.00, 2));
            IOHandler.save("products.dat", products);
        }
    }

    // --- ЭКРАН ЛОГИНА (MVC Refactored) ---
    private void showLogin() {
        // 1. Используем готовый View
        com.store.view.LoginView loginView = new com.store.view.LoginView();

        // 2. Подключаем Controller
        // Мы передаем ему lambda-выражение: что делать при успешном входе
        new com.store.controller.LoginController(loginView, users, (authenticatedUser) -> {
            this.currentUser = authenticatedUser; // Сохраняем вошедшего юзера
            showDashboard(); // Переходим в меню
        });

        // 3. Показываем сцену
        Scene scene = new Scene(loginView.getRoot(), 400, 350);
        stage.setScene(scene);
        stage.setTitle("Login - Electronics Store");
        stage.centerOnScreen();
        stage.show();
    }

    // --- ГЛАВНОЕ МЕНЮ (DASHBOARD) ---
    private void showDashboard() {
        // 1. Создаем View
        com.store.view.DashboardView dashView = new com.store.view.DashboardView(currentUser);

        // 2. Создаем Controller
        // Мы передаем ему ссылки на списки (users, products) и действие для Logout (this::showLogin)
        new com.store.controller.DashboardController(
                dashView,
                currentUser,
                users,
                products,
                this::showLogin // Это Runnable, который вернет нас на экран логина
        );

        // 3. Показываем
        stage.setScene(new Scene(dashView.getRoot(), 950, 650));
        stage.setTitle("Dashboard - " + currentUser.getRole());
        stage.centerOnScreen();
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: center-left; -fx-padding: 10;");
        return btn;
    }

    // --- ПАНЕЛЬ АДМИНИСТРАТОРА (MVC Refactored) ---
    private VBox createAdminPane() {
        // 1. Создаем View
        com.store.view.AdminPane adminView = new com.store.view.AdminPane();

        // 2. Инициализируем Controller
        // Передаем список users, чтобы контроллер мог добавлять/удалять и сохранять в файл
        new com.store.controller.AdminController(adminView, users);

        // 3. Возвращаем панель
        return adminView;
    }

    // --- ПАНЕЛЬ МЕНЕДЖЕРА (MVC Refactored) ---
    private VBox createManagerPane() {
        // 1. Создаем View
        com.store.view.ManagerView managerView = new com.store.view.ManagerView();

        // 2. Подключаем Controller (он возьмет на себя всю работу)
        // Передаем список продуктов для управления
        new com.store.controller.InventoryController(managerView, products);

        // 3. Возвращаем View
        return managerView;
    }

    // --- ПАНЕЛЬ КАССИРА (С ИСПОЛЬЗОВАНИЕМ КЛАССА BILL) ---
    // --- ПАНЕЛЬ КАССИРА (MVC Refactored) ---
    private VBox createCashierPane() {
        // 1. Создаем View
        com.store.view.CashierPane cashierView = new com.store.view.CashierPane();

        // 2. Инициализируем Controller (он сам свяжет логику с кнопками)
        // Важно: передаем текущий список продуктов и текущего пользователя
        new com.store.controller.CashierController(cashierView, products, currentUser);

        // 3. Возвращаем готовую панель
        return cashierView;
    }

    public static void main(String[] args) {
        //
        launch(args);
    }
}