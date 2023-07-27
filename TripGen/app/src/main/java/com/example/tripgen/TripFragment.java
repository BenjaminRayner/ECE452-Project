package com.example.tripgen;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tripgen.databinding.FragmentTripBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TripFragment extends Fragment {

    private FragmentTripBinding binding;
    ArrayList<String> tripNames = new ArrayList<>();
    ArrayList<String> tripIDs = new ArrayList<>();
    private BudgetViewModel budgetViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ListAdapter adapter = new ListAdapter(requireActivity(), tripNames);
        binding.listView.setAdapter(adapter);

        db.collection("Trips").whereArrayContains("sharedWith", auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
              tripIDs.clear();
              tripNames.clear();
              for (DocumentSnapshot document : value.getDocuments()) {
                  tripIDs.add(document.getId());
                  tripNames.add(document.toObject(Trip.class).name);
              }
              adapter.notifyDataSetChanged();
          }
        });


        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity mainActivity = (MainActivity) requireActivity();
                mainActivity.currentTrip = tripIDs.get(i);

                NavHostFragment.findNavController(TripFragment.this)
                        .navigate(R.id.action_TripFragment_to_DateFragment);
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
