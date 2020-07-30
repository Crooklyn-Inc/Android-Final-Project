package com.example.finalproject.deezer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Deezer_activity2 extends AppCompatActivity {

    JSONObject jObjectArtistDetails;
    String titleOfAlbum = "";
    String titleOfSong = "";
    String durationOfSong = "";
    ImageView albumImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_activity2);


        albumImage = (ImageView) findViewById(R.id.albumImageView);
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

            // contributors node is an Array, so we need to convert it to Array
           JSONArray jimageContributors = jObjectArtistDetails.getJSONArray("contributors");
           String s1 = jimageContributors.get(0).toString();
           JSONObject j2 = new JSONObject(s1);
           String imageurl = j2.getString("picture_small");

            //String imageurl = "https://cdns-images.dzcdn.net/images/artist/1cd9c41f13f3114367692383758be3c1/56x56-000000-80-0-0.jpg";

            albumTitle.setText("Album title: " + titleOfAlbum);
            title.setText("Song name: " + titleOfSong);
            songDuration.setText("Song duration: " + durationOfSong);

            new DownloadImageTask((ImageView) findViewById(R.id.albumImageView))
                    .execute(imageurl);
            Toast.makeText(this, "The image is loading now, please wait " , Toast.LENGTH_SHORT).show();


            Log.e("Tag", "Example Item: " + jObjectArtistDetails.getString("KEY"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView albumtitle = findViewById(R.id.albumTitle);
        albumtitle.setText(titleOfAlbum);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;

        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

        }
    }
}