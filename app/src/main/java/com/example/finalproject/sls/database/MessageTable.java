/**
 * This is the copyrighted content for course
 * of mobile programming at Algonquin College
 *
 * @author Olga Zimina
 * @version 1.0.0
 * @created Jul 25, 2020
 */

package com.example.finalproject.sls.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * SQL helper to manage the DB
 */
public class MessageTable {
    public static final String TABLE_NAME = "message";
    public static final String ID         = "_id";
    public static final String BAND       = "band";
    public static final String SONG       = "song";
    public static final String LYRICS     = "lyrics";

    private static final String CREATE_TABLE = "create table "
                                               + TABLE_NAME
                                               + "("
                                               + ID + " integer primary key autoincrement, "
                                               + BAND + " text not null, "
                                               + SONG + " text not null, "
                                               + LYRICS + " text not null);";


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param database The database.
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * @param database   The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(MessageTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
