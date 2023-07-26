package com.example.tripgen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_BUTTON = 1;
    private static final String DIRECTIONS_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String API_KEY = "AIzaSyC71z73qlGojykNfUrUXAmscdv8JGfzn8I";

    List<String> titleList;
    Fragment mFragment;
    Activity mActivity;
    GoogleApi googleApi;
    List<Pair<Integer, Integer>> startTimeList;
    List<Pair<Integer, Integer>> endTimeList;

    public RecyclerViewAdapter(Fragment fragment, List<String> titleList, Activity activity, List<Pair<Integer, Integer>> startTimeList, List<Pair<Integer, Integer>> endTimeList) {
        this.mFragment = fragment;
        this.titleList = titleList;
        this.mActivity = activity;
        this.googleApi = new GoogleApi(mActivity);
        this.startTimeList = startTimeList;
        this.endTimeList = endTimeList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
            return new PlaceViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item, parent, false);
            return new RadioGroupViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlaceViewHolder) {
            // Adding title of the place
            ((PlaceViewHolder) holder).textView.setText(titleList.get(position / 2));
//            int startHour = startTimeList.get(position / 2).first;
//            int startMinute = startTimeList.get(position / 2).second;
//            int endHour = endTimeList.get(position / 2).first;
//            int endMinute = endTimeList.get(position / 2).second;
//            ((PlaceViewHolder) holder).timeView.setText("Start Time: "+startHour+":"+startMinute+"      "+"End Time: "+endHour+":"+endMinute);
            // Adding the images from API
            googleApi.getPictureOfLocationToManipulate(titleList.get(position / 2), new GoogleApi.FetchPictureCallback() {
                @Override
                public void onPictureFetched(Bitmap bitmap, Place place) {
                    // Do whatever you need with the bitmap here
                    ((PlaceViewHolder) holder).imageView.setImageBitmap(bitmap);
                    Log.d("Debug", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());
                }

                @Override
                public void onFetchFailure(Exception exception) {
                    // Handle the exception here
                    Log.d("Debug", "Error while getting the image");
                }
            });

        } else if (holder instanceof RadioGroupViewHolder) {
            calculateTimingForDefaultSelection(titleList, position, ((RadioGroupViewHolder) holder).transportationTiming);
        }
    }
    private void calculateTimingForDefaultSelection(List<String> titleList, int position, TextView timingTextView) {
        int originPosition = (position - 1) / 2;
        int destinationPosition = originPosition == titleList.size() - 1 ? 0 : (originPosition + 1);

        String origin = titleList.get(originPosition);
        String destination = titleList.get(destinationPosition);

        // Assuming the default selected mode is "driving".
        String defaultMode = "driving";

        // Update the transportation timing for the default selection.
        getDistanceTime(origin, destination, defaultMode, new DistanceTimeCallback() {
            @Override
            public void onResponse(String response) {
                // Use the response here
                timingTextView.setText(response);
            }

            @Override
            public void onError(String error) {
                // Handle error here
            }
        });
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
    public interface DistanceTimeCallback {
        void onResponse(String response);
        void onError(String error);
    }
    public void getDistanceTime(String origin, String destination, String mode, DistanceTimeCallback callback) {
        getDirectionsDistanceTime(
                origin,
                destination,
                mode,
                response -> {
                    Log.d("Directions", "Distance and Duration: " + response);
                    callback.onResponse(response);
                },
                error -> {
                    Log.e("Directions", "Error: " + error.getMessage());
                    callback.onError(error.getMessage());
                }
        );
    }
    private void getDirectionsDistanceTime(
            String origin,
            String destination,
            String mode,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        String url = buildUrl(origin, destination, mode);
        RequestQueue queue = Volley.newRequestQueue(mActivity);

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


    @Override
    public int getItemCount() {
        return 2 * titleList.size() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? VIEW_TYPE_ITEM : VIEW_TYPE_BUTTON;
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView timeView;
        ImageView imageView;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItinerary);
            imageView = itemView.findViewById(R.id.imageViewItinerary);
            timeView = itemView.findViewById(R.id.startTimeText);
            itemView.setOnClickListener(view -> {
                String location = textView.getText().toString();
                Bundle args = new Bundle();
                args.putString("location", location);
                Log.d("RecyclerViewAdapter", "Sending location argument: " + location);
                NavHostFragment.findNavController(mFragment).navigate(R.id.action_ItineraryFragment_to_thirdFragment, args);
            });
        }
    }

    class RadioGroupViewHolder extends RecyclerView.ViewHolder {

        RadioGroup radioGroup;
        TextView transportationTiming;
        RadioButton defaultRadioButton;

        public RadioGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            radioGroup = itemView.findViewById(R.id.radio_group); // Replace with your RadioGroup id
            transportationTiming = itemView.findViewById(R.id.transportationTiming); // Your TextView for displaying time
            defaultRadioButton = itemView.findViewById(R.id.radio_drive); // Replace with your default RadioButton id

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != -1) {
                    RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
                    String mode = selectedRadioButton.getText().toString();
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        int originPos = (pos - 1) / 2;
                        int destinationPos = (originPos + 1) % titleList.size();
                        String origin = titleList.get(originPos);
                        String destination = titleList.get(destinationPos);

                        getDistanceTime(origin, destination, mode, new DistanceTimeCallback() {
                            @Override
                            public void onResponse(String response) {
                                // Use the response here
                                transportationTiming.setText(response);
                            }

                            @Override
                            public void onError(String error) {
                                // Handle error here
                            }
                        });
                    }
                } else {
                    Toast.makeText(group.getContext(), "No Radio Button selected", Toast.LENGTH_SHORT).show();
                }
            });

            // Check the default radio button and trigger the initial calculation
            defaultRadioButton.setChecked(true);
            radioGroup.check(defaultRadioButton.getId());
        }
    }
}
