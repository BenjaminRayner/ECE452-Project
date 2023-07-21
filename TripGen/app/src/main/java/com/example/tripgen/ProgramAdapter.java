package com.example.tripgen;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProgramAdapter extends ArrayAdapter<String> {
    Context context;
    int[] images;
    String[] programName;
    public List<String> choosen_location_names  = new ArrayList<String>();
    List<Integer> choosen_location_images  = new ArrayList<>();
    private int startTimeHour, startTimeMin, endTimeHour, endTimeMin;
    private String name;
    private int image;
    public ProgramAdapter(@NonNull Context context, String[] programName, int[] images){
        super(context, R.layout.single_list_item, R.id.textView1, programName);
        this.context = context;
        this.programName = programName;
        this.images = images;
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
                // Start Time dialog
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeHour = hourOfDay;
                        startTimeMin = minute;
                        // End Time dialog
                        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endTimeHour = hourOfDay;
                                endTimeMin = minute;
                                // add activity to itinerary
                                setPlace(programName[position]);
                                name = programName[position];
                                image = images[position];
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, startTimeHour, startTimeMin, true);
                        timePickerDialog.setTitle("Select End Time");
                        timePickerDialog.show();
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, startTimeHour, startTimeMin, true);
                timePickerDialog.setTitle("Select Start Time");
                timePickerDialog.show();
                System.out.println(choosen_location_names);
            }
        });
        return singleItem;
    }

    private void setPlace(String name){
        Toast.makeText(getContext(), "Place: " + name + " added to itinerary", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("Places").child(name).setValue(true);
    }
    public List<String> getPlacesList(){
        return choosen_location_names;
    }
    public String getPlaceName(){
        return name;
    }
    public int getPlaceImage() {
        return image;
    }
    public int getStartTimeHour(){
        return startTimeHour;
    }
    public int getStartTimeMin(){
        return startTimeMin;
    }
    public int getEndTimeHour(){
        return endTimeHour;
    }
    public int getEndTimeMin(){
        return endTimeMin;
    }
    public List<Integer> getImageList(){
        return choosen_location_images;
    }
}
