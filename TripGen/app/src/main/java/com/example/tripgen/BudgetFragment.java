package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentBudgetBinding;
import com.example.tripgen.databinding.FragmentTripBinding;

public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private BudgetViewModel budgetViewModel;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        budgetViewModel.getBudgetLiveData().observe(getViewLifecycleOwner(), budget -> {
//            updateUI(budget);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);


//        binding.newTripFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(BudgetFragment.this)
//                        .navigate(R.id.action_TripFragment_to_TripCreationFragment);
//            }
//        });
        return view;
    }
}
