package com.example.finalproject.sls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.finalproject.R;

public class SongLyricsSearch extends AppCompatActivity {

    public static final String PREFERENCES = "prefs";
    public static final String BAND        = "band";
    public static final String SONG        = "song";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_lyrics_search);

        Button btnSongLyrics = findViewById(R.id.slsSearchLyricsBtn);
        btnSongLyrics.setOnClickListener(v -> {
            Intent intentSongLyrics = new Intent(SongLyricsSearch.this, SongLyricsSearchResult.class);

            TextView bs           = findViewById(R.id.slsSearchInput);
            String   searchString = bs.getText().toString();
            int      firstBreak   = searchString.indexOf("-");
            String   band         = searchString.substring(0, firstBreak).trim();
            String   song         = searchString.substring(firstBreak + 1, searchString.length()).trim();

            intentSongLyrics.putExtra(BAND, band);
            intentSongLyrics.putExtra(SONG, song);

            startActivity(intentSongLyrics);
        });

        Button btnFavoriteList = findViewById(R.id.slsFavouriteList);
        btnFavoriteList.setOnClickListener(v -> {
            Intent intentSongLyrics = new Intent(SongLyricsSearch.this, SongLyricsSearchFavoriteList.class);
            startActivity(intentSongLyrics);
        });
    }
}
