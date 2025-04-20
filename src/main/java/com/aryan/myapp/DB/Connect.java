package com.aryan.myapp.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "aryan";
    private static final String PASSWORD = "secret123";

    // Instead of static connection, create a new connection on each request
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("PostgreSQL connection established.");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
        return connection;
    }

    // Close connection if open (to prevent resource leak)
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Connection closed.");
            } catch (SQLException e) {
                System.out.println("❌ Error closing connection: " + e.getMessage());
            }
        }
    }
}
