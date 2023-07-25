package com.example.tripgen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.util.Collections;
import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_BUTTON = 1;

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
            ((PlaceViewHolder) holder).textView.setText(titleList.get(position / 2));

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
                }
            });
        }
    }
    private void getPictures(String place){
        googleApi.getPictureOfLocationToManipulate(place, new GoogleApi.FetchPictureCallback() {
            @Override
            public void onPictureFetched(Bitmap bitmap) {
                // Do whatever you need with the bitmap here

                Log.d("Debug", "Bitmap width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

            }

            @Override
            public void onFetchFailure(Exception exception) {
                // Handle the exception here
                Log.d("Debug", "Error while getting the image");
            }
        });

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
                TextView t = view.findViewById(R.id.transportationTiming);
                t.setText("lol");
                System.out.println("lol");
                MapFragment mapFragment = new MapFragment();
                mapFragment.getDistanceTime("","","");
            });
        }
    }
}
