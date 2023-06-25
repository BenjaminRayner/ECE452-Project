package com.example.tripgen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TripFragment tripFragment = new TripFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.trip_fragment_container, tripFragment)
                .commit();
    }
}