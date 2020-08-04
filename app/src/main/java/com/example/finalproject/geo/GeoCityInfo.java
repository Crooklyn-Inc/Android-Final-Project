package com.example.finalproject.geo;

import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.deezer.DeezerSongSearch;
import com.example.finalproject.sls.SongLyricsSearch;
import com.example.finalproject.soccerMatch.SoccerMatchHighlights;
import com.google.android.material.navigation.NavigationView;

/**
 * Class implementing activity for visualisation on the phone of the selected city details as well as adding and removing of that city from the database.
 */
public class GeoCityInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GeoCityDetailsFragment.OnCityStatusChangeListener {

    static final int UNCHANGED = 100;
    static final int FAVOURABLE = 200;
    static final int UNFAVOURABLE = 300;
    private SQLiteDatabase sqlLiteDb = null;
    private Intent resultIntent;
    private Bundle incomingBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_city_info);

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

        Intent intent = getIntent();
        incomingBundle = intent.getExtras();

        GeoCityDetailsFragment gcdFragment = new GeoCityDetailsFragment();
        gcdFragment.setArguments(incomingBundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                .commit(); // actually load the fragment. Calls onCreate() in DetailFragment

        resultIntent = new Intent();
        resultIntent.putExtras(incomingBundle);
        setResult(UNCHANGED, resultIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqlLiteDb != null) sqlLiteDb.close();
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
            Intent intent = new Intent(GeoCityInfo.this, SoccerMatchHighlights.class);
            startActivity(intent);
        }
        else if (item.getItemId() ==  R.id.lyricsMenuItem) {
            Intent intent = new Intent(GeoCityInfo.this, SongLyricsSearch.class);
            startActivity(intent);
        }
        else if (item.getItemId() ==  R.id.deezerMenuItem) {
            Intent intent = new Intent(GeoCityInfo.this, DeezerSongSearch.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(GeoCityInfo.this, R.string.geoMessageForAboutProjectMenuItem, Toast.LENGTH_LONG).show();
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

    /**
     * callback method listening for a click from 'Add to Favourites' and 'Remove from Favourites' buttons.
     * @param index index of the city item currently displayed in the activity.
     */
    @Override
    public void onCityStatusChange(Long index) {
        GeoCityDetailsFragment gcdFragment = new GeoCityDetailsFragment();
        if (sqlLiteDb == null) sqlLiteDb = (new GeoDBOpener(this)).getWritableDatabase();

        if (index > 0L) {
            removeCityFromFavourites(index);
            incomingBundle.putLong(GeoDataSource.ATTR_MAP.get(0).string, -incomingBundle.getLong(GeoCityList.CITY_LIST_POSITION));
            gcdFragment.setArguments(incomingBundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                    .commit(); // actually load the fragment. Calls onCreate() in DetailFragment
            resultIntent.putExtras(incomingBundle);
            setResult(UNFAVOURABLE, resultIntent);
        }
        else {
            incomingBundle.putLong(GeoDataSource.ATTR_MAP.get(0).string, addCityToFavourites(incomingBundle));
            gcdFragment.setArguments(incomingBundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                    .commit(); // actually load the fragment. Calls onCreate() in DetailFragment
            resultIntent.putExtras(incomingBundle);
            setResult(FAVOURABLE, resultIntent);
        }
    }

    /**
     * method implementing insertion of the currently selected city into the database of favourite cities.
     * @param bundle city attributes to be stored in the database.
     * @return database ID of the inserted city record.
     */
    long addCityToFavourites(Bundle bundle) {
        ContentValues contentValues = new ContentValues();
        for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
            if (GeoDataSource.ATTR_MAP.get(i).isReal) {
                contentValues.put(GeoDataSource.ATTR_MAP.get(i).string.toUpperCase(), bundle.getDouble(GeoDataSource.ATTR_MAP.get(i).string));
            }
            else {
                contentValues.put(GeoDataSource.ATTR_MAP.get(i).string.toUpperCase(), bundle.getString(GeoDataSource.ATTR_MAP.get(i).string));
            }
        }
        long newRecordId = sqlLiteDb.insert(GeoDBOpener.TABLE_NAME, null, contentValues);
        printCursor();
        return newRecordId;
    }

    /**
     * method implementing removal of the currently selected city from the database of favourite cities.
     * @param id database ID of the record to be removed from the database.
     */
    void removeCityFromFavourites(Long id) {
        if (sqlLiteDb == null) return;
        sqlLiteDb.delete(GeoDBOpener.TABLE_NAME, GeoDBOpener.COL_ID + " = ?", new String[] {id.toString()});
        printCursor();
    }

    /**
     * method printing the database content into the console.
     */
    private void printCursor() {
        if (sqlLiteDb == null) return;

        String[] columns = new String[GeoDataSource.ATTR_MAP.size() - 1];
        columns[0] = GeoDataSource.ATTR_MAP.get(0).string;
        for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
            columns[i] = GeoDataSource.ATTR_MAP.get(i).string.toUpperCase();
        }
        Cursor c = sqlLiteDb.query(false, GeoDBOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int numOfCols = c.getColumnCount();
        int numOfRows = c.getCount();
        StringBuilder sb;

        Log.i("Database version number", String.valueOf(sqlLiteDb.getVersion()));
        Log.i("Number of columns", String.format("%7d", numOfCols));
        Log.i("Number of rows", String.format("%10d", numOfRows));

        sb = new StringBuilder();
        sb.append(String.format("%" + (GeoDataSource.ATTR_MAP.get(0).logColumnWidth +3) + "s", c.getColumnNames()[0]));
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