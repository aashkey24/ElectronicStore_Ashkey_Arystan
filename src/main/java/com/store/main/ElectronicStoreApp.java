package com.store.main;

import com.store.controller.LoginController;
import com.store.model.*;
import com.store.view.LoginView;
import com.store.util.IOHandler; // <--- Импорт

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class ElectronicStoreApp extends Application {
    private static final String USERS_FILE = "users.dat";

    @Override
    public void start(Stage primaryStage) {
        ensureUsersExist();

        LoginView loginView = new LoginView();
        new LoginController(loginView, primaryStage);

        Scene scene = new Scene(loginView, 900, 600);
        primaryStage.setTitle("Electronics Store Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void ensureUsersExist() {
        // LOAD FROM IOHANDLER
        ArrayList<User> users = IOHandler.loadList(USERS_FILE);

        if (users.isEmpty()) {
            System.out.println("Initializing default users...");

            users.add(new Admin("admin", "admin123", "System Administrator", LocalDate.of(1990, 1, 1), "+1-555-0100", "admin@store.com", 5000.0));
            users.add(new Manager("manager", "manager123", "Store Manager", LocalDate.of(1995, 5, 20), "+1-555-0200", "manager@store.com", 3500.0));
            users.add(new Cashier("cashier", "cashier123", "John Cashier", LocalDate.of(2000, 10, 15), "+1-555-0300", "cashier@store.com", 2500.0));

            // SAVE WITH IOHANDLER
            IOHandler.saveList(USERS_FILE, users);
            System.out.println("Default users created successfully.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}