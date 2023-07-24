package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentExpenseViewBinding;

import java.util.List;

public class ExpenseViewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentExpenseViewBinding binding;
    private BudgetViewModel budgetViewModel;
    private List<Budget.Expense> expenseList;
    private String activityID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenseViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());


        binding.listView.setOnItemClickListener(this);


        //TODO: Remove static create budget
        //TODO: Remove static activity ID
        activityID = "Activity1";
//        budgetViewModel.setBudget("Test1", 120, 54, 34, 2);
//        budgetViewModel.addExpense(new Budget.Expense(Budget.Category.ACCOMMODATION, 12, "Activity1"));
//        budgetViewModel.addExpense(new Budget.Expense(Budget.Category.TRANSPORTATION, 123, "Activity1"));
//        budgetViewModel.addExpense(new Budget.Expense(Budget.Category.FOOD, 1.10, "Activity1"));
//        budgetViewModel.addExpense(new Budget.Expense(Budget.Category.ACTIVITIES, 2.18, "Activity1"));
        renderExpenses();


        binding.newExpenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ExpenseViewFragment.this)
                        .navigate(R.id.action_ExpenseViewFragment_to_ExpenseCreationFragment);
            }
        });

        return view;
    }

    public void renderExpenses() {
        expenseList = budgetViewModel.getExpenses(activityID);
        ExpenseAdapter adapter = new ExpenseAdapter(requireActivity(), expenseList);
        binding.listView.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();

        renderExpenses();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = (int)parent.getItemAtPosition(position);
        Budget.Expense selectedExpense = expenseList.get(index);

        Bundle args = new Bundle();
        args.putSerializable("selectedExpense", selectedExpense);
        NavHostFragment.findNavController(ExpenseViewFragment.this)
                .navigate(R.id.action_ExpenseViewFragment_to_ExpenseCreationFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
