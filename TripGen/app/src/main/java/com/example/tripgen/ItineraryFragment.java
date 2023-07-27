package com.example.tripgen;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripgen.databinding.FragmentDateBinding;
import com.example.tripgen.databinding.FragmentItineraryBinding;
import com.example.tripgen.databinding.RowItemBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class ItineraryFragment extends Fragment {

    private FragmentItineraryBinding binding;
    private static final Event[] EVENTS = {
//            new Event(R.drawable.cn_tower, "Event 1", "Description 1", "1:00 PM", "2:00 PM"),
//            new Event(R.drawable.cn_tower, "Event 2", "Description 2", "3:00 PM", "5:00 PM"),
//            new Event(R.drawable.cn_tower, "Event 3", "Description 3", "9:00 PM", "10:00 PM")
    };

    private Event[] events;
    private PlacesClient placesClient;
    private GoogleMap googleMap;
    private int startTimeHour, startTimeMin, endTimeHour, endTimeMin;
    String [] location_names = {};
    int [] location_images = {};
    HashMap<String, String> activityIDs = new HashMap<>();

    LinearLayout linearLayout;

//    List<String> choosen_location_names  = new ArrayList<String>();
//
//    List<Integer> choosen_location_images = new ArrayList<Integer>();
    List<Pair<Integer, Integer>> startTimeList = new ArrayList<>();
    List<Pair<Integer, Integer>> endTimeList = new ArrayList<>();
    RecyclerViewAdapter adapter1;

    ProgramAdapter programAdapter;

    FirebaseFirestore db;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItineraryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        mainActivity = (MainActivity) requireActivity();

        events = EVENTS;

//        EventAdapter adapter = new EventAdapter(requireActivity(), events);
//        binding.listView.setAdapter(adapter);

        //initialize geoDataClient
        Places.initialize(getActivity(), "AIzaSyC71z73qlGojykNfUrUXAmscdv8JGfzn8I");
        placesClient = Places.createClient(getActivity());

        //Initialize AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)getChildFragmentManager()
                        .findFragmentById(R.id.autoCompleteTextViewSearch);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(getActivity(), "Selected: " + place.getName(), Toast.LENGTH_SHORT).show();
                // add to view
                place.getAddress();


                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeHour = hourOfDay;
                        startTimeMin = minute;
                        startTimeList.add(new Pair<>(startTimeHour, startTimeMin));
                        // End Time dialog
                        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endTimeHour = hourOfDay;
                                endTimeMin = minute;
                                endTimeList.add(new Pair<>(endTimeHour, endTimeMin));
                                //adapter1.getTime(startTimeHour, startTimeMin, endTimeHour, endTimeMin);

                                // add activity to itinerary
                                Activity activity = new Activity(place.getName(), startTimeHour + startTimeMin, startTimeHour, startTimeMin, endTimeHour, endTimeMin);
                                db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).add(activity);
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, startTimeHour, startTimeMin, true);
                        timePickerDialog.setTitle("Select End Time");
                        timePickerDialog.show();
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, startTimeHour, startTimeMin, true);
                timePickerDialog.setTitle("Select Start Time");
                timePickerDialog.show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getActivity(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        ListView listViewMenu = (ListView) view.findViewById(R.id.listViewChoose);
        programAdapter = new ProgramAdapter(getActivity(), location_names, location_images);
        listViewMenu.setAdapter(programAdapter);

        RecyclerView listViewChoosen = (RecyclerView) view.findViewById(R.id.list_view_display);
        adapter1 = new RecyclerViewAdapter(ItineraryFragment.this, programAdapter.choosen_location_names, getActivity(), startTimeList, endTimeList, activityIDs, (MainActivity) requireActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listViewChoosen.setLayoutManager(linearLayoutManager);
        listViewChoosen.setAdapter(adapter1);


        // for drag and drop itinerary
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        listViewChoosen.addItemDecoration(dividerItemDecoration);

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        ItemTouchHelper itemTouchHelperDelete = new ItemTouchHelper(callback);
 //       itemTouchHelper.attachToRecyclerView(listViewChoosen);
        itemTouchHelperDelete.attachToRecyclerView(listViewChoosen);



        //Keep listview synced with firebase DB
        db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                programAdapter.choosen_location_names.clear();
                startTimeList.clear();
                endTimeList.clear();
                activityIDs.clear();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Activity activity = document.toObject(Activity.class);
                    programAdapter.choosen_location_names.add(activity.name);
                    startTimeList.add(new Pair<>(activity.startTimeHour, activity.startTimeMin));
                    endTimeList.add(new Pair<>(activity.endTimeHour, activity.endTimeMin));
                    activityIDs.put(activity.name, document.getId());
                }
                adapter1.notifyDataSetChanged();
            }
        });

        Button routeButton = view.findViewById(R.id.routeButton);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<LatLng> locationsLatLng = new ArrayList<>();
                if (programAdapter.choosen_location_names.size() < 2) {
                    return;
                }

                //convert locations to LatLng
                for(String location : programAdapter.choosen_location_names){
                    locationsLatLng.add(getLatLngFromLocation(location));
                }

                StringBuilder uriBuilder = new StringBuilder("https://www.google.com/maps/dir/?api=1");

                uriBuilder.append("&origin=")
                        .append(locationsLatLng.get(0).latitude)
                        .append(",")
                        .append(locationsLatLng.get(0).longitude);
                uriBuilder.append("&destination=")
                        .append(locationsLatLng.get(programAdapter.choosen_location_names.size()-1).latitude)
                        .append(",")
                        .append(locationsLatLng.get(programAdapter.choosen_location_names.size()-1).longitude);

                if(locationsLatLng.size() > 2){
                    uriBuilder.append("&waypoints=");
                    for(int i=1; i<locationsLatLng.size()-1; ++i){
                        uriBuilder.append(locationsLatLng.get(i).latitude)
                                .append(",")
                                .append(locationsLatLng.get(i).longitude);
                        if(i<locationsLatLng.size()-2){
                            uriBuilder.append("!");
                        }
                    }
                }

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriBuilder.toString()));
                mapIntent.setPackage("com.google.android.apps.maps");

                List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(mapIntent, 0);

                if(activities != null && !activities.isEmpty()){
                    startActivity(mapIntent);
                }else{
                    Toast.makeText(getContext(), "Please install the Google Maps app to open Google Maps", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return view;
    }
    private LatLng getLatLngFromLocation(String location){
        Geocoder geocoder = new Geocoder(requireContext());
        LatLng latLng = null;
        try{
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                latLng = new LatLng(latitude, longitude);
            }else{
                //resolve if address is empty
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return latLng;
    }
//    //drag and drop
//    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = target.getAdapterPosition();
//            //add Firebase swap
//            Collections.swap(programAdapter.choosen_location_names, fromPosition, toPosition);
//            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//        }
//    };
    // Swipe to delete
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target){
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction){
            Snackbar snackbar = Snackbar.make(getView(), "Item Deleted", Snackbar.LENGTH_LONG);
            snackbar.show();

            System.out.println("deleting: "+viewHolder.getAdapterPosition());
            System.out.println("size: "+programAdapter.choosen_location_names.size());

            db.collection("Trips").document(mainActivity.currentTrip).collection(mainActivity.currentDay).whereEqualTo("name", programAdapter.choosen_location_names.get((int) Math.ceil(viewHolder.getAdapterPosition()/2))).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        document.getReference().delete();
                    }
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
