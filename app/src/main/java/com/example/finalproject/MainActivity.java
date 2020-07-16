package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGeoData = findViewById(R.id.btnGeoData);
        btnGeoData.setOnClickListener( v -> {
            Intent intentGeoData = new Intent(MainActivity.this, GeoDataSource.class);
            startActivity(intentGeoData);
        });

        Button btnSoccer = findViewById(R.id.btnSoccer);
        btnSoccer.setOnClickListener( v -> {
            Intent intentSoccerMatch = new Intent(MainActivity.this, SoccerMatchHighlights.class);
            startActivity(intentSoccerMatch);
        });

        Button btnSongLyrics = findViewById(R.id.btnSongLyrics);
        btnSongLyrics.setOnClickListener( v -> {
            Intent intentSongLyrics = new Intent(MainActivity.this, SongLyricsSearch.class);
            startActivity(intentSongLyrics);
        });

        Button btnDeezerSong = findViewById(R.id.btnDeezerSong);
        btnDeezerSong.setOnClickListener( v -> {
            Intent intentDeezerSong = new Intent(MainActivity.this, DeezerSongSearch.class);
            startActivity(intentDeezerSong);
        });
    }
}