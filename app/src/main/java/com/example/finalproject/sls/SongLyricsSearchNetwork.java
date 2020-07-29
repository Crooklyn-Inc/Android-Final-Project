package com.example.finalproject.sls;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.finalproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SongLyricsSearchNetwork extends AsyncTask<String, Integer, String> {

    private View rootView;

    public SongLyricsSearchNetwork(View rootView) {
        this.rootView = rootView;
    }

    private ProgressBar progressBar;
    private String      songLyrics;

    @Override
    protected String doInBackground(String... strings) {
        progressBar = rootView.findViewById(R.id.slsProgressBar);

        try {
            getLyrics(strings[0], strings[1]);
            TextView lyricsText = rootView.findViewById(R.id.slsLyricsName);
            //lyricsText.setText(songLyrics);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return songLyrics;
    }

    private void getLyrics(String band, String song) throws IOException, JSONException {
        String link = "https://api.lyrics.ovh/v1/" + URLEncoder.encode(band, "utf8") + "/" + URLEncoder.encode(song, "utf8");

        HttpURLConnection openConnection = getOpenConnection(link);
        InputStream       stream         = openConnection.getInputStream();
        JSONObject        jsonObject     = new JSONObject(convertStreamToString(stream));
        songLyrics = jsonObject.getString("lyrics");
        publishProgress(100);
    }

    private HttpURLConnection getOpenConnection(String link) throws IOException {
        URL url = new URL(link);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        return connection;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder  sb     = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    protected void onProgressUpdate(Integer... value) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(value[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        TextView lyricsName = rootView.findViewById(R.id.slsLyricsName);
        lyricsName.setText(songLyrics);

        progressBar.setVisibility(View.INVISIBLE);
    }
}
