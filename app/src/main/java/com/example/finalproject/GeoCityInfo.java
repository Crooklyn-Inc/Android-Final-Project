package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GeoCityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_city_info);

        Bundle dataToPass = getIntent().getExtras();

        GeoCityDetailsFragment gcdFragment = new GeoCityDetailsFragment();
        gcdFragment.setArguments(dataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                .commit(); // actually load the fragment. Calls onCreate() in DetailFragment
    }
}