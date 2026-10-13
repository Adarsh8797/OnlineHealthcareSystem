package com.healthcare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application Entry Point
 * Online Healthcare Management System
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load Login Screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/healthcare/ui/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                getClass().getResource("/com/healthcare/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle("Healthcare System - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        System.out.println("✅ Login screen loaded successfully!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}