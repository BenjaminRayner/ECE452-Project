package com.example.tripgen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

    Context context;
    String[] list;

    public ListAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.length;
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
        textView.setText(list[i]);

        return view;
    }
}