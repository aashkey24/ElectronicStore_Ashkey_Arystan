package com.store.model;

public class Admin extends User {
    // Добавь salary в параметры
    public Admin(String username, String password, String fullName, String phone, double salary) {
        // Передай salary в super
        super(username, password, fullName, phone, salary);
    }

    @Override
    public String getRole() { return "ADMINISTRATOR"; }
}