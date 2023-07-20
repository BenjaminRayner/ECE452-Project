package com.example.trip_gen;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final MutableLiveData<List<Trip>> trips;

    int selectedTrip;

    MainViewModel() {
        auth = FirebaseAuth.getInstance();
        trips = new MutableLiveData<>();

        db = FirebaseFirestore.getInstance();
        db.collection("/Trips").whereArrayContains("sharedWith", auth.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                String test3 = value.getDocuments().get(0).getId();
                trips.setValue(value.toObjects(Trip.class));
            }
        });
    }

    LiveData<List<Trip>> getTrips() {
        return trips;
    }

}
