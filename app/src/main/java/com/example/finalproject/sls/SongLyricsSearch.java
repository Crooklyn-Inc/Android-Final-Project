package com.example.finalproject.sls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.DeezerSongSearch;
import com.example.finalproject.GeoDataSource;
import com.example.finalproject.R;
import com.example.finalproject.SoccerMatchHighlights;
import com.google.android.material.navigation.NavigationView;

public class SongLyricsSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFERENCES   = "prefs";
    public static final String BAND          = "band";
    public static final String SONG          = "song";
    public static final String LYRICS        = "lyrics";
    public static final String SEARCH_STRING = "search_string";
    private             String searchString   = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sls_search);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        TextView ss = findViewById(R.id.slsSearchInput);
        ss.setText(sharedPreferences.getString(SEARCH_STRING, ""));

        Toolbar myToolbar = findViewById(R.id.slsToolbar);
        setSupportActionBar(myToolbar);

        NavigationView navigationView = findViewById(R.id.slsNavView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout          drawer = findViewById(R.id.slsDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Button btnSongLyrics = findViewById(R.id.slsSearchLyricsBtn);
        btnSongLyrics.setOnClickListener(v -> {
            Intent intentSongLyrics = new Intent(SongLyricsSearch.this, SongLyricsSearchResult.class);

            TextView bs           = findViewById(R.id.slsSearchInput);
            searchString = bs.getText().toString();
            int      firstBreak   = searchString.indexOf("-");
            if (firstBreak > 0) {
                String band = searchString.substring(0, firstBreak).trim();
                String song = searchString.substring(firstBreak + 1, searchString.length()).trim();
                if ((band != null && !band.isEmpty()) && (song != null && !song.isEmpty())) {
                    intentSongLyrics.putExtra(BAND, band);
                    intentSongLyrics.putExtra(SONG, song);
                    startActivity(intentSongLyrics);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.slsWrongInput), Toast.LENGTH_LONG).show();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.geoInstructionsMenuItem)
                        .setIcon(R.drawable.ic_lyric_song)
                        .setMessage(R.string.slsInstructionsMessage)
                        .setPositiveButton(R.string.ok, (click, arg) -> {})
                        .create()
                        .show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.slsWrongInput), Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.geoInstructionsMenuItem)
                    .setIcon(R.drawable.ic_lyric_song)
                    .setMessage(R.string.slsInstructionsMessage)
                    .setPositiveButton(R.string.ok, (click, arg) -> {})
                    .create()
                    .show();
            }
        });

        Button btnFavoriteList = findViewById(R.id.slsFavouriteList);
        btnFavoriteList.setOnClickListener(v -> {
            Intent intentSongLyrics = new Intent(SongLyricsSearch.this, SongLyricsSearchFavoriteList.class);
            startActivity(intentSongLyrics);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sls_nav_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.geoMenuItem:
                message = "Opening GeoData";
                startActivity(new Intent(this, GeoDataSource.class));
                break;
            case R.id.soccerMenuItem:
                message = "Opening Soccer";
                startActivity(new Intent(this, SoccerMatchHighlights.class));
                break;
            case R.id.deezerMenuItem:
                message = "Opening Deezer";
                startActivity(new Intent(this, DeezerSongSearch.class));
                break;
            case R.id.aboutProject:
                Toast.makeText(this, getResources().getString(R.string.slsToolAboutProject), Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.appInstructionsMenuItem) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.geoInstructionsMenuItem)
                .setIcon(R.drawable.ic_lyric_song)
                .setMessage(R.string.slsInstructionsMessage)
                .setPositiveButton(R.string.ok, (click, arg) -> {})
                .create()
                .show();
        } else if (item.getItemId() == R.id.aboutApiMenuItem) {
            startActivity(new Intent(Intent.ACTION_VIEW));
        } else if (item.getItemId() == R.id.donateToProjectMenuItem) {

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(40, 0, 40, 0);
            final EditText input = new EditText(this);
            input.setLayoutParams(lp);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setLines(1);
            input.setMaxLines(1);
            input.setHint(R.string.geoThreeCurrencySigns);
            container.addView(input, lp);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.geoDonationTitle)
                .setIcon(R.drawable.ic_geo_city)
                .setMessage(R.string.geoDonationMessage)
                .setView(container)
                .setPositiveButton(getResources().getString(R.string.geoThankYou), (click, arg) -> {})
                .setNegativeButton(R.string.geoCancel, (click, arg) -> { })
                .setView(container)
                .create()
                .show();
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // checking if the email has been entered
        // (in this case it should be anything more than 0 characters)
        if (this.searchString.length() > 0) {

            // and saving email to SharedPreferences
            SharedPreferences        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor            = sharedPreferences.edit();
            editor.putString(SEARCH_STRING, this.searchString);
            editor.commit();
        }
    }

}
