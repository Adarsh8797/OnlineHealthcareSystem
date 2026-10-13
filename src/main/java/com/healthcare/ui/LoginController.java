package com.healthcare.ui;

import com.healthcare.models.User;
import com.healthcare.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Login Controller - Handles login screen logic
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty()) {
            errorLabel.setText("⚠ Please enter username");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            errorLabel.setText("⚠ Please enter password");
            return;
        }

        User user = userService.login(username.trim(), password);

        if (user == null) {
            errorLabel.setText("❌ Invalid username or password");
            return;
        }

        System.out.println("✅ Login success: " + user.getFullName() + " (" + user.getRole() + ")");
        errorLabel.setText("");
        navigateToDashboard(user);
    }

    private void navigateToDashboard(User user) {
        try {
            String fxmlPath = user.getDashboardFXML();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            try {
                controller.getClass().getMethod("setUser", User.class).invoke(controller, user);
            } catch (NoSuchMethodException e) {
                // Controller doesn't have setUser — ignore
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/com/healthcare/css/styles.css").toExternalForm());

            stage.setTitle("Healthcare System - " + user.getRole());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Dashboard load failed: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("⚠ Dashboard load failed. Check console.");
        }
    }
}