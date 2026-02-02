package com.store.main;

import com.store.controller.LoginController;
import com.store.model.Admin;
import com.store.model.Cashier;
import com.store.model.Manager;
import com.store.model.User;
import com.store.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ElectronicStoreApp extends Application {
    private static final String USERS_FILE = "users.dat";

    @Override
    public void start(Stage primaryStage) {
        // 1. Проверяем и создаем дефолтных пользователей (Admin, Manager, Cashier)
        ensureUsersExist();

        // 2. Запускаем окно логина
        LoginView loginView = new LoginView();
        new LoginController(loginView, primaryStage);

        Scene scene = new Scene(loginView, 900, 600); // Чуть увеличили стартовый размер
        primaryStage.setTitle("Electronics Store Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void ensureUsersExist() {
        File file = new File(USERS_FILE);
        ArrayList<User> users = new ArrayList<>();

        // Пытаемся прочитать существующих пользователей
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (ArrayList<User>) ois.readObject();
            } catch (Exception e) {
                System.out.println("User file corrupted or empty. Creating new defaults.");
            }
        }

        // Если пользователей нет (первый запуск или файл удален) -> СОЗДАЕМ ТРОИХ
        if (users.isEmpty()) {
            System.out.println("Initializing default users...");

            // 1. Admin
            Admin admin = new Admin(
                    "admin",
                    "admin123",
                    "System Administrator",
                    LocalDate.of(1990, 1, 1),
                    "+1-555-0100",
                    "admin@store.com",
                    5000.0
            );

            // 2. Manager
            Manager manager = new Manager(
                    "manager",
                    "manager123",
                    "Store Manager",
                    LocalDate.of(1995, 5, 20),
                    "+1-555-0200",
                    "manager@store.com",
                    3500.0
            );

            // 3. Cashier
            Cashier cashier = new Cashier(
                    "cashier",
                    "cashier123",
                    "John Cashier",
                    LocalDate.of(2000, 10, 15),
                    "+1-555-0300",
                    "cashier@store.com",
                    2500.0
            );

            users.add(admin);
            users.add(manager);
            users.add(cashier);

            saveUsers(users);
            System.out.println("Default users created successfully.");
        }
    }

    private void saveUsers(ArrayList<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}