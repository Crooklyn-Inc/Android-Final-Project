package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

public class SongLyricsSearchResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_lyrics_search_result);

        TextView lyrics = findViewById(R.id.slsLyricsName);
        String   text   = "Baby, sometimes I feel like dying, \ndriving while I'm closing my eyes. \nMoving in and out of hiding, \ntrying to catch some truth in my life. \nWatching your stars and your moonlight, \ncome tumbling down from the sky. \n\nTake it now. \n\n\n\nI'm gonna run to you, I'm gonna come to you, \n\nI wanna find you in everything that I do. \n\nI'm gonna run to you, I'm gonna count on you, \n\nI'm gonna follow. Baby, what else can I do? \n\n\n\nSunday morning, my town is sleeping. \n\nLying all alone in my bed, \n\nthere's not a sound, I can't help but listening. \n\nWishing I was somewhere else instead. \n\nBut sometimes they're too hard to handle, \n\nthese voices inside my head. \n\nListen now. \n\n\n\nI'm gonna run to you, I'm gonna come to you, \n\nI wanna find you in everything that I do. \n\nI'm gonna run to you, I'm gonna count on you, \n\nI'm gonna follow. Baby, what else can I do? \n\n\n\nTake a walk inside my dream: \n\nA church, a lonely road. \n\nAll the people come and go and come and go. \n\n\n\nI'm gonna run to you, I'm gonna come to you. \n\nDo it now! \n\nI'm gonna run to you, I'm gonna count on you. \n\n\n\nI'm gonna run to you, I'm gonna come to you, \n\nI wanna find you in everything that I do. \n\nI'm gonna run to you, I'm gonna count on you, \n\nI'm gonna follow. Baby, what else can I do?";
        lyrics.setMovementMethod(new ScrollingMovementMethod());
        lyrics.setText(text);
    }
}
