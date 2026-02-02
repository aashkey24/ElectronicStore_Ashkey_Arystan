package com.store.model;

import java.time.LocalDate;

public class Manager extends User {
    private static final long serialVersionUID = 1L;

    public Manager(String username, String password, String fullName,
                   LocalDate dob, String phone, String email, double salary) {
        super(username, password, fullName, "Manager", dob, phone, email, salary);
    }
}