package com.store.model;
import java.time.LocalDate;

public class Cashier extends User {
    public Cashier(String username, String password, String fullName, LocalDate dob, String phone, String email, double salary) {
        super(username, password, fullName, dob, phone, email, salary);
    }
    @Override
    public String getRole() { return "Cashier"; }
}