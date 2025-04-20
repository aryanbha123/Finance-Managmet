package com.aryan.myapp.model;
import java.time.LocalDate;

public class Transaction {
    private int id;
    private String type; // "income" or "expense"
    private String category;
    private double amount;
    private LocalDate date;
    private String description;

    // Constructor
    public Transaction(String type, String category, double amount, LocalDate date, String description) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public Transaction(int id, String type, String category, double amount, LocalDate date, String description) {
        this(type, category, amount, date, description);
        this.id = id;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return type.toUpperCase() + " | " + category + " | ₹" + amount + " | " + date;
    }
}
