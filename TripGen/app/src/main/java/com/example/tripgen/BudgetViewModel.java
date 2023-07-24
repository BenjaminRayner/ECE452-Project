package com.example.tripgen;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BudgetViewModel extends ViewModel {
    private MutableLiveData<Budget> budgetLiveData;
    private Context context;
    private Observer<Budget> budgetObserver;
    private Gson gson;


    public BudgetViewModel() {
        budgetLiveData = new MutableLiveData<>();
        gson = new Gson();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public LiveData<Budget> getBudgetLiveData() {
        return budgetLiveData;
    }

    @Override
    protected void onCleared() {
        // Remove the observer when the ViewModel is cleared to avoid potential memory leaks
        removeObserver();
        super.onCleared();
    }

    private void removeObserver() {
        if (budgetObserver != null) {
            budgetLiveData.removeObserver(budgetObserver);
            budgetObserver = null;
        }
    }

    public void persistBudget() {
        Budget budget = getLiveBudget();
        String fileName = budget.getTripID() + ".json";
        try (FileWriter writer = new FileWriter(new File(context.getFilesDir(), fileName))) {
            gson.toJson(budget, writer);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
    }

    public void loadBudget(String trip_ID) {
        String filePath = context.getFilesDir() + "/" + trip_ID + ".json";
        Budget budget = null;
        try (FileReader reader = new FileReader(filePath)) {
            budget = gson.fromJson(reader, Budget.class);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exceptions
        }

        removeObserver();
        budgetLiveData.setValue(budget);
        createObserver();
    }

    public boolean validBudget() {
        return budgetLiveData.getValue() != null;
    }

    public void createBudget(String trip_ID, int transportationBudget, int accommodationBudget, int foodBudget, int activitiesBudget) {
        Budget budget = new Budget(trip_ID, transportationBudget, accommodationBudget, foodBudget, activitiesBudget);
        removeObserver();
        budgetLiveData.setValue(budget);

        createObserver();
        persistBudget();
    }

    private void createObserver() {
        // Observe the LiveData holding the budget data
        budgetObserver = newBudget -> {
            // Whenever the budget data changes, save it here.
            // For example, you can call a saveDataToStorage() method on the budget object.
            persistBudget();
        };
        budgetLiveData.observeForever(budgetObserver);
    }

    public void addExpense(Budget.Expense expense) {
        Budget budget = getLiveBudget();
        budget.addExpense(expense);
        budgetLiveData.setValue(budget);
    }

    public Budget.Expense updateExpense(Budget.Expense expense, double amount, Budget.Category category) {
        Budget budget = getLiveBudget();
        expense = budget.updateExpense(expense, amount, category);
        budgetLiveData.setValue(budget);

        return expense;
    }

    public List<Budget.Expense> getExpenses(String activity_ID) {
        return getLiveBudget().getExpensesForActivity(activity_ID);
    }


    public int getIcon(Budget.Category category) {
        return getLiveBudget().getIcon(category);
    }


    public void removeExpense(Budget.Expense expense) {
        getLiveBudget().removeExpense(expense);
    }

    public double getTotal(Budget.Category category) {
        return getLiveBudget().getTotal(category);
    }

    public double getBudget(Budget.Category category) {
        return getLiveBudget().getBudget(category);
    }

    private Budget getLiveBudget() {
        if (budgetLiveData.getValue() == null) throw new IllegalStateException("Budget is null");
        return budgetLiveData.getValue();
    }


    public void updateBudget(int transportationBudget, int accommodationBudget, int activityBudget, int foodBudget) {
        Budget budget = getLiveBudget();

        budget.setBudget(Budget.Category.TRANSPORTATION, transportationBudget);
        budget.setBudget(Budget.Category.ACCOMMODATION, accommodationBudget);
        budget.setBudget(Budget.Category.ACTIVITIES, activityBudget);
        budget.setBudget(Budget.Category.FOOD, foodBudget);

        budgetLiveData.setValue(budget);
    }
}