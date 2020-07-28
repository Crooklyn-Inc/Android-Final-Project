package com.example.finalproject.sls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.finalproject.R;

public class SongLyricsSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_lyrics_search);


        Button btnSongLyrics = findViewById(R.id.slsSearchLyricsBtn);
        btnSongLyrics.setOnClickListener(v -> {
            Intent intentSongLyrics = new Intent(SongLyricsSearch.this, SongLyricsSearchResult.class);
            startActivity(intentSongLyrics);
        });


    }
}
