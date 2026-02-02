package com.store.model;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username, password, fullName, phone, email;
    private LocalDate dateOfBirth;
    private double salary;
    private boolean isActive = true; // Для управления доступом

    public User(String username, String password, String fullName, LocalDate dob, String phone, String email, double salary) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.dateOfBirth = dob;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
    }

    public abstract String getRole();

    // Getters & Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getSalary() { return salary; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}