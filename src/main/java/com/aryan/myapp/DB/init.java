package com.aryan.myapp.DB;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class init {

    // Method to create necessary tables in the database
    public static void createTables() {
        // SQL query to create 'users' table (PostgreSQL version)
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id SERIAL PRIMARY KEY, "  // Use SERIAL for auto-increment
                + "username VARCHAR(100) NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "email VARCHAR(100) NOT NULL UNIQUE);";

        // SQL query to create 'expenses' table (PostgreSQL version)
        String createExpensesTable = "CREATE TABLE IF NOT EXISTS expenses ("
                + "expense_id SERIAL PRIMARY KEY, "  // Use SERIAL for auto-increment
                + "user_id INT, "
                + "category VARCHAR(100), "
                + "amount DOUBLE PRECISION, "
                + "date DATE, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE);";

        // Database connection and statement setup
        try (Connection conn = Connect.getConnection(); 
             Statement stmt = conn.createStatement()) {

            // Create 'users' table
            stmt.executeUpdate(createUsersTable);
            System.out.println("Users table created successfully.");

            // Create 'expenses' table
            stmt.executeUpdate(createExpensesTable);
            System.out.println("Expenses table created successfully.");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
