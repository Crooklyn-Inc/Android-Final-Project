package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class GeoCityInfo extends AppCompatActivity implements GeoCityDetailsFragment.OnCityStatusChangeListener {

    private SQLiteDatabase sqlLiteDb = null;
    private Bundle incomingBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_city_info);

        incomingBundle = getIntent().getExtras();

        GeoCityDetailsFragment gcdFragment = new GeoCityDetailsFragment();
        gcdFragment.setArguments(incomingBundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                .commit(); // actually load the fragment. Calls onCreate() in DetailFragment
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqlLiteDb != null) sqlLiteDb.close();
    }

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
        }
        else {
            incomingBundle.putLong(GeoDataSource.ATTR_MAP.get(0).string, addCityToFavourites(incomingBundle));
            gcdFragment.setArguments(incomingBundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.geoFrameLayout, gcdFragment) // Add the fragment in FrameLayout
                    .commit(); // actually load the fragment. Calls onCreate() in DetailFragment
        }
    }

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

    void removeCityFromFavourites(Long id) {
        if (sqlLiteDb == null) return;
        sqlLiteDb.delete(GeoDBOpener.TABLE_NAME, GeoDBOpener.COL_ID + " = ?", new String[] {id.toString()});
        printCursor();
    }

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
        sb.append(String.format("%" + GeoDataSource.ATTR_MAP.get(0).logColumnWidth + "s", c.getColumnNames()[0]));
        for(int k = 1; k < numOfCols; k++) {
            sb.append(" | " + String.format("%" + GeoDataSource.ATTR_MAP.get(k).logColumnWidth + "s", c.getColumnNames()[k]));
        }

        Log.i("Column names", sb.toString());

        c.moveToFirst();
        for(int i = 0; i < numOfRows; i++) {
            sb = new StringBuilder();
            sb.append(String.format("%" + GeoDataSource.ATTR_MAP.get(0).logColumnWidth + "s", c.getString(0)));
            for(int k = 1; k < numOfCols; k++) {
                sb.append(" | " + String.format("%" + GeoDataSource.ATTR_MAP.get(k).logColumnWidth + "s", c.getString(k)));
            }

            Log.i(String.format("Row # %6d", i), sb.toString());

            c.moveToNext();
        }
    }
}