package com.example.tripgen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {

    TestActivity testActivity;
    String[] trips;
    Animation animation;

    public MainAdapter(TestActivity testActivity, String[] trips) {
        this.testActivity = testActivity;
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

        view = LayoutInflater.from(testActivity).inflate(R.layout.trip_layout, viewGroup, false);
        animation = AnimationUtils.loadAnimation(testActivity, R.anim.animation1);

        TextView textView;
        LinearLayout ll_bg;
        ll_bg = view.findViewById(R.id.ll_bg);
        textView = view.findViewById(R.id.textview);

        textView.setText(trips[i]);
        textView.setAnimation(animation);

        return view;
    }
}
