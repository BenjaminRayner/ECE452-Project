package com.example.tripgen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    ListView listView;
    Animation animation;
    String[] trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        listView = findViewById(R.id.list_view);
        trips = getResources().getStringArray(R.array.trips);

        TripAdapter adapter = new TripAdapter(TestActivity.this, trips);
        animation= AnimationUtils.loadAnimation(this, R.anim.animation1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), ""+trips[i], Toast.LENGTH_SHORT).show();
            }
        });
    }
}