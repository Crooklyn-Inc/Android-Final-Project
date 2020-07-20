package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DeezerSongDBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "DeezerSongs";
    static final String DB_TABLE = "Songs";

    //columns
    static final String COL_TITLE = "Title";
    static final String COL_DURATION = "Duration";
    static final String COL_SONG_ID = "_id";
    static final String COL_ALBUM_NAME = "Album_Name";
    boolean isSaved;

    static final int VERSION_NUM = 1;
    //queries
    private static final String CREATE_TABLE = "CREATE TABLE "+DB_TABLE+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_TITLE +" TEXT,"+COL_ALBUM_NAME  +" TEXT, "+COL_DURATION+" INTEGER);";

    public DeezerSongDBHelper(Context context) {
        super(context,DB_NAME, null, VERSION_NUM );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    //insert data
    public boolean insertData(String title, int duration, String album_name) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeezerSongDBHelper.COL_TITLE, title);
        if (isSaved)
            contentValues.put(DeezerSongDBHelper.COL_DURATION, duration);
            contentValues.put(DeezerSongDBHelper.COL_ALBUM_NAME, album_name);

        long result = db.insert(DeezerSongDBHelper.DB_TABLE, null, contentValues);

//

        return result != -1; //if result = -1 data doesn't insert
    }

    // (MessageModel selectedMessage) / (int position)
    protected void deleteSong(DeezerSongModel selectedSong)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //position += 1;

        db.delete(DeezerSongDBHelper.DB_TABLE, DeezerSongDBHelper.COL_SONG_ID + "= ?", new String[] {Long.toString(selectedSong.getId())});
    }


    //view data
    public Cursor viewDataDb(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from "+DB_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        printCursor(cursor);
        return cursor;
    }

    public void printCursor(Cursor cursor){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.v("Database Version:", Integer.toString(db.getVersion()));
        Log.v("Number of columns: ", Integer.toString(cursor.getColumnCount()));
        for (int i = 0; i < cursor.getColumnCount(); i++){
            Log.v("Column "+(i+1)+": ", cursor.getColumnName(i));
        }
        Log.v("Number of rows:", Integer.toString(cursor.getCount()));
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));



    }
}

