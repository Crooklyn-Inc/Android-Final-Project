package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.google.android.material.snackbar.Snackbar;

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

public class GeoDataSource extends AppCompatActivity {

    private static final String FORMATTED_REQUEST = "https://api.geodatasource.com/cities?key=%S&format=json&lat=%S&lng=%S&zoom=14";
    private static final String API_KEY = "YR5XMSKGHMHTIWDKTLRADSUYNYTJNYNK";
    public static final List<GeoAttr> ATTR_MAP = new ArrayList<>(13);
    public static final String ACTIVITY_NAME = "GEO_DATA_SOURCE";

    private static final int MIN_PROGRESS = 0;
    private static final int MAX_PROGRESS = 100;
    static final int REQUEST_CITY_DISPLAY = 100;

    private EditText geoEditTxtLat;
    private EditText geoEditTxtLon;
    private Button geoBtnSearchCities;
    private Button geoBtnFavourites;
    private ProgressBar geoProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_data_source);

        geoEditTxtLat = (EditText) findViewById(R.id.geoEditTxtLat);
        geoEditTxtLon = (EditText) findViewById(R.id.geoEditTxtLon);
        geoBtnSearchCities = (Button) findViewById(R.id.geoBtnSearchCities);
        geoBtnFavourites = (Button) findViewById(R.id.geoBtnFavourites);
        geoProgressBar = (ProgressBar) findViewById(R.id.geoProgressBar);
        geoProgressBar.setVisibility(View.INVISIBLE);
        geoProgressBar.setMax(MAX_PROGRESS);

        if (ATTR_MAP.isEmpty()) {
            ATTR_MAP.add(new GeoAttr("_id", R.string.geoCityAttr_0, false));                //  "Database Record ID"
            ATTR_MAP.add(new GeoAttr("city", R.string.geoCityAttr_1, false));               //  "City"
            ATTR_MAP.add(new GeoAttr("country", R.string.geoCityAttr_2, false));            //  "Country"
            ATTR_MAP.add(new GeoAttr("region", R.string.geoCityAttr_3, false));             //  "Region"
            ATTR_MAP.add(new GeoAttr("latitude", R.string.geoCityAttr_4, true));            //  "Latitude"
            ATTR_MAP.add(new GeoAttr("longitude", R.string.geoCityAttr_5, true));           //  "Longitude"
            ATTR_MAP.add(new GeoAttr("currency_code", R.string.geoCityAttr_6, false));      //  "Currency Code"
            ATTR_MAP.add(new GeoAttr("currency_name", R.string.geoCityAttr_7, false));      //  "Currency Name"
            ATTR_MAP.add(new GeoAttr("currency_symbol", R.string.geoCityAttr_8, false));    //  "Currency Symbol"
            ATTR_MAP.add(new GeoAttr("sunrise", R.string.geoCityAttr_9, false));            //  "Sunrise"
            ATTR_MAP.add(new GeoAttr("sunset", R.string.geoCityAttr_10, false));            //  "Sunset"
            ATTR_MAP.add(new GeoAttr("time_zone", R.string.geoCityAttr_11, false));         //  "Time Zone"
            ATTR_MAP.add(new GeoAttr("distance_km", R.string.geoCityAttr_12, true));        //  "Distance km"
        }

        geoBtnSearchCities.setOnClickListener( e -> {
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
                    while (progress < 95) {
                        publishProgress(progress += 5);
                        Thread.sleep(50);
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
            cityListIntent.putExtra("json", result);
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

    public class GeoAttr {
        String string = null;
        int resID;
        Boolean isReal;

        GeoAttr(String string, int resID, Boolean isReal) {
            this.string = string;
            this.resID = resID;
            this.isReal = isReal;
        }
    }
}