package com.example.finalproject.sls.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME    = "sls.db";
    private static final int    DATABASE_VERSION = 1;

    public MessageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MessageTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MessageTable.onUpgrade(db, oldVersion, newVersion);
    }
}
