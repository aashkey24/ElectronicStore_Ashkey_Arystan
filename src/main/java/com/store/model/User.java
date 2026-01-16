package com.store.model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String fullName;
    private String role; // "Administrator", "Manager", "Cashier"

    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}