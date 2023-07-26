package com.example.tripgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentDateBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//TODO: Override back button to always go back to TripFragment
//TODO: After creation of a new trip, currently it goes back to TripCreationFragment

public class DateFragment extends Fragment {

    private FragmentDateBinding binding;
    private ArrayList<String> dates = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ListAdapter adapter = new ListAdapter(requireActivity(), dates);
        binding.listView.setAdapter(adapter);

        MainActivity mainActivity = (MainActivity) requireActivity();
        db.collection("Trips").document(mainActivity.currentTrip).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Trip trip = documentSnapshot.toObject(Trip.class);

                LocalDate startDate = LocalDate.ofEpochDay(trip.startDate);
                LocalDate endDate = LocalDate.ofEpochDay(trip.endDate);
                int numDays = Period.between(startDate, endDate).getDays();
                for (int i = 0; i < numDays; ++i) {
                    dates.add(startDate.toString());
                    startDate = startDate.plusDays(1);
                }
                adapter.notifyDataSetChanged();
            }
        });

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mainActivity.currentDay = String.valueOf(i);
                NavHostFragment.findNavController(DateFragment.this)
                        .navigate(R.id.action_DateFragment_to_ItineraryFragment);
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
