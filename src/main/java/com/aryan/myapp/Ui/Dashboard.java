package com.aryan.myapp.Ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Calendar;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.aryan.Utils.DBHelper;
import com.aryan.Utils.GeminiHelper;

public class Dashboard {

    public static void createDashboardScreen(int userId) {
        JFrame frame = new JFrame("Dashboard - " + userId);
        frame.setSize(1200, 800);  // Increased size for more space
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, User ID: " + userId + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(welcomeLabel, BorderLayout.NORTH);

        // Charts Panel
        JPanel chartPanel = new JPanel(new GridLayout(1, 2));
        chartPanel.add(createPieChartPanel(userId));
        chartPanel.add(createBarChartPanel(userId));
        frame.add(chartPanel, BorderLayout.CENTER);

        // Gemini Panel
        JPanel geminiPanel = new JPanel(new BorderLayout());
        geminiPanel.setBorder(BorderFactory.createTitledBorder("Gemini AI Insights"));

        JButton geminiButton = new JButton("Get AI Insights");
        JTextArea aiOutput = new JTextArea("Click the button to get financial tips from Gemini AI...");
        aiOutput.setLineWrap(true);
        aiOutput.setWrapStyleWord(true);
        aiOutput.setEditable(false);
        aiOutput.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        aiOutput.setPreferredSize(new Dimension(500, 400)); // Adjusted size for larger view

        geminiButton.addActionListener(e -> {
            Map<String, Integer> dist = DBHelper.getExpenseDistribution(userId);
            StringBuilder summary = new StringBuilder();
            for (String cat : dist.keySet()) {
                summary.append(cat).append(": ").append(dist.get(cat)).append(" INR\n");
            }
            aiOutput.setText("Loading insights from Gemini...");
            new Thread(() -> {
                String res = GeminiHelper.getFinancialAdvice(summary.toString());
                SwingUtilities.invokeLater(() -> aiOutput.setText(res));
            }).start();
        });

        geminiPanel.add(geminiButton, BorderLayout.NORTH);
        geminiPanel.add(new JScrollPane(aiOutput), BorderLayout.CENTER);

        frame.add(geminiPanel, BorderLayout.EAST);

        // South Panel with Add Expense + Logout
        JButton addExpenseButton = new JButton("Add Expense");
        addExpenseButton.addActionListener(e -> openAddExpenseDialog(userId, frame));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            Login.createLoginScreen();
        });

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(addExpenseButton);
        southPanel.add(logoutButton);

        frame.add(southPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void openAddExpenseDialog(int userId, JFrame parentFrame) {
        JDialog addExpenseDialog = new JDialog(parentFrame, "Add Expense", true);
        addExpenseDialog.setSize(400, 300);
        addExpenseDialog.setLayout(new GridLayout(6, 2));

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Food", "Transport", "Rent", "Utilities", "Entertainment" , "Others"});

        JLabel amountLabel = new JLabel("Amount (INR):");
        JTextField amountField = new JTextField();

        JLabel dateLabel = new JLabel("Date:");
        JComboBox<String> dateComboBox = new JComboBox<>(generateDateOptions());

        JButton submitButton = new JButton("Add Expense");
        submitButton.addActionListener(e -> {
            String category = (String) categoryComboBox.getSelectedItem();
            String amount = amountField.getText();
            String date = (String) dateComboBox.getSelectedItem();

            if (!category.isEmpty() && !amount.isEmpty() && !date.isEmpty()) {
                try {
                    double expenseAmount = Double.parseDouble(amount);
                    boolean success = DBHelper.addExpense(userId, category, expenseAmount, date);
                    if (success) {
                        JOptionPane.showMessageDialog(addExpenseDialog, "Expense added successfully!");
                        addExpenseDialog.dispose();
                        parentFrame.dispose(); // Close current dashboard frame
                        createDashboardScreen(userId); // Full refresh
                    } else {
                        JOptionPane.showMessageDialog(addExpenseDialog, "Failed to add expense. Try again.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addExpenseDialog, "Invalid amount format.");
                }
            } else {
                JOptionPane.showMessageDialog(addExpenseDialog, "All fields are required.");
            }
        });

        addExpenseDialog.add(categoryLabel);
        addExpenseDialog.add(categoryComboBox);
        addExpenseDialog.add(amountLabel);
        addExpenseDialog.add(amountField);
        addExpenseDialog.add(dateLabel);
        addExpenseDialog.add(dateComboBox);
        addExpenseDialog.add(new JLabel());
        addExpenseDialog.add(submitButton);

        addExpenseDialog.setVisible(true);
    }

    private static String[] generateDateOptions() {
        // Set the year as needed
        int year = 2025;

        // Generate dates for March and April
        String[] dateOptions = new String[31 + 30]; // 31 for March and 30 for April
        int index = 0;

        // For March (31 days)
        for (int i = 1; i <= 31; i++) {
            dateOptions[index++] = String.format("%04d-03-%02d", year, i);
        }

        // For April (30 days)
        for (int i = 1; i <= 30; i++) {
            dateOptions[index++] = String.format("%04d-04-%02d", year, i);
        }

        return dateOptions;
    }

    private static JPanel createPieChartPanel(int userId) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> data = DBHelper.getExpenseDistribution(userId);
        for (String cat : data.keySet()) {
            dataset.setValue(cat, data.get(cat));
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Distribution", dataset, true, true, false);
        return new ChartPanel(chart);
    }

    private static JPanel createBarChartPanel(int userId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> data = DBHelper.getMonthlyExpenses(userId);
        for (String month : data.keySet()) {
            dataset.addValue(data.get(month), "Expenses", month);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Spending", "Month", "Amount (INR)",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        return new ChartPanel(chart);
    }
}
