package com.example.finalproject.geo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.deezer.DeezerSongSearch;
import com.example.finalproject.R;
import com.example.finalproject.soccerMatch.SoccerMatchHighlights;
import com.example.finalproject.sls.SongLyricsSearch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GeoDataSource extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FORMATTED_REQUEST = "https://api.geodatasource.com/cities?key=%S&format=json&lat=%S&lng=%S";
    private static final String API_KEY = "YR5XMSKGHMHTIWDKTLRADSUYNYTJNYNK";
    private static final String LINK_TO_GEODATASOURCE = "https://www.geodatasource.com/web-service";
    private static final String PREF_FILE_NAME = "geo_preferences";
    private static final String LATITUDE_RES_NAME = "latitude";
    private static final String LONGITUDE_RES_NAME = "longitude";

    public static final List<GeoAttr> ATTR_MAP = new ArrayList<>(13);
    public static final String ACTIVITY_NAME = "GEO_DATA_SOURCE";
    public static final int LAT_INDEX = 4;
    public static final int LON_INDEX = 5;
    public static final String JSON_INTENT_DATA = "JSON";

    private static final int MIN_PROGRESS = 0;
    private static final int MAX_PROGRESS = 100;
    static final int REQUEST_CITY_DISPLAY = 100;

    private SharedPreferences prefs = null;
    private EditText geoEditTxtLat;
    private EditText geoEditTxtLon;
    private Button geoBtnSearchCities;
    private Button geoBtnFavourites;
    private ProgressBar geoProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_data_source);

        Toolbar geoToolbar = (Toolbar)findViewById(R.id.geoToolbar);
        setSupportActionBar(geoToolbar);

        DrawerLayout mainDrawerLayout = findViewById(R.id.geoDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mainDrawerLayout, geoToolbar, R.string.open, R.string.close);
        mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.geoNavigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        geoEditTxtLat = (EditText) findViewById(R.id.geoEditTxtLat);
        geoEditTxtLon = (EditText) findViewById(R.id.geoEditTxtLon);
        geoBtnSearchCities = (Button) findViewById(R.id.geoBtnSearchCities);
        geoBtnFavourites = (Button) findViewById(R.id.geoBtnFavourites);
        geoProgressBar = (ProgressBar) findViewById(R.id.geoProgressBar);
        geoProgressBar.setVisibility(View.INVISIBLE);
        geoProgressBar.setMax(MAX_PROGRESS);

        prefs = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        geoEditTxtLat.setText(prefs.getString(LATITUDE_RES_NAME, ""));
        geoEditTxtLon.setText(prefs.getString(LONGITUDE_RES_NAME, ""));

        if (ATTR_MAP.isEmpty()) {
            ATTR_MAP.add(new GeoAttr("_id", R.string.geoCityAttr_0, false, 4));                //  "Database Record ID"
            ATTR_MAP.add(new GeoAttr("city", R.string.geoCityAttr_1, false, 24));               //  "City"
            ATTR_MAP.add(new GeoAttr("country", R.string.geoCityAttr_2, false, 12));            //  "Country"
            ATTR_MAP.add(new GeoAttr("region", R.string.geoCityAttr_3, false, 20));             //  "Region"
            ATTR_MAP.add(new GeoAttr("latitude", R.string.geoCityAttr_4, true, 9));            //  "Latitude"
            ATTR_MAP.add(new GeoAttr("longitude", R.string.geoCityAttr_5, true, 10));           //  "Longitude"
            ATTR_MAP.add(new GeoAttr("currency_code", R.string.geoCityAttr_6, false, 13));      //  "Currency Code"
            ATTR_MAP.add(new GeoAttr("currency_name", R.string.geoCityAttr_7, false, 22));      //  "Currency Name"
            ATTR_MAP.add(new GeoAttr("currency_symbol", R.string.geoCityAttr_8, false, 15));    //  "Currency Symbol"
            ATTR_MAP.add(new GeoAttr("sunrise", R.string.geoCityAttr_9, false, 7));            //  "Sunrise"
            ATTR_MAP.add(new GeoAttr("sunset", R.string.geoCityAttr_10, false, 6));            //  "Sunset"
            ATTR_MAP.add(new GeoAttr("time_zone", R.string.geoCityAttr_11, false, 9));         //  "Time Zone"
            ATTR_MAP.add(new GeoAttr("distance_km", R.string.geoCityAttr_12, true, 11));        //  "Distance km"
        }

        geoBtnSearchCities.setOnClickListener( v -> {
            String latStr = geoEditTxtLat.getText().toString().trim();
            String lonStr = geoEditTxtLon.getText().toString().trim();

            if (latStr.isEmpty() || lonStr.isEmpty()) {
                Toast.makeText(GeoDataSource.this, latStr.isEmpty() ? R.string.geoMessageLatitudeEmpty : R.string.geoMessageLongitudeEmpty, Toast.LENGTH_LONG).show();
                return;
            }

            try {
                double latDbl = Double.parseDouble(latStr);
                if (latDbl < -90d || latDbl > 90d) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                Toast.makeText(GeoDataSource.this, R.string.geoMessageInvalidLatitude, Toast.LENGTH_LONG).show();
                return;
            }

            try {
                double lonDbl = Double.parseDouble(lonStr);
                if (lonDbl < -180d || lonDbl > 180d) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                Toast.makeText(GeoDataSource.this, R.string.geoMessageInvalidLongitude, Toast.LENGTH_LONG).show();
                return;
            }
            String request = String.format(FORMATTED_REQUEST, API_KEY, latStr, lonStr);
            geoProgressBar.setVisibility(View.VISIBLE);
            (new GeoDataRequest()).execute(request);
        });

        geoBtnFavourites.setOnClickListener( v -> {
            Intent cityListIntent = new Intent(GeoDataSource.this, GeoCityList.class);
            startActivity(cityListIntent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSharedPrefs(LATITUDE_RES_NAME, geoEditTxtLat.getText().toString().trim());
        saveSharedPrefs(LONGITUDE_RES_NAME, geoEditTxtLon.getText().toString().trim());
    }

    @Override
    protected void onResume() {
        super.onResume();
        geoProgressBar.setVisibility(View.INVISIBLE);
        geoProgressBar.setProgress(MIN_PROGRESS);
        Log.i(ACTIVITY_NAME, "In function: " + "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        geoProgressBar.setVisibility(View.INVISIBLE);
        Log.i(ACTIVITY_NAME, "In function: " + "onStart");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() ==  R.id.appInstructionsMenuItem) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.geoInstructionsMenuItem)
                    .setIcon(R.drawable.ic_geo_city)
                    .setMessage(R.string.geoInstructionsMessage)
                    .setPositiveButton(R.string.ok,(click, arg) -> {})
                    .create()
                    .show();
        }
        else if (item.getItemId() ==  R.id.aboutApiMenuItem) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(LINK_TO_GEODATASOURCE)));
        }
        else if (item.getItemId() ==  R.id.donateToProjectMenuItem) {

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
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
                    .setPositiveButton(getResources().getString(R.string.geoThankYou),(click, arg) -> {})
                    .setNegativeButton(R.string.geoCancel, (click, arg) -> { })
                    .setView( container )
                    .create()
                    .show();
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.geo_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() ==  R.id.soccerMenuItem) {
            Intent intent = new Intent(GeoDataSource.this, SoccerMatchHighlights.class);
            startActivity(intent);
        }
        else if (item.getItemId() ==  R.id.lyricsMenuItem) {
            Intent intent = new Intent(GeoDataSource.this, SongLyricsSearch.class);
            startActivity(intent);
        }
        else if (item.getItemId() ==  R.id.deezerMenuItem) {
            Intent intent = new Intent(GeoDataSource.this, DeezerSongSearch.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(GeoDataSource.this, R.string.geoMessageForAboutProjectMenuItem, Toast.LENGTH_LONG).show();
        }

        return true;
    }

    private class GeoDataRequest extends AsyncTask< String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String jsonString = null;

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (new URL(strings[0])).openConnection();
//                urlConnection.setReadTimeout(10000);
//                urlConnection.setConnectTimeout(15000);
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setDoInput(true);
                InputStream responseStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                jsonString = sb.toString();

                if (!(jsonString == null || jsonString.isEmpty())) {
                    int progress = 5;
                    while (progress < 90) {
                        publishProgress(progress += 5);
                        Thread.sleep(20);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            geoProgressBar.setVisibility(View.VISIBLE);
            geoProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null || result.isEmpty()) {
//                Snackbar.make(geoBtnSearchCities, R.string.geoSnackNoCitiesFound, Snackbar.LENGTH_LONG).show();
                Toast.makeText(GeoDataSource.this, R.string.geoMessageNoCitiesFound, Toast.LENGTH_LONG).show();
                geoProgressBar.setProgress(MIN_PROGRESS);
                geoProgressBar.setVisibility(View.INVISIBLE);
                return;
            }

            geoProgressBar.setProgress(MAX_PROGRESS);

            Intent cityListIntent = new Intent(GeoDataSource.this, GeoCityList.class);
            cityListIntent.putExtra(JSON_INTENT_DATA, result);
            startActivityForResult(cityListIntent, REQUEST_CITY_DISPLAY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CITY_DISPLAY && resultCode == GeoCityList.RESULT_NO_CITY_IN_JSON) {
            Toast.makeText(GeoDataSource.this, R.string.geoMessageNoCitiesFound, Toast.LENGTH_LONG).show();
            Snackbar.make(geoBtnSearchCities, R.string.geoMessageNoCitiesInJson, Snackbar.LENGTH_LONG).show();
        }
    }

    private void saveSharedPrefs(String key, String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, stringToSave);
        editor.commit();
    }

    public class GeoAttr {
        String string = null;
        int resID;
        Boolean isReal;
        int logColumnWidth;

        GeoAttr(String string, int resID, Boolean isReal, int logColumnWidth) {
            this.string = string;
            this.resID = resID;
            this.isReal = isReal;
            this.logColumnWidth = logColumnWidth;
        }
    }
}
