package com.example.tripgen;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BudgetViewModel extends ViewModel {
    private MutableLiveData<Budget> budgetLiveData;
    private Context context;
    private Observer<Budget> budgetObserver;

    public BudgetViewModel() {
        budgetLiveData = new MutableLiveData<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public LiveData<Budget> getBudgetLiveData() {
        return budgetLiveData;
    }

    public void setBudget(String trip_ID, double transportationBudget, double accommodationBudget, double foodBudget, double activitiesBudget) {
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
        Budget budget = budgetLiveData.getValue();
        if (budget != null) {
            budget.addExpense(expense);
            budgetLiveData.setValue(budget);
        }
    }

    public void updateExpense(Budget.Expense expense, double amount) {
        Budget budget = budgetLiveData.getValue();
        if (budget != null) {
            budget.updateExpense(expense, amount);
            budgetLiveData.setValue(budget);
        }
    }

    public void loadBudget(String trip_ID) {
        String filePath = context.getFilesDir() + "/" + trip_ID + ".ser";
        Budget budget = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(filePath)))) {
            budget = (Budget) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the exceptions
        }

        removeObserver();
        budgetLiveData.setValue(budget);
        createObserver();
    }

    public void persistBudget() {
        String fileName = budgetLiveData.getValue().getTripID() + ".ser";
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(new File(context.getFilesDir(), fileName).toPath()))) {
            outputStream.writeObject(budgetLiveData.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }
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

    public int getIcon(Budget.Category category) {
        return budgetLiveData.getValue().getIcon(category);
    }

    public double getTotal(Budget.Category category) {
        return budgetLiveData.getValue().getTotal(category);
    }

    public double getBudget(Budget.Category category) {
        return budgetLiveData.getValue().getBudget(category);
    }
}
