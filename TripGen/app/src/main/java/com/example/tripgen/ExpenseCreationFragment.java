package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentExpenseCreationBinding;

import java.util.ArrayList;

public class ExpenseCreationFragment extends Fragment {

    private FragmentExpenseCreationBinding binding;
    private BudgetViewModel budgetViewModel;
    private Budget.Expense selectedExpense = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedExpense = (Budget.Expense) getArguments().getSerializable("selectedExpense");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenseCreationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //TODO: Remove static load budget
        //TODO: Remove static activity ID
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());
        budgetViewModel.loadBudget("Test1");
        String activityID = "Activity1";

        binding.expenseCategorySpinner.setAdapter(new ArrayAdapter<Budget.Category>(requireContext(), android.R.layout.simple_spinner_item, Budget.Category.values()));


        // Modifying an existing expense
        if (selectedExpense != null) {
            int categoryIndex = Budget.Category.valueOf(selectedExpense.getCategory().name()).ordinal();
            binding.expenseCategorySpinner.setSelection(categoryIndex);
            binding.expenseAmountInput.setText(String.format("%.2f", selectedExpense.getAmount()));
            binding.submitExpenseButton.setText("Update Expense");

            binding.deleteExpenseButton.setEnabled(true);
        } else {
            binding.deleteExpenseButton.setEnabled(false);
        }

        binding.deleteExpenseButton.setOnClickListener(v -> {
            budgetViewModel.removeExpense(selectedExpense);
            NavHostFragment.findNavController(ExpenseCreationFragment.this)
                    .navigate(R.id.action_ExpenseCreationFragment_to_ExpenseViewFragment);
        });

        binding.submitExpenseButton.setOnClickListener(v -> {
            double amount = 0f;
            String amountText = binding.expenseAmountInput.getText().toString();

            if (amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Please input an amount", Toast.LENGTH_SHORT).show();
                return;
            } else {
                String[] splitAmount = amountText.split("\\.");
                if (splitAmount.length >= 2 && splitAmount[1].length() > 2) {
                    Toast.makeText(requireContext(), "Please input an amount with up two decimal places", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            try {
                amount = Double.parseDouble(amountText);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Please input an valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            Budget.Category category = Budget.Category.values()[binding.expenseCategorySpinner.getSelectedItemPosition()];

            if (selectedExpense != null) {
                selectedExpense = budgetViewModel.updateExpense(selectedExpense, amount, category);
            } else {
                Budget.Expense expense = new Budget.Expense(category, amount, activityID);
                budgetViewModel.addExpense(expense);
            }

//            NavHostFragment.findNavController(ExpenseCreationFragment.this)
//                    .navigate(R.id.action_BudgetCreationFragment_to_DateFragment);

        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
