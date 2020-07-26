package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        Button favor = findViewById(R.id.deezFavourites);
        Button search = findViewById(R.id.Search);
        search.setOnClickListener( v -> {
            saveSharedPrefs(artistName.getText().toString());
            Intent deezerAct1 = new Intent(DeezerSongSearch.this, Deezer_activity1.class);
            deezerAct1.putExtra(EXTRA_MESSAGE, artistName.getText().toString().trim());
            startActivity(deezerAct1);
        });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.geoMenuItem:
                message = "You clicked item 1";
                break;
            case R.id.soccerMenuItem:
                message = "You clicked item 2";
                break;
            case R.id.lyricsMenuItem:
                message = "You clicked item 3";
                break;
            case R.id.aboutProject:
                Toast.makeText(this,getResources().getString(R.string.deezToolAboutProject), Toast.LENGTH_LONG).show();
                break;

        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
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
        menuInflater.inflate(R.menu.deezer_menu, menu);
        return true;
    }





}