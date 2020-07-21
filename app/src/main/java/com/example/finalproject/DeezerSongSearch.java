package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DeezerSongSearch extends AppCompatActivity {
    ProgressBar progressBar;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_search);
        progressBar = findViewById(R.id.progressbar1);
        TextView txt = findViewById(R.id.EnterArtist);
        EditText artistName = findViewById(R.id.artist);
        pref = getSharedPreferences("ArtistFile", Context.MODE_PRIVATE);
        String savedString = pref.getString("ReserveName", " ");
        artistName.setText(savedString);

        Button search = findViewById(R.id.Search);
        search.setOnClickListener( v -> {
            if (!artistName.equals(" ")) saveSharedPrefs(artistName.getText().toString());
            Intent deezerAct1 = new Intent(DeezerSongSearch.this, Deezer_activity1.class);
            startActivity(deezerAct1);

        });



    }
    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ReserveName", stringToSave);
        editor.commit();
        Toast.makeText(this, "Your request is proceed " , Toast.LENGTH_SHORT).show();
    }


}