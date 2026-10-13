package com.healthcare.models;

/**
 * Admin Class - Inherits from User
 * Demonstrates INHERITANCE
 */
public class Admin extends User {

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(int userId, String username, String password,
                 String fullName, String email, String phone) {
        super(userId, username, password, fullName, email, phone, Role.ADMIN);
    }

    // ===== POLYMORPHISM =====
    // Admin ka apna dashboard return karega
    @Override
    public String getDashboardFXML() {
        return "/com/healthcare/ui/admin_dashboard.fxml";
    }
}