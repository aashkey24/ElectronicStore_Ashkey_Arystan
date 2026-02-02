package com.store.model;
import java.time.LocalDate;

public class Admin extends User {
    public Admin(String username, String password, String fullName, LocalDate dob, String phone, String email, double salary) {
        super(username, password, fullName, "Administrator", dob, phone, email, salary);
    }
}