package com.example.tripgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Budget {
    public enum Category {
        TRANSPORTATION("Transportation", R.drawable.transportation),
        ACCOMMODATION("Accommodation", R.drawable.accommodation),
        FOOD("Food", R.drawable.food),
        ACTIVITIES("Activites", R.drawable.activity);

        private String friendlyName;

        private int iconResId;
        private Category(String friendlyName, int iconResId){
            this.friendlyName = friendlyName;
            this.iconResId = iconResId;
        }

        @Override public String toString(){
            return friendlyName;
        }

    }
    private String trip_ID;
    private Map<String, List<Expense>> activityExpenseMap;
    private Map<String, CategoryData> categoryDataMap;
    public Budget(String trip_ID, int transportationBudget, int accommodationBudget, int foodBudget, int activitiesBudget) {
        this.trip_ID = trip_ID;
        activityExpenseMap = new HashMap<>();
        categoryDataMap = new HashMap<>();

        initializeCategory(Category.TRANSPORTATION, transportationBudget);
        initializeCategory(Category.ACCOMMODATION, accommodationBudget);
        initializeCategory(Category.FOOD, foodBudget);
        initializeCategory(Category.ACTIVITIES, activitiesBudget);
    }

    private void initializeCategory(Category category, int budget) {
        CategoryData categoryData = new CategoryData(budget);
        categoryDataMap.put(category.toString(), categoryData);
    }

    public int getIcon(Category category) {
        return category.iconResId;
    }

    public String getTripID() {
        return trip_ID;
    }

    public void setBudget(Category category, int budget) {
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

    public Expense updateExpense(Expense expense, double amount, Category category) {
        // Remove from old
        CategoryData categoryData = getCategoryData(expense.getCategory());
        categoryData.setTotal(categoryData.getTotal() - expense.getAmount());

        // Update fields
        categoryData = getCategoryData(category);
        expense.setAmount(amount);
        expense.setCategory(category);

        // Add to new
        categoryData.setTotal(categoryData.getTotal() + expense.getAmount());

        return expense;
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
        return categoryDataMap.get(category.toString());
    }

    public class CategoryData {
        private int budget;
        private double total;

        public CategoryData(int budget) {
            this.budget = budget;
            this.total = 0;
        }

        public int getBudget() {
            return budget;
        }

        public void setBudget(int budget) {
            this.budget = budget;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }


    public static class Expense implements Serializable{
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

        public void setCategory(Category category) {
            this.category = category;
        }
    }
}
