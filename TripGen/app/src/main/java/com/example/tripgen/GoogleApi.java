package com.example.tripgen;

import android.app.Activity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleApi {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FragmentMapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private Polyline polyline;
    private Activity activity;

    private static final String DIRECTIONS_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String API_KEY = "AIzaSyC71z73qlGojykNfUrUXAmscdv8JGfzn8I";

    public GoogleApi(Activity activity){
        //initialize geoDataClient
        this.activity = activity;
        Places.initialize(activity, "AIzaSyC71z73qlGojykNfUrUXAmscdv8JGfzn8I");
        placesClient = Places.createClient(activity);
    }


    //shared
    public void getDetailsOfLocationToManipulate(String location) {
        getDetailsOfLocation(
                location,
                new FetchPlaceCallback() {
                    @Override
                    public void onPlaceFetched(Place place) {
                        Log.i("Place", "Details-Place Name: " + place.getName());
                        Log.i("Place", "Details-Place Address: " + place.getAddress());
                        //do all the manipulation of the place info here
                    }

                    @Override
                    public void onFetchPlaceFailure(Exception exception) {
                        Log.e("Error", "Exception: " + exception.getMessage());
                    }
                }
        );
    }

    //shared
    public void makeNearbyPlaceRequestToManipulate() {
        makeNearbyPlaceRequest(
                new FetchPlaceArrayListCallback() {
                    @Override
                    public void onPlaceArrayListFetched(ArrayList<Place> places) {
                        for (Place place : places) {
                            Log.i("Place", "Nearby-Place Name: " + place.getName());
                            Log.i("Place", "Nearby-Place Address: " + place.getAddress());
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
                }
        );
    }

    //shared
    //if deny use of GPS, remind user to turn GPS on to use feature
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode,
//            @NonNull String[] permissions,
//            @NonNull int[] grantResults
//    ) {
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (
//                    grantResults.length > 0 &&
//                            grantResults[0] == PackageManager.PERMISSION_GRANTED
//            ) {
//                //permission granted, you are good to go
//            } else {
//                Toast
//                        .makeText(
//                                activity,
//                                "Location access denied. Please turn on GPS to use this feature",
//                                Toast.LENGTH_SHORT
//                        )
//                        .show();
//            }
//        }
//    }

    //shared
    public void getPictureOfLocationToManipulate(String location) {
        getPictureOfLocation(
                location,
                new FetchPictureCallback() {
                    @Override
                    public void onPictureFetched(Bitmap bitmap) {
                        testBitMapInAlertDialog(bitmap);
                        //do something with the image
                    }

                    @Override
                    public void onFetchFailure(Exception exception) {
                        Log.e("Error", "Exception: " + exception.getMessage());
                    }
                }
        );
    }

    //shared
    //Example origin = University of Waterloo
    //destination = CN Tower
    //mode = driving, walking, transit
    public void getDistanceTime(String origin, String destination, String mode) {
        getDirectionsDistanceTime(
                origin,
                destination,
                mode,
                response -> {
                    Log.d("Directions", "Distance and Duration: " + response);
                },
                error -> {
                    Log.e("Directions", "Error: " + error.getMessage());
                }
        );
    }

    //HELPER functions
    private void getDirectionsDistanceTime(
            String origin,
            String destination,
            String mode,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        String url = buildUrl(origin, destination, mode);
        RequestQueue queue = Volley.newRequestQueue(activity);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Parse the JSON response and extract distance and duration information
                        Log.d("Response", " " + response);
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONArray legs = route.getJSONArray("legs");
                            if (legs.length() > 0) {
                                JSONObject leg = legs.getJSONObject(0);
                                JSONObject distanceObj = leg.getJSONObject("distance");
                                JSONObject durationObj = leg.getJSONObject("duration");

                                String distanceText = distanceObj.getString("text");
                                String durationText = durationObj.getString("text");

                                listener.onResponse(distanceText + " (" + durationText + ")");
                            } else {
                                Log.d("legs.length", "less than 0" + legs);
                            }
                        } else {
                            Log.d("Routes.length", "less than 0" + routes);
                        }
                    } catch (JSONException e) {
                        errorListener.onErrorResponse(
                                new VolleyError("Error parsing response")
                        );
                    }
                },
                error ->
                        errorListener.onErrorResponse(
                                new VolleyError("Error getting directions")
                        )
        );
        queue.add(jsonObjectRequest);
    }

    private static String buildUrl(
            String origin,
            String destination,
            String mode
    ) {
        String encodeOrigin = Uri.encode(origin);
        String encodeDest = Uri.encode(destination);

        return (
                DIRECTIONS_API_BASE_URL +
                        "?origin=" +
                        encodeOrigin +
                        "&destination=" +
                        encodeDest +
                        "&mode=" +
                        mode +
                        "&key=" +
                        API_KEY
        );
    }

    private void testBitMapInAlertDialog(Bitmap bitmap) {
        // Create an ImageView to display the bitmap
        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(bitmap);

        // Create an AlertDialog with the ImageView
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setView(imageView)
                .setTitle("Photo Preview")
                .setPositiveButton("OK", null)
                .show();
    }

    private void getPictureOfLocation(
            String location,
            FetchPictureCallback callback
    ) {
        getDetailsOfLocation(
                location,
                new FetchPlaceCallback() {
                    @Override
                    public void onPlaceFetched(Place place) {
                        List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();

                        if (photoMetadatas != null && !photoMetadatas.isEmpty()) {
                            PhotoMetadata photoMetadata = photoMetadatas.get(0);

                            FetchPhotoRequest photoRequest = FetchPhotoRequest
                                    .builder(photoMetadata)
                                    .setMaxHeight(400)
                                    .setMaxWidth(400)
                                    .build();

                            placesClient
                                    .fetchPhoto(photoRequest)
                                    .addOnCompleteListener(task -> {
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
                }
        );
    }

    public interface FetchPlaceCallback {
        void onPlaceFetched(Place place);

        void onFetchPlaceFailure(Exception exception);
    }

    public interface FetchPlaceArrayListCallback {
        void onPlaceArrayListFetched(ArrayList<Place> places);

        void onFetchPlaceFailure(Exception exception);
    }

    public interface FetchPictureCallback {
        void onPictureFetched(Bitmap bitmap);

        void onFetchFailure(Exception exception);
    }

    private void makeNearbyPlaceRequest(FetchPlaceArrayListCallback callback) {
        List<Place.Type> desiredTypes = Arrays.asList(
                Place.Type.RESTAURANT,
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
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest
                .builder(placeFields)
                .build();

        if (
                ActivityCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                        PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
        placesClient
                .findCurrentPlace(request)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Place> placeArrayList = new ArrayList<>();
                        FindCurrentPlaceResponse response = task.getResult();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Place place = placeLikelihood.getPlace();
                            if (place.getTypes().stream().anyMatch(desiredTypes::contains)) {
                                placeArrayList.add(place);
                            }
                        }
                        callback.onPlaceArrayListFetched(placeArrayList);
                    } else {
                        Exception exception = task.getException();
                        callback.onFetchPlaceFailure(exception);
                    }
                });
    }

    private void getDetailsOfLocation(
            String location,
            FetchPlaceCallback callback
    ) {
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
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(token)
                .setQuery(location)
                .build();

        Task<FindAutocompletePredictionsResponse> placeResponse = placesClient.findAutocompletePredictions(
                request
        );
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
                        FetchPlaceRequest fetchRequest = FetchPlaceRequest
                                .builder(placeId.get(), placeFields)
                                .build();

                        Task<FetchPlaceResponse> fetchPlaceResponse = placesClient.fetchPlace(
                                fetchRequest
                        );
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

    private LatLng getLatLngFromLocation(String location) {
        Geocoder geocoder = new Geocoder(activity);
        LatLng latLng = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                latLng = new LatLng(latitude, longitude);
            } else {
                //resolve if address is empty
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }
}
