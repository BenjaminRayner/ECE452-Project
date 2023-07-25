package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentExpenseCreationBinding;


public class ExpenseCreationFragment extends Fragment {

    private FragmentExpenseCreationBinding binding;
    private BudgetViewModel budgetViewModel;
    private Budget.Expense selectedExpense = null;
    private String activityID = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey("selectedExpense")) {
            selectedExpense = (Budget.Expense) args.getSerializable("selectedExpense");
        }

        if (selectedExpense == null && args != null && args.containsKey("activityID")) {
            activityID = args.getString("activityID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenseCreationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        //TODO: Remove static activity ID
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());

        binding.expenseCategorySpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Budget.Category.values()));


        // Modifying an existing expense
        if (selectedExpense != null) {
            int categoryIndex = Budget.Category.valueOf(selectedExpense.getCategory().name()).ordinal();
            binding.expenseCategorySpinner.setSelection(categoryIndex);
            binding.expenseAmountInput.setText(String.format("%.2f", selectedExpense.getAmount()));
            binding.submitExpenseButton.setText("Update Expense");

            binding.deleteExpenseButton.setEnabled(true);
            binding.deleteExpenseButton.setVisibility(View.VISIBLE);
        } else {
            binding.deleteExpenseButton.setEnabled(false);
            binding.deleteExpenseButton.setVisibility(View.GONE);
        }

        binding.deleteExpenseButton.setOnClickListener(v -> {
            budgetViewModel.removeExpense(selectedExpense);
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
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

            // Updating expense
            if (selectedExpense != null) {
                selectedExpense = budgetViewModel.updateExpense(selectedExpense, amount, category);

            } else {
                Budget.Expense expense = new Budget.Expense(category, amount, activityID);
                budgetViewModel.addExpense(expense);
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();

        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
