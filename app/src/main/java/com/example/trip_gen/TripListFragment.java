package com.example.trip_gen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.trip_gen.databinding.FragmentTripListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripListFragment extends Fragment
{
    FirebaseAuth auth;
    FirebaseFirestore db;
    FragmentTripListBinding binding;
    ArrayList<String> tripNames = new ArrayList<>();
    ArrayList<String> tripIDs = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentTripListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.welcomeTextView.setText(getString(R.string.welcome_placeholder, auth.getCurrentUser().getEmail()));

//        Trip test = new Trip();
//        test.name = "Toronto";
//        test.owner = mainActivity.auth.getUid();
//        test.startDate = "22 July 2023";
//        test.endDate = "23 July 2023";
//        test.sharedWith = new ArrayList<>();
//        test.sharedWith.add("user1");
//        test.sharedWith.add("user2");
//        db.collection("/Trips").add(test).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
////                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                String test = e.toString();
//                int test2 = 1;
//            }
//        });

        // Changes to tripList variable will update tripList view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, tripNames);
        binding.tripList.setAdapter(adapter);

        // Realtime updates of trips the user has access to
        db.collection("Trips").whereArrayContains("sharedWith", auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
//                for (DocumentChange change : snapshot.getDocumentChanges()) {
//                    switch (change.getType()) {
//                        case ADDED:
//                            tripList.add(change.getDocument());
//                            break;
//                        case MODIFIED:
//                            tripList.;
//                            break;
//                        case REMOVED:
//                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());
//                            break;
//                    }
//                }



                tripIDs.clear();
                tripNames.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    tripIDs.add(document.getId());
                    tripNames.add(document.toObject(Trip.class).name);
                }

//                List<Trip> trips = snapshot.toObjects(Trip.class);
//                for (Trip trip : trips) {
//                    tripNames.add(trip.name);    // Need item that takes more than just name. Custom ArrayAdapter<>?
//                }
                adapter.notifyDataSetChanged();
            }
        });

        binding.tripList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tripID = tripIDs.get(position);

                TripListFragmentDirections.ActionTripListFragmentToTripFragment action = TripListFragmentDirections.actionTripListFragmentToTripFragment(tripID);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

//        MainViewModel test = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
//        test.getTrips().observe(requireActivity(), new Observer<List<Trip>>() {
//            @Override
//            public void onChanged(List<Trip> trips) {
//                Trip test2 = trips.get(test.selectedTrip);
//            }
//        });
    }
}