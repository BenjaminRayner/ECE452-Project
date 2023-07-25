package com.example.tripgen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tripgen.databinding.FragmentMapBinding;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
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

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private Polyline polyline;

    private static final String DIRECTIONS_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String API_KEY = "AIzaSyC71z73qlGojykNfUrUXAmscdv8JGfzn8I";


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

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getActivity(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //initialize MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get GoogleMap object
        mapView.getMapAsync(this);

        //save button init
        Button saveButton = view.findViewById(R.id.searchButton2);

        //TESTING
//        getDistanceTime("Niagra Falls", "CN Tower", "driving");
//        getCurrentLocation();
//        getDetailsOfLocationToManipulate("Canada's Wonderland");
//        getPictureOfLocationToManipulate("Canada's Wonderland");
//
//        makeNearbyPlaceRequestToManipulate();

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                ArrayList<String> locations = new ArrayList<>();
//                locations.add("Markham");
//                locations.add("CN Tower");
//                locations.add("Niagara Falls");
//                openGoogleMapsWithDirections(locations);
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

    //Example origin = University of Waterloo
    //destination = CN Tower
    //mode = driving, walking, transit
    public void getDistanceTime(String origin, String destination, String mode){
        getDirectionsDistanceTime(origin, destination, mode,
                response -> {
                    Log.d("Directions", "Distance and Duration: " + response);
                },
                error -> {
                    Log.e("Directions", "Error: " + error.getMessage());
                }
        );
    }
    private void getDirectionsDistanceTime(String origin, String destination, String mode,
                                                 Response.Listener<String> listener,
                                                 Response.ErrorListener errorListener){
        String url = buildUrl(origin, destination, mode);
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Parse the JSON response and extract distance and duration information
                        Log.d("Response", " " + response);
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONArray legs = route.getJSONArray("legs");
                            if(legs.length() > 0){
                                JSONObject leg = legs.getJSONObject(0);
                                JSONObject distanceObj = leg.getJSONObject("distance");
                                JSONObject durationObj = leg.getJSONObject("duration");

                                String distanceText = distanceObj.getString("text");
                                String durationText = durationObj.getString("text");

                                listener.onResponse(distanceText + " (" + durationText + ")");
                            }else{
                                Log.d("legs.length", "less than 0" + legs);
                            }

                        }else{
                            Log.d("Routes.length", "less than 0" + routes);
                        }
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(new VolleyError("Error parsing response"));
                    }
                },
                error -> errorListener.onErrorResponse(new VolleyError("Error getting directions"))
        );
        queue.add(jsonObjectRequest);

    }

    private static String buildUrl(String origin, String destination, String mode){
        String encodeOrigin = Uri.encode(origin);
        String encodeDest = Uri.encode(destination);

        return DIRECTIONS_API_BASE_URL + "?origin=" + encodeOrigin + "&destination="
                + encodeDest + "&mode=" + mode + "&key=" + API_KEY;
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
//        ArrayList<String> locations = new ArrayList<>();
//        locations.add("Markham");
//        locations.add("CN Tower");
//        locations.add("Niagra Falls");
//        addItineraryTripRouteMap(locations);
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
        List<Place.Type> desiredTypes = Arrays.asList(Place.Type.RESTAURANT,
                                                Place.Type.TOURIST_ATTRACTION,
                                                Place.Type.PARK,
                                                Place.Type.MUSEUM,
                                                Place.Type.SHOPPING_MALL,
                                                Place.Type.UNIVERSITY
        );
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                //Place.Field.PHONE_NUMBER,
                //Place.Field.WEBSITE_URI,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.PRICE_LEVEL,
                //Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES
        );
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
                    if(place.getTypes().stream().anyMatch(desiredTypes::contains)){
                        placeArrayList.add(place);
                    }
                }
                callback.onPlaceArrayListFetched(placeArrayList);
            }else{
                Exception exception = task.getException();
                callback.onFetchPlaceFailure(exception);
            }
        });
    }

    private void openGoogleMapsWithDirections(ArrayList<String> locations) {
        ArrayList<LatLng> locationsLatLng = new ArrayList<>();
        if (locations.size() < 2) {
            return;
        }

        //convert locations to LatLng
        for(String location : locations){
            locationsLatLng.add(getLatLngFromLocation(location));
        }

        StringBuilder uriBuilder = new StringBuilder("https://www.google.com/maps/dir/?api=1");

        uriBuilder.append("&origin=")
                .append(locationsLatLng.get(0).latitude)
                .append(",")
                .append(locationsLatLng.get(0).longitude);
        uriBuilder.append("&destination=")
                .append(locationsLatLng.get(locations.size()-1).latitude)
                .append(",")
                .append(locationsLatLng.get(locations.size()-1).longitude);

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

    private void getDetailsOfLocation(String location, FetchPlaceCallback callback) {
        AtomicReference<String> placeId = new AtomicReference<>();
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.PRICE_LEVEL,
                Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES
        );

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

    //gets the phone actual location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.i("CurrentLocation", "Latitude: " + latitude + ", Longitude: " + longitude);
                    } else {
                        Log.e("CurrentLocation", "Location is null");
                    }
                })
                .addOnFailureListener(getActivity(), e -> {
                    Log.e("CurrentLocation", "Failed to retrieve location: " + e.getMessage());
                });
    }

}
