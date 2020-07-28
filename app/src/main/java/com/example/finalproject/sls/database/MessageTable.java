package com.example.finalproject.sls.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTable {
    public static final String TABLE_NAME = "message";
    public static final String ID = "_id";
    public static final String TEXT = "text";
    public static final String IS_SENT = "sent";

    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME
            + "("
            + ID + " integer primary key autoincrement, "
            + TEXT + " text not null, "
            + IS_SENT + " string not null);";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(MessageTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
