package com.example.finalproject.sls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SongLyricsSearchResult extends AppCompatActivity {

    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_lyrics_search_result);

        SharedPreferences sharedPreferences = getSharedPreferences(SongLyricsSearch.PREFERENCES, Context.MODE_PRIVATE);

        TextView band = findViewById(R.id.slsGroupName);
        TextView song = findViewById(R.id.slsSongName);
        band.setText(sharedPreferences.getString(SongLyricsSearch.BAND, ""));
        song.setText(sharedPreferences.getString(SongLyricsSearch.SONG, ""));

        TextView lyrics = findViewById(R.id.slsLyricsName);

        SongLyricsSearchNetwork searchNetwork = new SongLyricsSearchNetwork();
        searchNetwork.execute(band, song);

        HttpURLConnection openConnection = getOpenConnection(param);


        String   text   = "Baby, sometimes I feel like dying, \ndriving while I'm closing my eyes. \nMoving in and out of hiding, \ntrying to catch some truth in my life. \nWatching your stars and your moonlight, \ncome tumbling down from the sky. \n\nTake it now. \n\n\n\nI'm gonna run to you, I'm gonna come to you, \n\nI wanna find you in everything that I do. \n\nI'm gonna run to you, I'm gonna count on you, \n\nI'm gonna follow. Baby, what else can I do? \n\n\n\nSunday morning, my town is sleeping. \n\nLying all alone in my bed, \n\nthere's not a sound, I can't help but listening. \n\nWishing I was somewhere else instead. \n\nBut sometimes they're too hard to handle, \n\nthese voices inside my head. \n\nListen now. \n\n\n\nI'm gonna run to you, I'm gonna come to you, \n\nI wanna find you in everything that I do. \n\nI'm gonna run to you, I'm gonna count on you, \n\nI'm gonna follow. Baby, what else can I do? \n\n\n\nTake a walk inside my dream: \n\nA church, a lonely road. \n\nAll the people come and go and come and go. \n\n\n\nI'm gonna run to you, I'm gonna come to you. \n\nDo it now! \n\nI'm gonna run to you, I'm gonna count on you. \n\n\n\nI'm gonna run to you, I'm gonna come to you, \n\nI wanna find you in everything that I do. \n\nI'm gonna run to you, I'm gonna count on you, \n\nI'm gonna follow. Baby, what else can I do?";
        lyrics.setMovementMethod(new ScrollingMovementMethod());
        lyrics.setText(text);

        Button addToFavBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        addToFavBtn.setOnClickListener(btn -> {
            addToFavoriteList();
        });

    }

    private void addToFavoriteList() {
        messageDao = new MessageDao(this);
        messageDao.open();

        TextView b = findViewById(R.id.slsGroupName);
        TextView s = findViewById(R.id.slsSongName);
        TextView l = findViewById(R.id.slsLyricsName);

        MessageDTO newMessage = messageDao.create(
            new MessageDTO(b.getText().toString(),
                s.getText().toString(),
                l.getText().toString()));
    }

    private HttpURLConnection getOpenConnection(String link) throws IOException {
        URL               url        = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        return connection;
    }


}
