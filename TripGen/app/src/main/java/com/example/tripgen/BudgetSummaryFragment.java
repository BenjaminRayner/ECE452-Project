package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.FragmentBudgetSummaryBinding;


public class BudgetSummaryFragment extends Fragment implements CategoryAdapter.OnItemClickListener{

    private FragmentBudgetSummaryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetSummaryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        BudgetViewModel budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());

        // Initialize the RecyclerView and set its adapter
        RecyclerView recyclerView = binding.categoriesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), budgetViewModel);
        categoryAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(categoryAdapter);

        // Observe the budgetLiveData
        budgetViewModel.getBudgetLiveData().observe(getViewLifecycleOwner(), newBudgetValue -> {
            categoryAdapter.notifyDataSetChanged();
        });

        return view;

    }

    @Override
    public void onItemClick(Budget.Category category) {
        NavController navController = NavHostFragment.findNavController(this);
        int currentDestinationId = navController.getCurrentDestination().getId();

        int actionId;
        if (currentDestinationId == R.id.DateFragment) {
            actionId = R.id.action_DateFragment_to_BudgetTotalFragment;
        } else if (currentDestinationId == R.id.ExpenseViewFragment) {
            actionId = R.id.action_ExpenseViewFragment_to_BudgetTotalFragment;
        } else if (currentDestinationId == R.id.ExpenseCreationFragment) {
            actionId = R.id.action_ExpenseCreationFragment_to_BudgetTotalFragment;
        } else {
            return;
        }

        navController.navigate(actionId);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
