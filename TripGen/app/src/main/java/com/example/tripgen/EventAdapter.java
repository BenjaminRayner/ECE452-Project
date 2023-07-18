package com.example.tripgen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {

    Context context;
    Event[] events;

    public EventAdapter(Context context, Event[] events) {
        this.context = context;
        this.events = events;
    }


    @Override
    public int getCount() {
        return events.length;
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
            view = LayoutInflater.from(context).inflate(R.layout.layout_event, viewGroup, false);
        }

        ImageView imageView = view.findViewById(R.id.location_image);
        imageView.setImageResource(events[i].getImageResource());

        TextView textView = view.findViewById(R.id.location_name);
        textView.setText(events[i].getName());

        textView = view.findViewById(R.id.location_desc);
        textView.setText(events[i].getDescription());

        textView = view.findViewById(R.id.location_time);
        String time = events[i].getStartTime() + " - " + events[i].getEndTime();
        textView.setText(time);


        return view;
    }
}
