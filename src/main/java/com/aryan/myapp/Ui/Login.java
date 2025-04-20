package com.aryan.myapp.Ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.*;
import java.nio.charset.StandardCharsets;
import javax.swing.border.EmptyBorder;
import com.aryan.myapp.DB.Connect;

public class Login {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::createLoginScreen);
    }

    public static void createLoginScreen() {
        JFrame frame = new JFrame("Login");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 250, 200));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        setTextFieldStyle(usernameField);
        setTextFieldStyle(passwordField);
        setButtonStyle(loginButton, Color.BLACK);  // Black button background
        setButtonStyle(registerButton, Color.BLACK);  // Black button background

        panel.add(createLabeledPanel("Username:", usernameField));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createLabeledPanel("Password:", passwordField));
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButtonPanel(loginButton));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButtonPanel(registerButton));

        frame.add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            int userId = validateLogin(username, password);  // Get the user_id

            if (userId != -1) {
                frame.dispose();
                JOptionPane.showMessageDialog(frame, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                Dashboard.createDashboardScreen(userId);  // Pass user_id to the dashboard
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            createRegisterScreen();
        });

        frame.setVisible(true);
    }

    private static JPanel createLabeledPanel(String label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    private static JPanel createButtonPanel(JButton button) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(button);
        return panel;
    }

    private static void setTextFieldStyle(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                field.getBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private static void setButtonStyle(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);  // Set button background to black
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    // Validate login and return user_id if successful, -1 if failed
    private static int validateLogin(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int userId = -1;  // Initialize to -1 (invalid)

        try {
            conn = Connect.getConnection();
            String query = "SELECT user_id, password FROM users WHERE username = ?";  // Assuming the column name for user ID is 'user_id'
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String inputPasswordHash = hashPassword(password);  // Hash the input password

                if (storedPassword.equals(inputPasswordHash)) {
                    userId = rs.getInt("user_id");  // Retrieve the user_id
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }

        return userId;  // Return user_id or -1 if login failed
    }

    public static void createRegisterScreen() {
        JFrame frame = new JFrame("Register");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 250, 200));

        JTextField usernameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        setTextFieldStyle(usernameField);
        setTextFieldStyle(emailField);
        setTextFieldStyle(passwordField);
        setButtonStyle(registerButton, Color.BLACK);  // Black button background
        setButtonStyle(backButton, Color.BLACK);  // Black button background

        panel.add(createLabeledPanel("Username:", usernameField));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createLabeledPanel("Email:", emailField));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createLabeledPanel("Password:", passwordField));
        panel.add(Box.createVerticalStrut(20));
        panel.add(createButtonPanel(registerButton));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createButtonPanel(backButton));

        frame.add(panel, BorderLayout.CENTER);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String hashedPassword = hashPassword(password);

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = Connect.getConnection();
                String insertQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";  // Adjusted column name to 'password'
                stmt = conn.prepareStatement(insertQuery);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, hashedPassword);  // Store the hashed password

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    createLoginScreen();
                } else {
                    JOptionPane.showMessageDialog(frame, "Registration Failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (stmt != null) stmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            createLoginScreen();
        });

        frame.setVisible(true);
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
