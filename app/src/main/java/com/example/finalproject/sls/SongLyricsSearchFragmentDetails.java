/**
 * This is the copyrighted content for course
 * of mobile programming at Algonquin College
 *
 * @author Olga Zimina
 * @version 1.0.0
 * @created Jul 25, 2020
 */

package com.example.finalproject.sls;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * This class describes functionality of Fragment, shown in tablet mode of the device
 */
public class SongLyricsSearchFragmentDetails extends Fragment {
    private SongLyricsSearchFavoriteList.MessageListAdapter messageAdapter;

    private Bundle            dataFromActivity;
    private AppCompatActivity parentActivity;
    private boolean           isSongFavorite = false;
    private Context           context;
    private MessageDao        messageDao;
    private Long              songId;


    /**
     * Parameterized constructor to obtain access to the database and message adapter
     *
     * @param context        context of the parent page to use inside fragment
     * @param messageAdapter adapter to be used withing this Fragment to show rows of messages (lyrics)
     */
    public SongLyricsSearchFragmentDetails(Context context, SongLyricsSearchFavoriteList.MessageListAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
        this.context        = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataFromActivity = getArguments();

        View result = inflater.inflate(R.layout.fragment_sls_details, container, false);

        TextView band   = result.findViewById(R.id.slsGroupName);
        TextView song   = result.findViewById(R.id.slsSongName);
        TextView lyrics = result.findViewById(R.id.slsLyricsName);

        band.setText(dataFromActivity.getString(SongLyricsSearch.BAND));
        song.setText(dataFromActivity.getString(SongLyricsSearch.SONG));
        lyrics.setText(dataFromActivity.getString(SongLyricsSearch.LYRICS));

        isSongFavorite(dataFromActivity.getString(SongLyricsSearch.BAND),
            dataFromActivity.getString(SongLyricsSearch.SONG));

        Button favBtn = result.findViewById(R.id.slsAddToFavoriteListBtn);
        if (isSongFavorite) {
            favBtn.setText(R.string.slsRemoveFavorite);
        }
        favBtn.setOnClickListener(btn -> {
            if (isSongFavorite) {
                deleteFromFavorites(result, songId);
            } else {
                addToFavoriteList(result);
            }
        });


        Button gglBtn = result.findViewById(R.id.slsSearchGoogleBtn);
        gglBtn.setOnClickListener(btn -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + URLEncoder.encode(dataFromActivity.getString(SongLyricsSearch.BAND), "utf8") + "+" + URLEncoder.encode(dataFromActivity.getString(SongLyricsSearch.SONG), "utf8"))));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }

    /**
     * Remove message from Favorite list.
     *
     * @param result View to get access to parent elements
     * @param songId ID of the song to use in DB transaction
     */
    private void deleteFromFavorites(View result, Long songId) {
        messageDao = new MessageDao(context);
        messageDao.open();
        messageDao.delete(messageDao.findById(songId));
        parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();

        messageAdapter.remove(messageDao.findById(songId));

        Button favBtn = result.findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText(R.string.slsAddFavorite);
        isSongFavorite = false;
    }

    /**
     * This method is neede to determine whether band-song pair is already added
     * to the favorite list and saved in database or not.
     * It is necessary to define buttons at the bottom and change them dynamically -
     * Remove from Favorites/Add to favorites
     *
     * @param bandName name of the Band
     * @param songText name of the Song
     *
     * @return true if that song is already saved in Favorites, false otherwise
     */
    private boolean isSongFavorite(String bandName, String songText) {
        messageDao = new MessageDao(context);
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

    /**
     * Actual method which adds song to the Favorite list and change the button below from
     * ADD TO FAVORITES to REMOVE FROM FAVORITES
     *
     * @param result parent view to be modified and show new song
     */
    private void addToFavoriteList(View result) {
        messageDao = new MessageDao(context);
        messageDao.open();

        TextView b = result.findViewById(R.id.slsGroupName);
        TextView s = result.findViewById(R.id.slsSongName);
        TextView l = result.findViewById(R.id.slsLyricsName);

        MessageDTO newMessage = messageDao.create(
            new MessageDTO(b.getText().toString(),
                s.getText().toString(),
                l.getText().toString()));
        messageDao.close();
        Button favBtn = result.findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText(R.string.slsRemoveFavorite);
        isSongFavorite = true;
        songId         = newMessage.getId();
    }
}
