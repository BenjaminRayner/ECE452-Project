package com.example.tripgen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TripAdapter extends BaseAdapter {

    Context context;
    String[] trips;

    public TripAdapter(Context context, String[] trips) {
        this.context = context;
        this.trips = trips;
    }


    @Override
    public int getCount() {
        return trips.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.trip_layout, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.textview);
        LinearLayout ll_bg = view.findViewById(R.id.ll_bg);

        textView.setText(trips[i]);

        return view;
    }
}
