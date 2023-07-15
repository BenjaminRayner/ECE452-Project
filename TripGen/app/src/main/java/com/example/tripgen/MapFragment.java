package com.example.tripgen;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tripgen.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private PlacesClient placesClient;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //initialize geoDataClient
        Places.initialize(getActivity(), "AIzaSyC71z73qlGojykNfUrUXAmscdv8JGfzn8I");
        placesClient = Places.createClient(getActivity());

        //initialize MapView
        mapView =view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get GoogleMap object
        mapView.getMapAsync(this);

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        Button searchButton = view.findViewById(R.id.searchButton);

        getDetailsOfLocation("Canada Wanderland", new FetchPlaceCallback() {
            @Override
            public void onPlaceFetched(Place place) {
                Log.i("Place", "Place Name: " + place.getName());
                Log.i("Place", "Place Address: " + place.getAddress());
                List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();

                if(photoMetadatas != null && !photoMetadatas.isEmpty()){
                    PhotoMetadata photoMetadata = photoMetadatas.get(0);

                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxHeight(400)
                            .setMaxWidth(400)
                            .build();

                    placesClient.fetchPhoto(photoRequest).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            FetchPhotoResponse photoResponse = task.getResult();
                            Bitmap bitmap = photoResponse.getBitmap();

                            // Create an ImageView to display the bitmap
                            ImageView imageView = new ImageView(getContext());
                            imageView.setImageBitmap(bitmap);

                            // Create an AlertDialog with the ImageView
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(imageView)
                                    .setTitle("Photo Preview")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }else{
                            Exception exception = task.getException();
                        }
                    });

                }
            }

            @Override
            public void onFetchPlaceFailure(Exception exception) {
                Log.e("Error", "Exception: " + exception.getMessage());
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String location = searchEditText.getText().toString();

                Geocoder geocoder = new Geocoder(requireContext());

                try {
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                    } else {
                        // Handle case when no addresses found
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mapView = view.findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);

//        binding.buttonThird.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(ThirdFragment.this)
//                        .navigate(R.id.action_ThirdFragment_to_FirstFragment);
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public interface FetchPlaceCallback{
        void onPlaceFetched(Place place);
        void onFetchPlaceFailure(Exception exception);
    }

    private void getDetailsOfLocation(String location, FetchPlaceCallback callback) {
        AtomicReference<String> placeId = new AtomicReference<>();

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(location)
                .build();

        Task<FindAutocompletePredictionsResponse> placeResponse = placesClient.findAutocompletePredictions(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindAutocompletePredictionsResponse response = task.getResult();
                if (response != null) {
                    List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                    if (!predictions.isEmpty()) {
                        AutocompletePrediction prediction = predictions.get(0);
                        placeId.set(prediction.getPlaceId());
                        Log.i("Place", "Place ID: " + placeId.get());

                        // Fetch details of the place
                        List<Place.Field> placeFields = Arrays.asList(
                                Place.Field.NAME,
                                Place.Field.ADDRESS,
                                Place.Field.PHONE_NUMBER,
                                Place.Field.WEBSITE_URI,
                                Place.Field.RATING,
                                Place.Field.USER_RATINGS_TOTAL,
                                Place.Field.PRICE_LEVEL,
                                Place.Field.OPENING_HOURS,
                                Place.Field.PHOTO_METADATAS
                        );
                        FetchPlaceRequest fetchRequest = FetchPlaceRequest.builder(placeId.get(), placeFields).build();

                        Task<FetchPlaceResponse> fetchPlaceResponse = placesClient.fetchPlace(fetchRequest);
                        fetchPlaceResponse.addOnCompleteListener(fetchTask -> {
                            if (fetchTask.isSuccessful()) {
                                FetchPlaceResponse fetchResponse = fetchTask.getResult();
                                if (fetchResponse != null) {
                                    Place place = fetchResponse.getPlace();
                                    callback.onPlaceFetched(place);
                                }
                            } else {
                                Exception exception = fetchTask.getException();
                                if (exception != null) {
                                    callback.onFetchPlaceFailure(exception);
                                }
                            }
                        });
                    }
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("Error", "Exception: " + exception.getMessage());
                }
            }
        });
    }

}
