package com.example.tripgen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProgramAdapter extends ArrayAdapter<String> {
    Context context;
    int[] images;
    String[] programName;
    List<String> choosen_location_names  = new ArrayList<String>();

    public ProgramAdapter(@NonNull Context context, String[] programName, int[] images){
        super(context, R.layout.single_list_item, R.id.textView1, programName);
        this.context = context;
        this.images = images;
        this.programName = programName;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View singleItem = convertView;

        ProgramViewHolder holder = null;

        if(singleItem == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_list_item, parent, false);
            holder = new ProgramViewHolder(singleItem);
            singleItem.setTag(holder);
        }
        else{
            holder = (ProgramViewHolder) singleItem.getTag();
        }
        holder.itemImage.setImageResource(images[position]);
        holder.programTitle.setText(programName[position]);


        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Added: "+ programName[position], Toast.LENGTH_SHORT).show();
                setPlace(programName[position]);
            }
        });
        return singleItem;
    }

    private void setPlace(String name){
        FirebaseDatabase.getInstance().getReference().child("Places").child(name).setValue(true);
//        choosen_location_names.add(name);
    }
    public List<String> getPlace(){
        return choosen_location_names;
    }
}
