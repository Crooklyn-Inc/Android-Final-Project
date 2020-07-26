package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DeezerSongSearch extends AppCompatActivity {
    ProgressBar progressBar;
    SharedPreferences pref;
    public static String EXTRA_MESSAGE = "ARTIST_SONG_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_search);
        progressBar = findViewById(R.id.progressbar1);
        TextView txt = findViewById(R.id.EnterArtist);
        EditText artistName = findViewById(R.id.artist);
        pref = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = pref.getString("ReserveName", "");
        artistName.setText(savedString);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.deezerToolbar);
        setSupportActionBar(myToolbar);


        String message = "";

        Button search = findViewById(R.id.Search);
        search.setOnClickListener( v -> {
            saveSharedPrefs(artistName.getText().toString());
            Intent deezerAct1 = new Intent(DeezerSongSearch.this, Deezer_activity1.class);
            deezerAct1.putExtra(EXTRA_MESSAGE, artistName.getText().toString().trim());
            startActivity(deezerAct1);
        });



    }
    private void saveSharedPrefs(String savedString) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ReserveName", savedString);
        editor.commit();
        Toast.makeText(this, "Your request is proceed " , Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.deezer_ьутг, menu);
        return true;
    }





}