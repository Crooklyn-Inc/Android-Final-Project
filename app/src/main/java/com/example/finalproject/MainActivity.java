package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.example.finalproject.deezer.DeezerSongSearch;
import com.example.finalproject.geo.GeoDataSource;
import com.example.finalproject.sls.SongLyricsSearch;
import com.example.finalproject.soccerMatch.SoccerMatchHighlights;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        DrawerLayout mainDrawerLayout = findViewById(R.id.mainDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mainDrawerLayout, mainToolbar, R.string.open, R.string.close);
        mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.mainNavigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent;

        if (item.getItemId() ==  R.id.geoMenuItem) {
            intent = new Intent(MainActivity.this, GeoDataSource.class);
        }
        else if (item.getItemId() ==  R.id.soccerMenuItem) {
            intent = new Intent(MainActivity.this, SoccerMatchHighlights.class);
        }
        else if (item.getItemId() ==  R.id.lyricsMenuItem) {
            intent = new Intent(MainActivity.this, SongLyricsSearch.class);
        }
        else {
            intent = new Intent(MainActivity.this, DeezerSongSearch.class);
        }

        startActivity(intent);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        if (item.getItemId() ==  R.id.geoMenuItem) {
            intent = new Intent(MainActivity.this, GeoDataSource.class);
        }
        else if (item.getItemId() ==  R.id.soccerMenuItem) {
            intent = new Intent(MainActivity.this, SoccerMatchHighlights.class);
        }
        else if (item.getItemId() ==  R.id.lyricsMenuItem) {
            intent = new Intent(MainActivity.this, SongLyricsSearch.class);
        }
        else {
            intent = new Intent(MainActivity.this, DeezerSongSearch.class);
        }

        DrawerLayout drawerLayout = findViewById(R.id.mainDrawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);

        startActivity(intent);
        return false;
    }
}
