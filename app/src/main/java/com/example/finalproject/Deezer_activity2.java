package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Deezer_activity2 extends AppCompatActivity {

    JSONObject jObjectArtistDetails;
    String titleOfAlbum = "";
    String titleOfSong = "";
    String durationOfSong = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_activity2);

        ImageView albumImage = (ImageView) findViewById(R.id.albumImageView);
        TextView albumTitle = (TextView) findViewById(R.id.albumTitle);
        TextView title = (TextView) findViewById(R.id.title);
        TextView songDuration = (TextView) findViewById(R.id.songDuration);
        ProgressBar progressBar = findViewById(R.id.progressBarActivity2);

        try {
            jObjectArtistDetails = new JSONObject(getIntent().getStringExtra("ARTIST_DETAILS"));
            //JSONArray dataArraySongs = jObjectArtistDetails.getJSONArray("artist");
            //JSONObject jobjSongDetails = dataArraySongs.getJSONObject(0);

            titleOfSong = jObjectArtistDetails.getString("title");
            durationOfSong = jObjectArtistDetails.getString("duration");
            titleOfAlbum = jObjectArtistDetails.getJSONObject("album").getString("title");

            albumTitle.setText("Album title: " + titleOfAlbum);
            title.setText("Song name: " + titleOfSong);
            songDuration.setText("Song duration: " + durationOfSong);

            Log.e("Tag", "Example Item: " + jObjectArtistDetails.getString("KEY"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView albumtitle = findViewById(R.id.albumTitle);
        albumtitle.setText(titleOfAlbum);

    }
}