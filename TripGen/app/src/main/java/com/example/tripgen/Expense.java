package com.example.tripgen;

public class Expense {
    String category;
    double price;

    Expense() {}
    Expense(String category, double price) {
        this.category = category;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}
