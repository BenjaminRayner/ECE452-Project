package com.example.tripgen;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    List<String> titleList;
    List<Integer> imageList;
    Fragment mFragment;

    NavController navController;

    public RecyclerViewAdapter(Fragment fragment, List<String> titleList, List<Integer> imageList) {
        this.mFragment = fragment;
        this.navController = navController;
        this.titleList = titleList;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // ****
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.rowCountTextView.setText(String.valueOf(position));
        holder.textView.setText(titleList.get(position));

        // images currently disabled
        holder.imageView.setImageResource(0);
        holder.itemView.setOnClickListener(view ->{
            NavHostFragment.findNavController(mFragment)
                        .navigate(R.id.action_ItineraryFragment_to_thirdFragment);
        });

    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public void removeItem(int index) {
        titleList.remove(index);
        //imageList.remove(index);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, rowCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewItinerary);
            // ****
            textView = itemView.findViewById(R.id.textViewItinerary);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), titleList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}

