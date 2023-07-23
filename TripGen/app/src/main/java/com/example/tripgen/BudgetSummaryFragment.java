package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
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

        // TODO: Should be replaced with:
        // TODO: budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        // TODO: Remove loadBudget(), move to select Trip area

        BudgetViewModel budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());
        budgetViewModel.loadBudget("Test1");


        // Initialize the RecyclerView and set its adapter
        RecyclerView recyclerView = binding.categoriesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), budgetViewModel);
        recyclerView.setAdapter(categoryAdapter);


        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
