/**
 * This is the copyrighted content for course
 * of mobile programming at Algonquin College
 *
 * @author Olga Zimina
 * @version 1.0.0
 * @created Jul 25, 2020
 */

package com.example.finalproject.sls;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;

/**
 * Format of each row in the Favorite list.
 */
public class SongLyricsSearchFavoriteRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sls_favorite_record);
    }
}
