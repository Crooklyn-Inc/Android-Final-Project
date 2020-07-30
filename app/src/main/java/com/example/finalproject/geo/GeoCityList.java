package com.example.finalproject.geo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.deezer.DeezerSongSearch;
import com.example.finalproject.sls.SongLyricsSearch;
import com.example.finalproject.soccerMatch.SoccerMatchHighlights;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GeoCityList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GeoCityDetailsFragment.OnCityStatusChangeListener {

    static final int RESULT_NO_CITY_IN_JSON = 50;
    static final int CITY_DETAILED_VIEW_REQUEST = 150;
    static final String CITY_LIST_POSITION = "city_listed_position";

    private SQLiteDatabase sqlLiteDb = null;
    private ArrayList<ArrayList<Object>> cityWebDataArray = new ArrayList<ArrayList<Object>>();
    private ArrayList<ArrayList<Object>> favouriteCityArray = new ArrayList<ArrayList<Object>>();
    private ListView geoListViewCities;
    private GeoListViewAdapter geoListViewAdapter;
    private boolean isTablet;
    private View selectedListView;
    private int selectedListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_city_list);

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

        geoListViewCities = findViewById(R.id.geoListViewCities);
        isTablet = findViewById(R.id.geoFrameLayout) != null;
        selectedListView = null;
        selectedListPosition = -1;

        clearArrList(cityWebDataArray);
        Intent intent = getIntent();
        String json = intent.getStringExtra(GeoDataSource.JSON_INTENT_DATA);

        loadDataFromDatabase();

        if (json == null) {
            cityWebDataArray.addAll(favouriteCityArray);
            favouriteCityArray.clear();
            favouriteCityArray = null;
        }
        else {
            try {
                JSONObject jObject;
                ArrayList<Object> singleCityArray;
                JSONArray jArray = new JSONArray(json);

                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        jObject = jArray.getJSONObject(i);
                        singleCityArray = new ArrayList<>(GeoDataSource.ATTR_MAP.size());

                        singleCityArray.add(new Long(-i));
                        for (int k = 1; k < GeoDataSource.ATTR_MAP.size(); k++) {
                            singleCityArray.add(GeoDataSource.ATTR_MAP.get(k).isReal ? jObject.getDouble(GeoDataSource.ATTR_MAP.get(k).string) : jObject.getString(GeoDataSource.ATTR_MAP.get(k).string));
                        }

                        for (ArrayList<Object> favouriteCity: favouriteCityArray) {
                            if (areSameCities(singleCityArray, favouriteCity)) {
                                singleCityArray.set(0, favouriteCity.get(0));
                                break;
                            }
                        }
                        cityWebDataArray.add(singleCityArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                clearArrList(favouriteCityArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (cityWebDataArray == null || cityWebDataArray.isEmpty()) {
                setResult(RESULT_NO_CITY_IN_JSON);
                finish();
                return;
            }
        }

        geoListViewCities.setAdapter(geoListViewAdapter = new GeoListViewAdapter());
        geoListViewCities.smoothScrollToPosition(0);

        geoListViewCities.setOnItemClickListener((parent, view, position, id) -> {
            selectedListPosition = position;
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putLong(GeoDataSource.ATTR_MAP.get(0).string, id);
            for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
                if(GeoDataSource.ATTR_MAP.get(i).isReal) {
                    dataToPass.putDouble(GeoDataSource.ATTR_MAP.get(i).string, (Double)cityWebDataArray.get(position).get(i));
                }
                else {
                    dataToPass.putString(GeoDataSource.ATTR_MAP.get(i).string, (String)cityWebDataArray.get(position).get(i));
                }
            }

            if(isTablet) {
                if (selectedListView != null)
                    selectedListView.setBackgroundColor(getColor(android.R.color.transparent));
                view.setBackgroundColor(getColor(R.color.colorGeoBackground));
                selectedListView = view;

                GeoCityDetailsFragment gcdFragment = new GeoCityDetailsFragment();
                gcdFragment.setArguments(dataToPass);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else {
                selectedListView = view;
                Intent nextActivity = new Intent(GeoCityList.this, GeoCityInfo.class);
                dataToPass.putLong(CITY_LIST_POSITION, (new Long(position)).longValue());
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, CITY_DETAILED_VIEW_REQUEST); //make the transition
            }
        });

        Toast.makeText(GeoCityList.this, cityWebDataArray.size() + " " + (json == null ? getString(R.string.geoMessageNumberOfFavouriteCities) : getString(R.string.geoMessageNumberOfFoundCities)), Toast.LENGTH_LONG).show();
        Snackbar.make(geoListViewCities, cityWebDataArray.size() + " " + (json == null ? getString(R.string.geoMessageNumberOfFavouriteCities) : getString(R.string.geoMessageNumberOfFoundCities)), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (sqlLiteDb != null) sqlLiteDb.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CITY_DETAILED_VIEW_REQUEST) {
            Bundle returningBundle = data.getExtras();
            ArrayList<Object> incomingAttributes = new ArrayList<>(GeoDataSource.ATTR_MAP.size());

            if (returningBundle == null) return;

            incomingAttributes.add(returningBundle.getLong(GeoDataSource.ATTR_MAP.get(0).string));
            for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
                if (GeoDataSource.ATTR_MAP.get(i).isReal) {
                    incomingAttributes.add(returningBundle.getDouble(GeoDataSource.ATTR_MAP.get(i).string));
                }
                else {
                    incomingAttributes.add(returningBundle.getString(GeoDataSource.ATTR_MAP.get(i).string));
                }
            }

            Long cityIndex = returningBundle.getLong(CITY_LIST_POSITION);

            if (cityIndex.intValue() == selectedListPosition) {
                if (!((Long)cityWebDataArray.get(selectedListPosition).get(0)).equals((Long)incomingAttributes.get(0))) {
                    if ((Long)cityWebDataArray.get(selectedListPosition).get(0) <= 0L && (Long)incomingAttributes.get(0) > 0L)
                        Toast.makeText(GeoCityList.this, (String)cityWebDataArray.get(selectedListPosition).get(1) + " " + getString(R.string.geoMessageCityAddedToFavourites), Toast.LENGTH_LONG).show();
                    else if ((Long)cityWebDataArray.get(selectedListPosition).get(0) > 0L && (Long)incomingAttributes.get(0) <= 0L)
                        Toast.makeText(GeoCityList.this, (String)cityWebDataArray.get(selectedListPosition).get(1) + " " + getString(R.string.geoMessageCityRemovedFromFavourites), Toast.LENGTH_LONG).show();

                    cityWebDataArray.get(selectedListPosition).set(0, incomingAttributes.get(0));
                    ImageView geoImgViewFavouriteSign = selectedListView.findViewById(R.id.geoImgViewFavouriteSign);
                    geoImgViewFavouriteSign.setImageResource((Long) incomingAttributes.get(0) > 0L ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
                }
            }
            else {
                Snackbar.make(geoListViewCities, R.string.geoMessageWrongCityListedPosition, Snackbar.LENGTH_LONG).show();
//                Toast.makeText(GeoCityList.this, R.string.geoMessageWrongCityListedPosition, Toast.LENGTH_LONG).show();
            }

//            if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(GeoCityList.this, R.string.geoMessageUserPressedBackButton, Toast.LENGTH_LONG).show();
//            }
//            else if (resultCode == GeoCityInfo.UNCHANGED) {
//                Toast.makeText(GeoCityList.this, R.string.geoMessageUserDidNotChangeCityStatus, Toast.LENGTH_LONG).show();
//            }
//            else if (resultCode == GeoCityInfo.FAVOURABLE) {
//                Toast.makeText(GeoCityList.this, R.string.geoMessageUserSavedCityToFavourites, Toast.LENGTH_LONG).show();
//            }
//            else if (resultCode == GeoCityInfo.UNFAVOURABLE) {
//                Toast.makeText(GeoCityList.this, R.string.geoMessageUserRemovedCityFromFavourites, Toast.LENGTH_LONG).show();
//            }
        }
    }

    private void clearArrList(ArrayList<ArrayList<Object>> arrListOfArrLists) {
        if (!arrListOfArrLists.isEmpty()) {
            for(ArrayList<Object> array: arrListOfArrLists) {
                if (array != null) {
                    for (Object obj : array) obj = null;
                    array.clear();
                }
            }
            arrListOfArrLists.clear();
        }
    }

    @Override
    public void onCityStatusChange(Long index) {
        if (index <= 0L) {
            cityWebDataArray.get(-index.intValue()).set(0, addCityToFavourites(-index.intValue()));
            ImageView geoImgViewFavouriteSign = selectedListView.findViewById(R.id.geoImgViewFavouriteSign);
            geoImgViewFavouriteSign.setImageResource(android.R.drawable.btn_star_big_on);
            geoListViewCities.performItemClick(selectedListView, -index.intValue(), geoListViewCities.getItemIdAtPosition(-index.intValue()));
            Toast.makeText(GeoCityList.this, cityWebDataArray.get(-index.intValue()).get(1) + " " + getString(R.string.geoMessageCityAddedToFavourites), Toast.LENGTH_LONG).show();
        }
        else {
            ArrayList<Object> tempArray;
            for (int i = 0; i < cityWebDataArray.size() ; i++) {
                tempArray = cityWebDataArray.get(i);
                if (((Long)tempArray.get(0)).equals(index)) {
                    removeCityFromFavourites(index);
                    tempArray.set(0, new Long(-i));
                    ImageView geoImgViewFavouriteSign = selectedListView.findViewById(R.id.geoImgViewFavouriteSign);
                    geoImgViewFavouriteSign.setImageResource(android.R.drawable.btn_star_big_off);
                    geoListViewCities.performItemClick(selectedListView, i, geoListViewCities.getItemIdAtPosition(i));
                    Toast.makeText(GeoCityList.this, cityWebDataArray.get(i).get(1) + " " + getString(R.string.geoMessageCityRemovedFromFavourites), Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
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
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GeoDataSource.LINK_TO_GEODATASOURCE)));
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
                    .setIcon(R.drawable.ic_geo_donate)
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
            Intent intent = new Intent(GeoCityList.this, SoccerMatchHighlights.class);
            startActivity(intent);
        }
        else if (item.getItemId() ==  R.id.lyricsMenuItem) {
            Intent intent = new Intent(GeoCityList.this, SongLyricsSearch.class);
            startActivity(intent);
        }
        else if (item.getItemId() ==  R.id.deezerMenuItem) {
            Intent intent = new Intent(GeoCityList.this, DeezerSongSearch.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(GeoCityList.this, R.string.geoMessageForAboutProjectMenuItem, Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.geoAboutProjectMenuItem)
                    .setIcon(R.drawable.ic_geo_city)
                    .setMessage(R.string.geoMessageForAboutProjectMenuItem)
                    .setPositiveButton(R.string.ok,(click, arg) -> {})
                    .create()
                    .show();
        }

        return true;
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
            else {
                newView = convertView;
                if (isTablet) newView.setBackgroundColor(getColor(android.R.color.transparent));
            }

            if (selectedListPosition == position && isTablet) newView.setBackgroundColor(getColor(R.color.colorGeoBackground));

            ImageView geoImgViewFavouriteSign = newView.findViewById(R.id.geoImgViewFavouriteSign);
            geoImgViewFavouriteSign.setImageResource((Long)cityWebDataArray.get(position).get(0) > 0 ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
            TextView geoTxtViewCityRow = newView.findViewById(R.id.geoTxtViewCityRow);
            geoTxtViewCityRow.setText((String) getItem(position));
            TextView geoTxtViewCityDistRow = newView.findViewById(R.id.geoTxtViewCityDistRow);
            Double distance = (Double) cityWebDataArray.get(position).get(GeoDataSource.ATTR_MAP.size() - 1);
            geoTxtViewCityDistRow.setText(distance == null ? "" : String.format("%.1f km", Math.round(distance * 10d) / 10d));

            return newView;
        }
    }

    private void loadDataFromDatabase() {
        GeoDBOpener dbOpener = new GeoDBOpener(this);
        clearArrList(favouriteCityArray);
        sqlLiteDb = dbOpener.getWritableDatabase();

        String[] columns = new String[GeoDataSource.ATTR_MAP.size() - 1];
        for (int i = 0; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
            columns[i] = GeoDataSource.ATTR_MAP.get(i).string.toUpperCase();
        }
        Cursor cursor = sqlLiteDb.query(false, GeoDBOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int[] columnMap = new int[GeoDataSource.ATTR_MAP.size() - 1];
        cursor.moveToFirst();
        columnMap[0] = cursor.getColumnIndex(GeoDataSource.ATTR_MAP.get(0).string);
        for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
            columnMap[i] = cursor.getColumnIndex(GeoDataSource.ATTR_MAP.get(i).string.toUpperCase());
        }
        printCursor(cursor);

        ArrayList<Object> singleCityArray;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            singleCityArray = new ArrayList<>(GeoDataSource.ATTR_MAP.size());
            singleCityArray.add(cursor.getLong(columnMap[0]));
            for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
                if (GeoDataSource.ATTR_MAP.get(i).isReal) {
                    singleCityArray.add(cursor.getDouble(columnMap[i]));
                }
                else {
                    singleCityArray.add(cursor.getString(columnMap[i]));
                }
            }
            singleCityArray.add(null);
            favouriteCityArray.add(singleCityArray);
            cursor.moveToNext();
        }
    }

    long addCityToFavourites(int index) {
        ContentValues contentValues = new ContentValues();
        for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
            if (GeoDataSource.ATTR_MAP.get(i).isReal) {
                contentValues.put(GeoDataSource.ATTR_MAP.get(i).string.toUpperCase(), (Double)cityWebDataArray.get(index).get(i));
            }
            else {
                contentValues.put(GeoDataSource.ATTR_MAP.get(i).string.toUpperCase(), (String)cityWebDataArray.get(index).get(i));
            }
        }
        long newRecordId = sqlLiteDb.insert(GeoDBOpener.TABLE_NAME, null, contentValues);
        printCursor();
        return newRecordId;
    }

    void removeCityFromFavourites(Long id) {
        sqlLiteDb.delete(GeoDBOpener.TABLE_NAME, GeoDBOpener.COL_ID + " = ?", new String[] {id.toString()});
        printCursor();
    }

    private boolean areSameCities(ArrayList<Object> city1, ArrayList<Object> city2) {
        for (int i = 1; i < 6; i++) {
            if (GeoDataSource.ATTR_MAP.get(i).isReal) {
                if (!((Double)city1.get(i)).equals((Double)city2.get(i))) return false;
            }
            else {
                if (!((String)city1.get(i)).equals((String)city2.get(i))) return false;
            }
        }
        return true;
    }

    private void printCursor() {
        if (sqlLiteDb != null) {
            String[] columns = new String[GeoDataSource.ATTR_MAP.size() - 1];
            columns[0] = GeoDataSource.ATTR_MAP.get(0).string;
            for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
                columns[i] = GeoDataSource.ATTR_MAP.get(i).string.toUpperCase();
            }
            printCursor(sqlLiteDb.query(false, GeoDBOpener.TABLE_NAME, columns, null, null, null, null, null, null));
        }
    }

    private void printCursor(Cursor c) {
        int numOfCols = c.getColumnCount();
        int numOfRows = c.getCount();
        StringBuilder sb;

        Log.i("Database version number", String.valueOf(sqlLiteDb.getVersion()));
        Log.i("Number of columns", String.format("%7d", numOfCols));
        Log.i("Number of rows", String.format("%10d", numOfRows));

        sb = new StringBuilder();
        sb.append(String.format("%" + (GeoDataSource.ATTR_MAP.get(0).logColumnWidth + 3) + "s", c.getColumnNames()[0]));
        for(int k = 1; k < numOfCols; k++) {
            sb.append(" | " + String.format("%" + GeoDataSource.ATTR_MAP.get(k).logColumnWidth + "s", c.getColumnNames()[k]));
        }

        Log.i("Columns", sb.toString());

        c.moveToFirst();
        for(int i = 0; i < numOfRows; i++) {
            sb = new StringBuilder();
            sb.append(String.format("%" + GeoDataSource.ATTR_MAP.get(0).logColumnWidth + "s", c.getString(0)));
            for(int k = 1; k < numOfCols; k++) {
                sb.append(" | " + String.format("%" + GeoDataSource.ATTR_MAP.get(k).logColumnWidth + "s", c.getString(k)));
            }

            Log.i(String.format("Row # %4d", i + 1), sb.toString());

            c.moveToNext();
        }
    }
}