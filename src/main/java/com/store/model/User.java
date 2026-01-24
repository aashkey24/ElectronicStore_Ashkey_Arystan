package com.store.model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullName;
    private String phone;

    // ДОБАВЬ ЭТО ПОЛЕ
    protected double salary;

    // Обнови конструктор (добавь salary)
    public User(String username, String password, String fullName, String phone, double salary) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.salary = salary;
    }

    // ДОБАВЬ ЭТОТ МЕТОД (из-за него ошибка)
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    // Остальные геттеры...
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public abstract String getRole();
}