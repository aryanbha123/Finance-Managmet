package com.aryan.myapp.model;

public class Budget {
    private int id;
    private String category;
    private double limitAmount;
    private String monthYear; // e.g. "04-2025"

    public Budget(String category, double limitAmount, String monthYear) {
        this.category = category;
        this.limitAmount = limitAmount;
        this.monthYear = monthYear;
    }

    public Budget(int id, String category, double limitAmount, String monthYear) {
        this(category, limitAmount, monthYear);
        this.id = id;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
}
