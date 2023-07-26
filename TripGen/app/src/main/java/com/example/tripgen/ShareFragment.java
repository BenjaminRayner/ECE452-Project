package com.example.tripgen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tripgen.databinding.FragmentShareBinding;
import com.example.tripgen.databinding.FragmentTripBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ShareFragment extends Fragment {

    private FragmentShareBinding binding;
    Trip trip;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentShareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String tripID = ((MainActivity) requireActivity()).currentTrip;

        db.collection("Trips").document(tripID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.exists()) { Toast.makeText(getActivity(), "You have been removed", Toast.LENGTH_SHORT).show(); return; }
                trip = value.toObject(Trip.class);
            }
        });

        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedWith = binding.shareText.getText().toString();
                db.collection("Trips").document(tripID).update("sharedWith", FieldValue.arrayUnion(sharedWith));
                Toast.makeText(getActivity(), "Shared", Toast.LENGTH_SHORT).show();
            }
        });

        binding.removeShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removeShare = binding.shareText.getText().toString();
                if (!auth.getCurrentUser().getEmail().equals(trip.owner)) { Toast.makeText(getActivity(), "Not owner", Toast.LENGTH_SHORT).show(); return; }
                if (auth.getCurrentUser().getEmail().equals(removeShare)) { Toast.makeText(getActivity(), "Can't remove yourself!", Toast.LENGTH_SHORT).show(); return; }
                Toast.makeText(getActivity(), "Removed share", Toast.LENGTH_SHORT).show();
                db.collection("Trips").document(tripID).update("sharedWith", FieldValue.arrayRemove(removeShare));
            }
        });
    }
}