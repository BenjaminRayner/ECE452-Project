package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentBudgetCreationBinding;
import com.example.tripgen.databinding.FragmentTripCreationBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

public class BudgetCreationFragment extends Fragment {

    private FragmentBudgetCreationBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentBudgetCreationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.calendarButton.setOnClickListener(v -> {
//            Pair<Long, Long> defaultSelection = Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds());
//
//            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.
//                    Builder.dateRangePicker().setSelection(defaultSelection).build();
//
//            materialDatePicker.show(getParentFragmentManager(), "Tag_picker");
//            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
//
//                String tripName = binding.editTripName.getText().toString().trim();
//                if (materialDatePicker.getHeaderText() == null || materialDatePicker.getHeaderText().equals("")) {
//                    Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT).show();
//                } else if (tripName.isEmpty()) {
//                    Toast.makeText(requireContext(), R.string.empty_trip_name, Toast.LENGTH_SHORT).show();
//                } else {
//                    NavHostFragment.findNavController(TripCreationFragment.this)
//                            .navigate(R.id.action_TripCreationFragment_to_DateFragment);
//                }
//            });
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
