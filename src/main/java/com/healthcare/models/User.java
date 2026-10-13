package com.healthcare.models;

/**
 * Abstract User Class
 * Parent class for Admin, Doctor, Patient (INHERITANCE)
 * Demonstrates OOP - Abstraction & Inheritance
 */
public abstract class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private Role role;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(int userId, String username, String password, String fullName,
                String email, String phone, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // ===== ABSTRACT METHOD =====
    // Har child class apna dashboard path return karega (POLYMORPHISM)
    public abstract String getDashboardFXML();

    // ===== GETTERS & SETTERS =====
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}