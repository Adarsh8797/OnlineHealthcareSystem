package com.healthcare.service;

import com.healthcare.dao.UserDAO;
import com.healthcare.models.User;

import java.util.List;

/**
 * UserService - Business Logic for User operations
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticate user login
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username cannot be empty");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            System.err.println("Password cannot be empty");
            return null;
        }
        return userDAO.authenticate(username.trim(), password);
    }

    /**
     * Register new user
     */
    public boolean registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().length() < 3) {
            System.err.println("Username must be at least 3 characters");
            return false;
        }
        return userDAO.registerUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
}