package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.finalproject.soccerMatch.EmptyActivitySMH;
import com.example.finalproject.soccerMatch.ListOfFavourites;
import com.example.finalproject.soccerMatch.Match;
import com.example.finalproject.soccerMatch.MyOpener;
import com.example.finalproject.soccerMatch.ViewInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.function.ToLongBiFunction;


public class SoccerMatchHighlights extends AppCompatActivity {
    public ListView listItems;
    public static ArrayList<Match> matchArrayList = new ArrayList<>();
    private MyListAdapter myAdapter;
    private ProgressBar progressBar;
    public ImageView thumbnail;
    public Button showFavourites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_match_highlights);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarSMH);
        setSupportActionBar(myToolbar);

        listItems = findViewById(R.id.list_itemsSMH);
        progressBar = findViewById(R.id.progressBarSMH);
        progressBar.setVisibility(View.VISIBLE);
        thumbnail = findViewById(R.id.thumbnail);
        showFavourites = findViewById(R.id.showFavourites);
        myAdapter = new MyListAdapter();
        listItems.setAdapter(myAdapter);

        showFavourites.setOnClickListener(click -> {
            Intent i = new Intent(SoccerMatchHighlights.this, ListOfFavourites.class);
            startActivity(i);
        });

        MatchQuery reqInfo = new MatchQuery();
        reqInfo.execute("https://www.scorebat.com/video-api/v1/");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.infoSMH:
                Intent nextActivity = new Intent(SoccerMatchHighlights.this, EmptyActivitySMH.class);
                startActivity(nextActivity);

                break;

            case R.id.aboutSMH:

                AlertDialog.Builder alertD = new AlertDialog.Builder(SoccerMatchHighlights.this);

                alertD.setTitle("Information how this app works")
                        .setMessage("In the main page you can see different soccer matches sorted by date." +
                                "\nYou can click on the match to see more details about it (Teams, League, Date, Video)." +
                                "\nYou can also save match that you like into the list of favourites by clicking the appropriate button." +
                                "\n By clicking on \"Show Favourites\" button you can access your custom list with possibility see details and remove from the list.  ")
                        .setNegativeButton("Back",((dialog, which) -> {

                        })).create().show();
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.smh_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        matchArrayList.clear();
    }

    public class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return matchArrayList.size();
        }

        public Match getItem(int position) {
            return matchArrayList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }


        public View getView(int position, View old, ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View newView;
            Match m = (Match) getItem(position);


            newView = inflater.inflate(R.layout.activity_soccer_match_highlights_single_item, parent, false);
            Button b = (Button) newView.findViewById(R.id.title);
            b.setOnClickListener(click -> {
                Intent intent = new Intent(SoccerMatchHighlights.this, ViewInfo.class);

                intent.putExtra("id", m.getId());
                startActivity(intent);
            });


            TextView textView = newView.findViewById(R.id.title);
            textView.setText(m.getTitle());

            return newView;
        }


    }

    public class MatchQuery extends AsyncTask<String, Integer, String> {

        ArrayList<String> titleArray = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream jsonResponse = urlConnection.getInputStream();


                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();


                JSONArray jArray = new JSONArray(result);


                int publishProgress = 4;
                for (int i = 0; i < 25; i++) {

                    try {
                        Match m = new Match();

                        JSONObject o = jArray.getJSONObject(i);
                        JSONArray videos = o.getJSONArray("videos");
                        JSONObject embed = videos.getJSONObject(0);
                        String videoXML = embed.getString("embed");
                        videoXML = videoXML.substring(90, 141);
                        publishProgress(publishProgress);
                        publishProgress += 10;
                        m.setTitle(o.getString("title"));
                        m.setDate(o.getString("date"));
                        m.setThumbnail(o.getString("thumbnail"));
                        m.setCompetitionName(o.getJSONObject("competition").getString("name"));
                        m.setTeam1(o.getJSONObject("side1").getString("name"));
                        m.setTeam2(o.getJSONObject("side2").getString("name"));
                        m.setVideoUrl(videoXML);
                        m.setId(i);

                        matchArrayList.add(m);


                    } catch (Exception ex) {

                    }
                    publishProgress(publishProgress);
                    publishProgress += 4;

                }


            } catch (Exception e) {
                Log.e("Async Task", "Invalid URL");
            }

            return "Done";
        }


        @Override
        public void onProgressUpdate(Integer... args) {
            progressBar.setVisibility(View.VISIBLE);

            progressBar.setProgress(args[0]);


        }

        public void onPostExecute(String fromDoInBackground) {
            myAdapter.notifyDataSetChanged();
            progressBar.setProgress(100);

//            progressBar.setVisibility(View.INVISIBLE);
        }

    }

}