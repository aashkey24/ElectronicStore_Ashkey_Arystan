package com.store.model;

public class Cashier extends User {

    public Cashier(String username, String password, String fullName, String phone, double salary) {
        super(username, password, fullName, phone, salary);
    }

    @Override
    public String getRole() {
        return "CASHIER";
    }

    @Override
    public String toString() {
        return getFullName() + " (Cashier)";
    }
}