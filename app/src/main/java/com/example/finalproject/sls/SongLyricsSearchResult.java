package com.example.finalproject.sls;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.DeezerSongSearch;
import com.example.finalproject.GeoDataSource;
import com.example.finalproject.R;
import com.example.finalproject.SoccerMatchHighlights;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;
import com.google.android.material.navigation.NavigationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongLyricsSearchResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MessageDao messageDao;
    private Long       songId;
    private boolean    isSongFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sls_result);

        Toolbar myToolbar = findViewById(R.id.slsToolbar);
        setSupportActionBar(myToolbar);

        NavigationView navigationView = findViewById(R.id.slsNavView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout          drawer = findViewById(R.id.slsDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent searchData = getIntent();
        String bandName   = searchData.getStringExtra(SongLyricsSearch.BAND);
        String songText   = searchData.getStringExtra(SongLyricsSearch.SONG);
        String lyricsText = searchData.getStringExtra(SongLyricsSearch.LYRICS);

        TextView band = findViewById(R.id.slsGroupName);
        TextView song = findViewById(R.id.slsSongName);
        band.setText(bandName.toUpperCase());
        song.setText(songText.toUpperCase());

        isSongFavorite(bandName, songText);

        TextView lyrics = findViewById(R.id.slsLyricsName);

        lyrics.setMovementMethod(new ScrollingMovementMethod());

        if (lyricsText == null || lyricsText.isEmpty()) {
            SongLyricsSearchNetwork            searchNetwork = new SongLyricsSearchNetwork(findViewById(R.id.slsLyricsName).getRootView());
            AsyncTask<String, Integer, String> asd           = searchNetwork.execute(band.getText().toString(), song.getText().toString());
            try {
                lyricsText = asd.get();
                if (lyricsText != null && !lyricsText.isEmpty()) {
                    lyrics.setText(lyricsText);
                    TextView nf = findViewById(R.id.slsLyricsNameNotFound);
                    nf.setText("");
                } else {
                }
            } catch (ExecutionException | InterruptedException e) {e.printStackTrace(); }
        } else {
            lyrics.setText(lyricsText);
        }

        Button favBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        if (isSongFavorite) {
            favBtn.setText("Remove Favorite");
        }
        favBtn.setOnClickListener(btn -> {
            if (isSongFavorite) {
                deleteFromFavorites(songId);
            } else {
                addToFavoriteList();
            }
        });

        Button gglBtn = findViewById(R.id.slsSearchGoogleBtn);
        gglBtn.setOnClickListener(btn->{
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + URLEncoder.encode(bandName, "utf8") + "/" + URLEncoder.encode(songText, "utf8"))));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

    }

    private void deleteFromFavorites(Long songId) {
        messageDao = new MessageDao(this);
        messageDao.open();
        messageDao.delete(messageDao.findById(songId));
        Button favBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText("Add to Favorite");
        isSongFavorite = false;
    }

    private boolean isSongFavorite(String bandName, String songText) {
        messageDao = new MessageDao(this);
        messageDao.open();

        List<MessageDTO> favList = messageDao.findAll();
        for (MessageDTO record : favList) {
            if (record.getBand().equalsIgnoreCase(bandName) && record.getSong().equalsIgnoreCase(songText)) {
                songId         = record.getId();
                isSongFavorite = true;
                messageDao.close();
                return true;
            }
        }
        messageDao.close();
        return false;

    }

    private void addToFavoriteList() {
        messageDao = new MessageDao(this);
        messageDao.open();

        TextView b = findViewById(R.id.slsGroupName);
        TextView s = findViewById(R.id.slsSongName);
        TextView l = findViewById(R.id.slsLyricsName);

        MessageDTO newMessage = messageDao.create(
            new MessageDTO(b.getText().toString(),
                s.getText().toString(),
                l.getText().toString()));
        messageDao.close();
        Button favBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText("Remove Favorite");
        isSongFavorite = true;
        songId         = newMessage.getId();
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


}
