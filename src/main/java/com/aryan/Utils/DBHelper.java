package com.aryan.Utils;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DBHelper {

    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        String URL = "jdbc:postgresql://localhost:5432/mydb";
        String USER = "aryan";
        String PASSWORD = "secret123";
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to add an expense to the database
     public static boolean addExpense(int userId, String category, double amount, String date) {
        try (Connection conn = getConnection()) {
            // Ensure the date is in the correct format (ISO 8601: yyyy-MM-dd)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date sqlDate = null;
            
            // Try to parse the date string into a java.sql.Date
            try {
                java.util.Date parsedDate = dateFormat.parse(date); // Parse the date
                sqlDate = new java.sql.Date(parsedDate.getTime());  // Convert to java.sql.Date
            } catch (ParseException e) {
                e.printStackTrace();
                return false;  // Return false if the date format is invalid
            }

            String query = "INSERT INTO expenses (user_id, category, amount, date) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, category);
            ps.setDouble(3, amount);
            ps.setDate(4, sqlDate); // Set the date parameter as java.sql.Date
            int result = ps.executeUpdate();
            return result > 0; // Returns true if the expense was successfully added
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Returns false if there was an error
        }
    }
    // Method to get the distribution of expenses by category
    public static Map<String, Integer> getExpenseDistribution(int userId) {
        Map<String, Integer> expenseDistribution = new HashMap<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT category, SUM(amount) AS total FROM expenses WHERE user_id = ? GROUP BY category";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                int total = rs.getInt("total");
                expenseDistribution.put(category, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenseDistribution;
    }

    // Method to get monthly expenses for a user (year-wise)
    public static Map<String, Integer> getMonthlyExpenses(int userId) {
        Map<String, Integer> monthlyExpenses = new HashMap<>();
        try (Connection conn = getConnection()) {
            // Use EXTRACT function to get the month from the date
            String query = "SELECT EXTRACT(MONTH FROM date) AS month, SUM(amount) AS total FROM expenses WHERE user_id = ? GROUP BY EXTRACT(MONTH FROM date)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int month = rs.getInt("month");
                int total = rs.getInt("total");
                // Convert the month integer to month name (optional, for readability)
                String monthName = getMonthName(month);
                monthlyExpenses.put(monthName, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyExpenses;
    }

    // Helper method to convert month number to month name (e.g., 1 -> January)
    private static String getMonthName(int month) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months[month - 1];
    }

    // Method to get all expenses of a user (for detailed view or any analysis)
    public static Map<String, Object> getAllExpenses(int userId) {
        Map<String, Object> allExpenses = new HashMap<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT category, amount, date FROM expenses WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                String date = rs.getString("date");
                allExpenses.put(date, new Object[] { category, amount });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allExpenses;
    }

    // Method to get total expenses for a user (for overview or dashboard)
    public static double getTotalExpenses(int userId) {
        double total = 0;
        try (Connection conn = getConnection()) {
            String query = "SELECT SUM(amount) AS total FROM expenses WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // Example method for handling any user authentication or session validation
    public static boolean isUserValid(int userId) {
        boolean isValid = false;
        try (Connection conn = getConnection()) {
            String query = "SELECT COUNT(*) AS userCount FROM users WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isValid = rs.getInt("userCount") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }
}
