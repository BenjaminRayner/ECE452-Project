package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentTripBinding;

public class TripFragment extends Fragment {

    private FragmentTripBinding binding;
    private String[] trips;
    private BudgetViewModel budgetViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        trips = getResources().getStringArray(R.array.trips);

        ListAdapter adapter = new ListAdapter(requireActivity(), trips);
        binding.listView.setAdapter(adapter);

        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        budgetViewModel.setContext(getContext());

        // When entering trip selection, remove budget selection
        budgetViewModel.removeBudget();


        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: Remove static budget loading
                try {
                    budgetViewModel.loadBudget("Test1");
                    NavHostFragment.findNavController(TripFragment.this)
                            .navigate(R.id.action_TripFragment_to_DateFragment);

                } catch (Exception e) {
                    NavHostFragment.findNavController(TripFragment.this)
                            .navigate(R.id.action_TripFragment_to_BudgetCreationFragment);
                }
            }
        });

        binding.newTripFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TripFragment.this)
                        .navigate(R.id.action_TripFragment_to_TripCreationFragment);
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
