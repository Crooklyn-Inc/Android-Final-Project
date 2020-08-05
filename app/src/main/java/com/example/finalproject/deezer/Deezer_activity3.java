package com.example.finalproject.deezer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.example.finalproject.R;

import org.json.JSONArray;

import java.util.ArrayList;
/*
 * @author Yulia Tsvetkova
 * @version 1
 * @ August 4, 2020
 */

public class Deezer_activity3 extends AppCompatActivity {
    ArrayList<DeezerSongModel> arrayListDb = new ArrayList();
    SQLiteDatabase db;
    JSONArray dataArraySongs = null;
    public static String ARTIST_DETAILS = "ARTIST_DETAILS";
    public static String TITLE = "TITLE";
    public static String ALBUM_NAME = "ALBUM_NAME";
    public static String DURATION = "DURATION";
    public static String ALBUM_IMAGE = "ALBUM_IMAGE";


    DeezerSongDBHelper dbOpener = new DeezerSongDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_3);
        ListView listViewDbFav = findViewById(R.id.ListViewDB);

        MyAdapter adapter = new MyAdapter(Deezer_activity3.this, arrayListDb);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null;
        /**
         Getting data (song information) from DeezerActivity2 Class
          * */

        listViewDbFav.setOnItemClickListener((list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(TITLE, arrayListDb.get(position).getTitle());
            dataToPass.putString(ALBUM_NAME, arrayListDb.get(position).getAlbum_name());
            dataToPass.putString(DURATION, arrayListDb.get(position).getDuration());
            dataToPass.putString(ALBUM_IMAGE, arrayListDb.get(position).getAlbum_Image());


            if (isTablet) {
                DeezerFragmentDetails dFragment = new DeezerFragmentDetails(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            } else //isPhone
            {
                Intent nextActivity = new Intent(Deezer_activity3.this, DeezerEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });
        /**
         * Long click on the row of the listview of the favorites songs prompts user to choose if song should be deleted from the list or not.
         * Load data from database to the listview by calling loadDataFromDatabase() method and adapter.
         */

        listViewDbFav.setOnItemLongClickListener((parent, view, position, id) -> {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.alertBuilderTitle2))
       .setMessage(getResources().getString(R.string.alertBuilderMsg1) + " " + position +1  + "\n" + getResources().getString(R.string.alertBuilderMsg2) + " " + id)

       .setPositiveButton(getResources().getString(R.string.yes), (click, arg) -> {
        DeezerSongModel selectedMessage = arrayListDb.get(position);
        dbOpener.deleteSong(selectedMessage);
        //dbOpener.deleteMessage(position);
        arrayListDb.remove(position);
        adapter.notifyDataSetChanged();
    })
     .setNegativeButton(getResources().getString(R.string.no), (click, arg) -> {
    });

        //Show the dialog
            alert.create().show();

            return true;
        });


        loadDataFromDatabase();
        listViewDbFav.setAdapter(adapter);
    }
    /**
     * Method to retrieve all rows from the SQLite database. Each row represents DeezerModel object and all will be added to the ArrayList.
     */
            private void loadDataFromDatabase (){
            //get a database connection:
            db = dbOpener.getWritableDatabase();

            String[] columns = {dbOpener.COL_SONG_ID,dbOpener.COL_TITLE, dbOpener.COL_ALBUM_NAME, dbOpener.COL_DURATION, dbOpener.COL_ALBUM_IMAGE};
            //query all the results from the database:
            Cursor results = db.query(false, dbOpener.DB_TABLE, columns, null, null, null, null, null, null);


            int titleColumnIndex = results.getColumnIndex(dbOpener.COL_TITLE);
            int albumNameColIndex = results.getColumnIndex(dbOpener.COL_ALBUM_NAME);
            int durationColIndex = results.getColumnIndex(dbOpener.COL_DURATION);
            int idIndex = results.getColumnIndex(dbOpener.COL_SONG_ID);
            int albumImageColIndex = results.getColumnIndex(dbOpener.COL_ALBUM_IMAGE);

            //iterate over the results, return true if there is a next item:
            while (results.moveToNext()) {
                String titleDB = results.getString(titleColumnIndex);
                String albumNameDB = results.getString(albumNameColIndex);
                long id = results.getLong(idIndex);
                String durationDB = results.getString(durationColIndex);
                String albumImageDB = results.getString(albumImageColIndex);


                //add the new song to the array list:
                arrayListDb.add(new DeezerSongModel(titleDB, durationDB, albumNameDB, id, albumImageDB));
            }


        }


}
