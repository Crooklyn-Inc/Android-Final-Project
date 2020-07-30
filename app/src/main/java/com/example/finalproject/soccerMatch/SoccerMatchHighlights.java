package com.example.finalproject.soccerMatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.deezer.DeezerSongSearch;
import com.example.finalproject.geo.GeoDataSource;
import com.example.finalproject.sls.SongLyricsSearch;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SoccerMatchHighlights extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public ListView listItems;
    public static ArrayList<Match> matchArrayList = new ArrayList<>();
    private MyListAdapter myAdapter;
    private ProgressBar progressBar;
    public ImageView thumbnail;
    public Button showFavourites;
    FrameLayout frameLayout;
    boolean tablet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_match_highlights);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarSMH);
        setSupportActionBar(myToolbar);

        NavigationView navigationView = findViewById(R.id.smh_nav_menu);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout smhDrawer = findViewById(R.id.smh_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                smhDrawer, myToolbar, R.string.open, R.string.close);
        smhDrawer.addDrawerListener(toggle);
        toggle.syncState();


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

        tablet = findViewById(R.id.frameLayout) != null;

        frameLayout = findViewById(R.id.frameLayout);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.infoSMH:
                Bundle dataToPass = new Bundle();
                dataToPass.putString("Title", getResources().getString(R.string.titleSMH));
                dataToPass.putString("TitleText", getResources().getString(R.string.titleTextSMH));
                dataToPass.putString("Version", getResources().getString(R.string.version));
                dataToPass.putString("VersionText", getResources().getString(R.string.versionTextSMH));
                dataToPass.putString("Author", getResources().getString(R.string.author));
                dataToPass.putString("AuthorText", getResources().getString(R.string.myNameSMH));

                if (tablet) {
                    DetailFragment dFragment = new DetailFragment();
                    dFragment.setArguments(dataToPass);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, dFragment)
                            .commit();
                } else {
                    Intent nextActivity = new Intent(SoccerMatchHighlights.this, EmptyActivitySMH.class);
                    startActivity(nextActivity);
                }
                break;

            case R.id.aboutSMH:
                AlertDialog.Builder alertD = new AlertDialog.Builder(SoccerMatchHighlights.this);
                alertD.setTitle(getResources().getString(R.string.infoSMH))
                        .setMessage(getResources().getString(R.string.explSMH))
                        .setNegativeButton(getResources().getString(R.string.backSMH), ((dialog, which) -> {

                        })).create().show();
                break;

            case R.id.geoMenuItem:
                Intent geo = new Intent(SoccerMatchHighlights.this, GeoDataSource.class);
                startActivity(geo);
                break;

            case R.id.lyricsMenuItem:
                Intent lyrics = new Intent(SoccerMatchHighlights.this, SongLyricsSearch.class);
                startActivity(lyrics);
                break;

            case R.id.deezerMenuItem:
                Intent deezer = new Intent(SoccerMatchHighlights.this, DeezerSongSearch.class);
                startActivity(deezer);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.smhInstructions) {

            AlertDialog.Builder alertD = new AlertDialog.Builder(SoccerMatchHighlights.this);
            alertD.setTitle(getResources().getString(R.string.infoSMH))
                    .setMessage(getResources().getString(R.string.explSMH))
                    .setNegativeButton(getResources().getString(R.string.backSMH), ((dialog, which) -> {

                    })).create().show();

        } else if (item.getItemId() == R.id.smhAPI) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.scorebat.com/video-api/"));
            startActivity(intent);

        } else if (item.getItemId() == R.id.smhDonate) {
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
                    Toast.makeText(getApplicationContext(), "Thank you for your donation!", Toast.LENGTH_LONG).show();
                }
            });
            alert.setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Put actions for CANCEL button here, or leave in blank
                }
            });
            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.smh_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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