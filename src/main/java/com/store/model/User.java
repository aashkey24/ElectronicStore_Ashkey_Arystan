package com.store.model;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private double salary;

    public User(String username, String password, String fullName, String role,
                LocalDate dateOfBirth, String phone, String email, double salary) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
    }

    // Геттеры
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getSalary() { return salary; }

    // Сеттеры (важны для редактирования)
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String toString() { return fullName + " (" + role + ")"; }
}