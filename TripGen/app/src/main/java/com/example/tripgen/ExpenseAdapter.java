package com.example.tripgen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ExpenseAdapter extends BaseAdapter {

    Context context;
    List<Expense> expenses;

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
    }


    @Override
    public int getCount() {
        return expenses.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.expense_layout, viewGroup, false);
        }

        Expense expense = expenses.get(i);

        TextView amountText = view.findViewById(R.id.amountText);
        double amount = expense.price;
        amountText.setText(String.format("$%.2f", amount));

        TextView categoryTag = view.findViewById(R.id.categoryTag);
        categoryTag.setText(expense.category);

        return view;
    }
}
