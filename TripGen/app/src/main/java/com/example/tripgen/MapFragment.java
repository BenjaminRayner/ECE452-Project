package com.example.tripgen;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tripgen.databinding.FragmentMapBinding;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private Polyline polyline;
    private List<Place.Field> placeFields = Arrays.asList(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            //Place.Field.PHONE_NUMBER,
            //Place.Field.WEBSITE_URI,
            //Place.Field.RATING,
            //Place.Field.USER_RATINGS_TOTAL,
            //Place.Field.PRICE_LEVEL,
            //Place.Field.OPENING_HOURS,
            Place.Field.PHOTO_METADATAS
    );

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
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get GoogleMap object
        mapView.getMapAsync(this);

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        Button searchButton = view.findViewById(R.id.searchButton);

        //TESTING
        getCurrentLocation();
        getDetailsOfLocationToManipulate("Canada's Wonderland");
        getPictureOfLocationToManipulate("Canada's Wonderland");

        makeNearbyPlaceRequestToManipulate();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = searchEditText.getText().toString();

                LatLng latLng = getLatLngFromLocation(location);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        });

        return view;
    }

    public void getDetailsOfLocationToManipulate(String location) {
        getDetailsOfLocation(location, new FetchPlaceCallback() {
            @Override
            public void onPlaceFetched(Place place) {
                Log.i("Place", "Place Name: " + place.getName());
                Log.i("Place", "Place Address: " + place.getAddress());
                //do all the manipulation of the place info here
            }

            @Override
            public void onFetchPlaceFailure(Exception exception) {
                Log.e("Error", "Exception: " + exception.getMessage());
            }
        });
    }

    public void makeNearbyPlaceRequestToManipulate() {
        makeNearbyPlaceRequest(new FetchPlaceArrayListCallback() {
            @Override
            public void onPlaceArrayListFetched(ArrayList<Place> places) {
                for(Place place : places){
                    Log.i("Place", "Place Name: " + place.getName());
                    Log.i("Place", "Place Address: " + place.getAddress());
                }
                //do all the manipulation of the place info here
            }

            @Override
            public void onFetchPlaceFailure(Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    Log.e("NearbyPlace", "Place not found: " + statusCode);
                }
            }
        });
    }

    public void getPictureOfLocationToManipulate(String location) {
        getPictureOfLocation(location, new FetchPictureCallback() {
            @Override
            public void onPictureFetched(Bitmap bitmap) {
                testBitMapInAlertDialog(bitmap);
                //do something with the image
            }

            @Override
            public void onFetchFailure(Exception exception) {
                Log.e("Error", "Exception: " + exception.getMessage());
            }
        });
    }

    private void testBitMapInAlertDialog(Bitmap bitmap) {
        // Create an ImageView to display the bitmap
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);

        // Create an AlertDialog with the ImageView
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(imageView)
                .setTitle("Photo Preview")
                .setPositiveButton("OK", null)
                .show();
    }

    private void getPictureOfLocation(String location, FetchPictureCallback callback) {
        getDetailsOfLocation(location, new FetchPlaceCallback() {
            @Override
            public void onPlaceFetched(Place place) {
                List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();

                if (photoMetadatas != null && !photoMetadatas.isEmpty()) {
                    PhotoMetadata photoMetadata = photoMetadatas.get(0);

                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .setMaxHeight(400)
                            .setMaxWidth(400)
                            .build();

                    placesClient.fetchPhoto(photoRequest).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FetchPhotoResponse photoResponse = task.getResult();
                            Bitmap bitmap = photoResponse.getBitmap();

                            callback.onPictureFetched(bitmap);
                        } else {
                            Exception exception = task.getException();
                            callback.onFetchFailure(exception);
                        }
                    });

                }
            }

            @Override
            public void onFetchPlaceFailure(Exception exception) {
                Log.e("Error", "Exception: " + exception.getMessage());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    public void addItineraryTripRouteMap(ArrayList<String> locations) {
        ArrayList<LatLng> latLngLocations = new ArrayList<>();

        // Add markers for each location
        for (int i = 0; i < locations.size(); ++i) {
            LatLng location = getLatLngFromLocation(locations.get(i));
            latLngLocations.add(location);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title("Location " + (i + 1) + ": " + locations.get(i));
            Marker marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();

        }

        //Draw polyline to connect the locations
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5f)
                .color(getResources().getColor(R.color.black))
                .addAll(latLngLocations);

        polyline = googleMap.addPolyline(polylineOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocations.get(0), 10f));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        //testing addItineraryTripRouteMap
        ArrayList<String> locations = new ArrayList<>();
        locations.add("Markham");
        locations.add("CN Tower");
        locations.add("Niagra Falls");
        addItineraryTripRouteMap(locations);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //if deny use of GPS, remind user to turn GPS on to use feature
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted, you are good to go
            }else{
                Toast.makeText(getContext(), "Location access denied. Please turn on GPS to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface FetchPlaceCallback {
        void onPlaceFetched(Place place);

        void onFetchPlaceFailure(Exception exception);
    }

    public interface FetchPlaceArrayListCallback{
        void onPlaceArrayListFetched(ArrayList<Place> places);

        void onFetchPlaceFailure(Exception exception);
    }

    public interface FetchPictureCallback {
        void onPictureFetched(Bitmap bitmap);

        void onFetchFailure(Exception exception);
    }

    private void makeNearbyPlaceRequest(FetchPlaceArrayListCallback callback) {
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields)
                .build();

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        placesClient.findCurrentPlace(request).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                ArrayList<Place> placeArrayList = new ArrayList<>();
                FindCurrentPlaceResponse response = task.getResult();
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place = placeLikelihood.getPlace();
                    placeArrayList.add(place);
                }
                callback.onPlaceArrayListFetched(placeArrayList);
            }else{
                Exception exception = task.getException();
                callback.onFetchPlaceFailure(exception);
            }
        });
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

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.i("CurrentLocation", "Latitude: " + latitude + ", Longitude: " + longitude);
                        // Use the latitude and longitude for further processing
                    } else {
                        Log.e("CurrentLocation", "Location is null");
                    }
                })
                .addOnFailureListener(getActivity(), e -> {
                    Log.e("CurrentLocation", "Failed to retrieve location: " + e.getMessage());
                });
    }

}
