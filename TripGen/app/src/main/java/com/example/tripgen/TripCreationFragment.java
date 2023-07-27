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

import com.example.tripgen.databinding.FragmentTripCreationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class TripCreationFragment extends Fragment {

    private FragmentTripCreationBinding binding;
    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        materialDatePicker = MaterialDatePicker
                .Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
                .build();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTripCreationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            if (materialDatePicker.getHeaderText() == null || materialDatePicker.getHeaderText().equals("")) {
                Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT).show();
            } else {
                Trip newTrip = new Trip(selection.first / 1000 / 60 / 60 / 24, selection.second / 1000 / 60 / 60 / 24, auth.getCurrentUser().getEmail(), binding.editTripName.getText().toString(), new ArrayList<>());
                newTrip.sharedWith.add(auth.getCurrentUser().getEmail());

                db.collection("Trips").add(newTrip).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        MainActivity mainActivity = (MainActivity) requireActivity();
                        mainActivity.currentTrip = documentReference.getId();
                    }
                });

                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_TripCreationFragment_to_budgetCreationFragment);
            }
        });

        binding.calendarButton.setOnClickListener(v -> {
            String tripName = binding.editTripName.getText().toString().trim();
            if (tripName.isEmpty()) {
                Toast.makeText(requireContext(), R.string.empty_trip_name, Toast.LENGTH_SHORT).show();
            } else {
                materialDatePicker.show(getParentFragmentManager(), "Tag_picker");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
