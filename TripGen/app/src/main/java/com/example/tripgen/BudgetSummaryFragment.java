package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.FragmentBudgetSummaryBinding;


public class BudgetSummaryFragment extends Fragment {

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
        recyclerView.setAdapter(categoryAdapter);

        // Observe the budgetLiveData
        budgetViewModel.getBudgetLiveData().observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget newBudgetValue) {
                // Update the UI with the new budget value
                // For example, update the adapter or any other UI elements you want
                categoryAdapter.notifyDataSetChanged();
            }
        });

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
