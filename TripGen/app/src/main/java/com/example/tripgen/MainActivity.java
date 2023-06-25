package com.example.tripgen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripgen.databinding.ActivityMainBinding;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    String [] location_names = {"CN Tower", "Casa Loma", "ROM", "Ripley's Aquarium"};
    int [] location_images = {R.drawable.cn_tower, R.drawable.casa_loma, R.drawable.rom, R.drawable.ripleys};

    List<String> choosen_location_names  = new ArrayList<String>();

    List<Integer> choosen_location_images = new ArrayList<Integer>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            ListView listViewMenu = findViewById(R.id.listView);
            ProgramAdapter programAdapter = new ProgramAdapter(this, location_names, location_images);
            listViewMenu.setAdapter(programAdapter);

            ListView listViewChoosen = findViewById(R.id.displayItineraryListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, programAdapter.getPlace());
            listViewChoosen.setAdapter(adapter);

            listViewChoosen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("Clicked: ", programAdapter.getPlace().get(position));
                }
            });

            //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
            //        new String[] {"CN Tower", "Royal Ontario Museum", "Casa Loma", "Delete", "Convert", "Open", "aiwiaowpaooooooooo", "wiaoijdaijojdoiwoijaoi"}));
        }




}
