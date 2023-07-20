package com.example.trip_gen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trip_gen.databinding.FragmentTripBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class TripFragment extends Fragment
{
    FirebaseAuth auth;
    FirebaseFirestore db;
    Trip trip;
    private FragmentTripBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String tripID = TripFragmentArgs.fromBundle(getArguments()).getDocumentID();

        db.collection("Trips").document(tripID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.exists()) { Toast.makeText(getActivity(), "You have been removed", Toast.LENGTH_SHORT).show(); return; }
                trip = value.toObject(Trip.class); //Store in viewmodel?
                binding.test.setText("Name" + "  : " + trip.name + '\n' +
                                     "Start Date" + " : " + trip.startDate + '\n' +
                                     "End Date" + " : " + trip.endDate + '\n' +
                                     "Owner" + " : " + trip.owner + '\n' +
                                     "Shared" + " : " + trip.sharedWith);
            }
        });

        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedWith = binding.shareText.getText().toString();
                db.collection("Trips").document(tripID).update("sharedWith", FieldValue.arrayUnion(sharedWith));
            }
        });

        binding.removeShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removeShare = binding.shareText.getText().toString();
                if (!auth.getCurrentUser().getEmail().equals(trip.owner)) { Toast.makeText(getActivity(), "Not owner", Toast.LENGTH_SHORT).show(); return; }
                if (auth.getCurrentUser().getEmail().equals(removeShare)) { Toast.makeText(getActivity(), "Can't remove yourself!", Toast.LENGTH_SHORT).show(); return; }
                db.collection("Trips").document(tripID).update("sharedWith", FieldValue.arrayRemove(removeShare));
            }
        });
    }
}