package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GeoCityList extends AppCompatActivity {

    static final int RESULT_NO_CITY_IN_JSON = 200;
    private ArrayList<ArrayList<Object>> cityWebDataArray = new ArrayList<ArrayList<Object>>();
    ListView geoListViewCities;
    GeoListViewAdapter geoListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_city_list);

        geoListViewCities = findViewById(R.id.geoListViewCities);

        clearArrList(cityWebDataArray);
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");

        try {
            JSONObject jObject;
            ArrayList<Object> singleCityArray;
            JSONArray jArray = new JSONArray(json);

            for(int i = 0; i < jArray.length(); i++) {
                try {
                    jObject = jArray.getJSONObject(i);
                    singleCityArray = new ArrayList<>(GeoDataSource.ATTR_MAP.size());

                    singleCityArray.add(new Long(-1L));
                    for (int k = 1; k < GeoDataSource.ATTR_MAP.size(); k++) {
                        singleCityArray.add(GeoDataSource.ATTR_MAP.get(k).isReal ? jObject.getDouble(GeoDataSource.ATTR_MAP.get(k).string) : jObject.getString(GeoDataSource.ATTR_MAP.get(k).string));
                    }

                    cityWebDataArray.add(singleCityArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (cityWebDataArray == null || cityWebDataArray.isEmpty()) {
            setResult(RESULT_NO_CITY_IN_JSON);
            finish();
            return;
        }

        geoListViewCities.setAdapter(geoListViewAdapter = new GeoListViewAdapter());
        Toast.makeText(GeoCityList.this, cityWebDataArray.size() + " " + getString(R.string.geoMessageNumberOfCitiesFound), Toast.LENGTH_LONG).show();
        Snackbar.make(geoListViewCities, cityWebDataArray.size() + " " + getString(R.string.geoMessageNumberOfCitiesFound), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearArrList(cityWebDataArray);
        cityWebDataArray = null;
    }

    private void clearArrList(ArrayList<ArrayList<Object>> arrListOfArrLists) {
        if (!arrListOfArrLists.isEmpty()) {
            for(ArrayList<Object> array: arrListOfArrLists) {
                for(Object obj: array) obj = null;
                array.clear();
            }
            arrListOfArrLists.clear();
        }
    }

    class GeoListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cityWebDataArray.size();
        }

        @Override
        public Object getItem(int position) {
            return (String) cityWebDataArray.get(position).get(1);
        }

        @Override
        public long getItemId(int position) {
            return (Long) cityWebDataArray.get(position).get(0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView;
            LayoutInflater inflater = getLayoutInflater();

            if (convertView == null) {
                newView = inflater.inflate(R.layout.geo_city_row, parent, false);
            }
            else newView = convertView;

            TextView geoTxtViewCityRow = newView.findViewById(R.id.geoTxtViewCityRow);
            geoTxtViewCityRow.setText((String) getItem(position));
            TextView geoTxtViewCityDistRow = newView.findViewById(R.id.geoTxtViewCityDistRow);
            geoTxtViewCityDistRow.setText(String.format("%.1f km", Math.round((Double) cityWebDataArray.get(position).get(GeoDataSource.ATTR_MAP.size() - 1) * 10d) / 10d));

            return newView;
        }
    }
}