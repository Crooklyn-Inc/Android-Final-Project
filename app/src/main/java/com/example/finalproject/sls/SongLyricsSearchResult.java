package com.example.finalproject.sls;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongLyricsSearchResult extends AppCompatActivity {

    private MessageDao messageDao;
    private Long       songId;
    private boolean    isSongFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sls_result);

        Intent searchData = getIntent();
        String bandName   = searchData.getStringExtra(SongLyricsSearch.BAND);
        String songText   = searchData.getStringExtra(SongLyricsSearch.SONG);
        String lyricsText = searchData.getStringExtra(SongLyricsSearch.LYRICS);

        TextView band = findViewById(R.id.slsGroupName);
        TextView song = findViewById(R.id.slsSongName);
        band.setText(bandName);
        song.setText(songText);

        isSongFavorite(bandName, songText);

        TextView lyrics = findViewById(R.id.slsLyricsName);

        lyrics.setMovementMethod(new ScrollingMovementMethod());

        if (lyricsText == null || lyricsText.isEmpty()) {
            SongLyricsSearchNetwork            searchNetwork = new SongLyricsSearchNetwork(findViewById(R.id.slsLyricsName).getRootView());
            AsyncTask<String, Integer, String> asd           = searchNetwork.execute(band.getText().toString(), song.getText().toString());
            try {
                lyrics.setText(asd.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            lyrics.setText(lyricsText);
        }

        Button favBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        if (isSongFavorite) {
            favBtn.setText("Remove Favorite");
        }
        favBtn.setOnClickListener(btn -> {
            if (isSongFavorite) {
                deleteFromFavorites(songId);
            } else {
                addToFavoriteList();
            }
        });

    }

    private void deleteFromFavorites(Long songId) {
        messageDao = new MessageDao(this);
        messageDao.open();
        messageDao.delete(messageDao.findById(songId));
        Button favBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText("Add to Favorite");
        isSongFavorite = false;
    }

    private boolean isSongFavorite(String bandName, String songText) {
        messageDao = new MessageDao(this);
        messageDao.open();

        List<MessageDTO> favList = messageDao.findAll();
        for (MessageDTO record : favList) {
            if (record.getBand().equalsIgnoreCase(bandName) && record.getSong().equalsIgnoreCase(songText)) {
                songId         = record.getId();
                isSongFavorite = true;
                messageDao.close();
                return true;
            }
        }
        messageDao.close();
        return false;

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
        messageDao.close();
        Button favBtn = findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText("Remove Favorite");
        isSongFavorite = true;
        songId = newMessage.getId();
    }
}
