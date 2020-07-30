package com.example.finalproject.soccerMatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.finalproject.R;

public class EmptyActivitySMH extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_smh);


        DetailFragment dFragment = new DetailFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, dFragment)
                .commit();

    }


}