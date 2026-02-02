package com.store.model;

import java.time.LocalDate;

public class Cashier extends User {
    private static final long serialVersionUID = 1L;

    public Cashier(String username, String password, String fullName,
                   LocalDate dob, String phone, String email, double salary) {
        super(username, password, fullName, "Cashier", dob, phone, email, salary);
    }
}