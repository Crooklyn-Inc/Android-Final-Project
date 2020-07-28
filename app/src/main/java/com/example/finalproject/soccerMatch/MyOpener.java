package com.example.finalproject.soccerMatch;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyOpener extends SQLiteOpenHelper {
    public static final String ACTIVITY_NAME = "SMH";

    protected static final String DATABASE_NAME = "Favourite_Matches";
    static final String DATABASE_TABLE = "FMT";

    protected final static int VERSION_NUM = 1;

    public final static String COL_TITLE = "Title";
    public final static String COL_ID = "_id";
    public final static String COL_THUMBNAIL = "Thumbnail";
    public final static String COL_DATE = "Date";
    public final static String COL_COMPETITION_NAME = "CompetitionName";
    public final static String COL_VIDEO_URL = "VideoUrl";
    public final static String COL_TEAM1 = "TeamOne";
    public final static String COL_TEAM2 = "TeamTwo";


    private static final String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE + " (" + COL_ID + " INTEGER PRIMARY KEY , "
            + COL_TITLE + " TEXT, "
            + COL_THUMBNAIL + " TEXT, "
            + COL_DATE + " TEXT, "
            + COL_COMPETITION_NAME + " TEXT, "
            + COL_VIDEO_URL + " TEXT, "
            + COL_TEAM1 + " TEXT, "
            + COL_TEAM2 + " TEXT);";

    public MyOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public boolean insertData(long id, String title, String thumbnail,
                              String date, String competitionName,
                              String videoURL, String team1, String team2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(MyOpener.COL_ID, id);
        contentValues.put(MyOpener.COL_TITLE, title);
        contentValues.put(MyOpener.COL_THUMBNAIL, thumbnail);
        contentValues.put(MyOpener.COL_DATE, date);
        contentValues.put(MyOpener.COL_COMPETITION_NAME, competitionName);
        contentValues.put(MyOpener.COL_VIDEO_URL, videoURL);
        contentValues.put(MyOpener.COL_TEAM1, team1);
        contentValues.put(MyOpener.COL_TEAM2, team2);

        long result = db.insert(MyOpener.DATABASE_TABLE, null, contentValues);

        return result != -1;

    }

    protected void deleteMessage(Match m) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MyOpener.DATABASE_TABLE, MyOpener.COL_ID + "= ?", new String[]{Long.toString(m.getId())});
    }

    public Cursor viewDataDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + DATABASE_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}
