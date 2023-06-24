package com.example.tripgen;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripgen.databinding.ActivityMainBinding;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    String [] location_names = {"CN Tower", "Casa Loma", "ROM"};
    int [] location_images = {R.drawable.cn_tower, R.drawable.casa_loma, R.drawable.rom};
    @Override
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.toolbar);

            ListView listView = findViewById(R.id.listView);
            ProgramAdapter programAdapter = new ProgramAdapter(this, location_names, location_images);
            listView.setAdapter(programAdapter);

            //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
            //        new String[] {"CN Tower", "Royal Ontario Museum", "Casa Loma", "Delete", "Convert", "Open", "aiwiaowpaooooooooo", "wiaoijdaijojdoiwoijaoi"}));
        }
}
