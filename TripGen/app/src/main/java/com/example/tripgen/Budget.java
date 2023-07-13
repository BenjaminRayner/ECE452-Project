package com.example.tripgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Budget {
    public enum Category {
        TRANSPORTATION,
        ACCOMMODATION,
        FOOD,
        ACTIVITIES
    }

    private Map<String, List<Expense>> activityExpenseMap;
    private Map<String, CategoryData> categoryDataMap;

    public Budget(double transportationBudget, double accommodationBudget, double foodBudget, double activitiesBudget) {
        activityExpenseMap = new HashMap<>();
        categoryDataMap = new HashMap<>();

        initializeCategory(Category.TRANSPORTATION.name(), transportationBudget);
        initializeCategory(Category.ACCOMMODATION.name(), accommodationBudget);
        initializeCategory(Category.FOOD.name(), foodBudget);
        initializeCategory(Category.ACTIVITIES.name(), activitiesBudget);
    }

    private void initializeCategory(String category, double budget) {
        CategoryData categoryData = new CategoryData(budget);
        categoryDataMap.put(category, categoryData);
    }

    public void setBudget(String category, double budget) {
        CategoryData categoryData = getCategoryData(category);
        categoryData.setBudget(budget);
    }

    public double getBudget(String category) {
        CategoryData categoryData = getCategoryData(category);
        return categoryData.getBudget();
    }

    public double getTotal(String category) {
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

    private CategoryData getCategoryData(String category) {
        return categoryDataMap.get(category);
    }

    public class CategoryData {
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


    public static class Expense {
        private String category;
        private double amount;
        private final String attachedActivity;

        public Expense(String category, double amount, String attachedActivity) {
            this.category = category;
            this.amount = amount;
            this.attachedActivity = attachedActivity;
        }

        public String getCategory() {
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
