package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeoDBOpener extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "GeoDataSourceDB";
    protected static final int VERSION_NUM = 1;
    static final String TABLE_NAME = "CITIES";
    static final String COL_ID = GeoDataSource.ATTR_MAP.get(0).string;

    public GeoDBOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT");

        for (int i = 1; i < GeoDataSource.ATTR_MAP.size() - 1; i++) {
            sb.append(", " + GeoDataSource.ATTR_MAP.get(i).string.toUpperCase() + (GeoDataSource.ATTR_MAP.get(i).isReal ? " REAL" : " TEXT"));
        }
        sb.append(");");

        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
