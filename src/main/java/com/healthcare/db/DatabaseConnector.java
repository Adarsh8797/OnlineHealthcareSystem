package com.healthcare.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connector Class
 * Handles JDBC connection to MySQL database
 */
public class DatabaseConnector {

    // ⚠️ CHANGE THESE VALUES according to your MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/healthcare_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin123";  //

    /**
     * Gets a connection to the MySQL database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver missing");
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Test the database connection
     */
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connected successfully!");
                System.out.println("Database: " + conn.getCatalog());
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
        }
    }
}