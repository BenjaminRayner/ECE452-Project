package com.example.tripgen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BudgetViewModel extends ViewModel {
    private MutableLiveData<Budget> budgetLiveData;

    public BudgetViewModel() {
        budgetLiveData = new MutableLiveData<>();
    }

    public LiveData<Budget> getBudgetLiveData() {
        return budgetLiveData;
    }

    public void setBudget(double transportationBudget, double accommodationBudget, double foodBudget, double activitiesBudget) {
        Budget budget = new Budget(transportationBudget, accommodationBudget, foodBudget, activitiesBudget);
        budgetLiveData.setValue(budget);
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
}
