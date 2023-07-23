package com.example.tripgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Budget implements Serializable {
    public int getIcon(Category category) {
        int iconResId;
        switch (category) {
            case TRANSPORTATION:
                iconResId = R.drawable.transportation;
                break;
            case ACCOMMODATION:
                iconResId = R.drawable.accommodation;
                break;
            case FOOD:
                iconResId = R.drawable.food;
                break;
            case ACTIVITIES:
                iconResId = R.drawable.activity;
                break;
            default:
                // If the category is not found, use a default icon resource
                iconResId = R.drawable.transportation;
                break;
        }
        return iconResId;

    }

    public String getTripID() {
        return trip_ID;
    }

    public enum Category {
        TRANSPORTATION,
        ACCOMMODATION,
        FOOD,
        ACTIVITIES
    }

    private Map<String, List<Expense>> activityExpenseMap;
    private Map<String, CategoryData> categoryDataMap;
    private String trip_ID;

    public Budget(String trip_ID, double transportationBudget, double accommodationBudget, double foodBudget, double activitiesBudget) {
        this.trip_ID = trip_ID;
        activityExpenseMap = new HashMap<>();
        categoryDataMap = new HashMap<>();

        initializeCategory(Category.TRANSPORTATION, transportationBudget);
        initializeCategory(Category.ACCOMMODATION, accommodationBudget);
        initializeCategory(Category.FOOD, foodBudget);
        initializeCategory(Category.ACTIVITIES, activitiesBudget);
    }



    private void initializeCategory(Category category, double budget) {
        CategoryData categoryData = new CategoryData(budget);
        categoryDataMap.put(category.name(), categoryData);
    }

    public void setBudget(Category category, double budget) {
        CategoryData categoryData = getCategoryData(category);
        categoryData.setBudget(budget);
    }

    public double getBudget(Category category) {
        CategoryData categoryData = getCategoryData(category);
        return categoryData.getBudget();
    }

    public double getTotal(Category category) {
        CategoryData categoryData = getCategoryData(category);
        return categoryData.getTotal();
    }

    public void addExpense(Expense expense) {
        List<Expense> expenses = activityExpenseMap.computeIfAbsent(expense.getAttachedActivity(), k -> new ArrayList<>());
        expenses.add(expense);

        CategoryData categoryData = getCategoryData(expense.getCategory());
        categoryData.setTotal(categoryData.getTotal() + expense.getAmount());
    }

    public void updateExpense(Expense expense, double amount) {
        double difference = amount - expense.getAmount();
        expense.setAmount(amount);

        CategoryData categoryData = getCategoryData(expense.getCategory());
        categoryData.setTotal(categoryData.getTotal() + difference);
    }

    public void removeExpense(Expense expense) {
        List<Expense> expenses = activityExpenseMap.get(expense.getAttachedActivity());

        expenses.remove(expense);

        CategoryData categoryData = getCategoryData(expense.getCategory());
        categoryData.setTotal(categoryData.getTotal() - expense.getAmount());
    }

    public List<Expense> getExpensesForActivity(String activityId) {
        return activityExpenseMap.getOrDefault(activityId, Collections.emptyList());
    }

    private CategoryData getCategoryData(Category category) {
        return categoryDataMap.get(category.name());
    }

    public class CategoryData implements Serializable {
        private double budget;
        private double total;

        public CategoryData(double budget) {
            this.budget = budget;
            this.total = 0;
        }

        public double getBudget() {
            return budget;
        }

        public void setBudget(double budget) {
            this.budget = budget;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }


    public static class Expense implements Serializable {
        private Category category;
        private double amount;
        private final String attachedActivity;

        public Expense(Category category, double amount, String attachedActivity) {
            this.category = category;
            this.amount = amount;
            this.attachedActivity = attachedActivity;
        }

        public Category getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getAttachedActivity() {
            return attachedActivity;
        }
    }
}
