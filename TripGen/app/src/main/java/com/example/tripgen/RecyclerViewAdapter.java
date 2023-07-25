package com.example.tripgen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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

    public RecyclerViewAdapter(Fragment fragment, List<String> titleList, Activity activity) {
        this.mFragment = fragment;
        this.titleList = titleList;
        this.mActivity = activity;
        this.googleApi = new GoogleApi(mActivity);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
            return new PlaceViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_item, parent, false);
            return new ButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlaceViewHolder) {
            // Adding title of the place
            ((PlaceViewHolder) holder).textView.setText(titleList.get(position / 2));
            // Adding the images from API
            googleApi.getPictureOfLocationToManipulate(titleList.get(position / 2), new GoogleApi.FetchPictureCallback() {
                @Override
                public void onPictureFetched(Bitmap bitmap) {
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

        } else if (holder instanceof ButtonViewHolder) {

            ((ButtonViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle your button action here...
                    System.out.println("yo");
                    ViewGroup parentView = (ViewGroup) v.getParent();
                    TextView t = parentView.findViewById(R.id.transportationTiming);

                    getDistanceTime("CN Tower", "Casa Loma", "mode", new DistanceTimeCallback() {
                        @Override
                        public void onResponse(String response) {
                            // Use the response here
                            t.setText(response);
                        }

                        @Override
                        public void onError(String error) {
                            // Handle error here
                        }
                    });

                }
            });
        }
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
        ImageView imageView;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItinerary);
            imageView = itemView.findViewById(R.id.imageViewItinerary);
            itemView.setOnClickListener(view -> {
                NavHostFragment.findNavController(mFragment).navigate(R.id.action_ItineraryFragment_to_thirdFragment);
            });
        }
    }

    class ButtonViewHolder extends RecyclerView.ViewHolder {

        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.transportationButton);
            button.setOnClickListener(view -> {
                // Define your button click action here
            });
        }
    }
}
