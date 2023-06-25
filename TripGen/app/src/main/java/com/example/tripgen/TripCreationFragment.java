package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentTripCreationBinding;

public class TripCreationFragment extends Fragment {

    private FragmentTripCreationBinding binding;

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

        binding.tripSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tripName = binding.editTripName.getText().toString().trim();

                if (binding.tripCalendarSelection.getDate() == 0 ) {
                    Toast.makeText(requireContext(), R.string.invalid_date, Toast.LENGTH_SHORT).show();
                } else if (tripName.isEmpty() ) {
                    Toast.makeText(requireContext(), R.string.empty_trip_name, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "binding.tripCalendarSelection.getDate()" + binding.tripCalendarSelection.getDate() + " tripName " + tripName, Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(TripCreationFragment.this)
                        .navigate(R.id.action_TripCreationFragment_to_DateFragment);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
