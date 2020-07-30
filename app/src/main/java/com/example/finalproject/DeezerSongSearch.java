package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.sls.SongLyricsSearch;
import com.google.android.material.navigation.NavigationView;

public class DeezerSongSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressBar progressBar;
    SharedPreferences pref;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener( this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        String message = "";

        Button favor = findViewById(R.id.deezFavourites);
        favor.setOnClickListener( v -> {
            Intent deezerAct3 = new Intent(DeezerSongSearch.this, Deezer_activity3.class);
            deezerAct3.putExtra(EXTRA_MESSAGE, artistName.getText().toString().trim());
            startActivity(deezerAct3);
        });
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
                message= getResources().getString(R.string.dzdToastTB);
                startActivity(new Intent(this, GeoDataSource.class));
                break;
            case R.id.soccerMenuItem:
                message = getResources().getString(R.string.dzdToastTB2);
                startActivity(new Intent(this, SoccerMatchHighlights.class));
                break;
            case R.id.lyricsMenuItem:
                message = getResources().getString(R.string.dzdToastTB3);
                startActivity(new Intent(this, SongLyricsSearch.class));
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
        Toast.makeText(this, getResources().getString(R.string.dzdToastRequest) , Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.deezer_menu, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.dzIstructions:

                if (!menuItem.isChecked()) {
       //             selectedIndex = ALERT_DIALOG;
                    menuItem.setChecked(true);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(DeezerSongSearch.this);
                    dialog.setIcon(R.drawable.ic_deezer);
                    dialog.setTitle(R.string.genInstructions);

               dialog.setMessage(getString(R.string.instructions)) ;
               dialog.create().show();
                return true;}
                    drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.dzAPI:
                if (!menuItem.isChecked()) {
                    //             selectedIndex = ALERT_DIALOG;
                    menuItem.setChecked(true);

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developers.deezer.com/guidelines"));
                    startActivity(browserIntent);
                }
             //   startActivity(new Intent(this, Activity1.class));
                break;
            case R.id.dzDonate:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.donationTitle);
                alert.setMessage(R.string.donationMessage);
                final EditText input = new EditText(this);
                input.setHint("$$$");
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(input);
                alert.setPositiveButton((R.string.thanks), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here
                    }
                });
                alert.setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alert.show();
                break;
        //    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
       //     drawerLayout.closeDrawer(GravityCompat.START);

        //   Toast.makeText(this, "NavigationDrawer: " +  Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
