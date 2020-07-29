package com.example.finalproject.sls;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.DeezerFragmentDetails;
import com.example.finalproject.Deezer_activity3;
import com.example.finalproject.R;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;

import java.util.List;

public class SongLyricsSearchFragmentDetails extends Fragment {
    private final SongLyricsSearchFavoriteList.MessageListAdapter messageAdapter;
    private       Bundle                                          dataFromActivity;
    private       AppCompatActivity                               parentActivity;
    private       boolean                                         isSongFavorite = false;
    private       Context                                         context;
    private       MessageDao                                      messageDao;
    private       Long                                            songId;


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
            favBtn.setText("Remove Favorite");
        }
        favBtn.setOnClickListener(btn -> {
            if (isSongFavorite) {
                deleteFromFavorites(result, songId);
            } else {
                addToFavoriteList(result);
            }
        });

        return result;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }

    private void deleteFromFavorites(View result, Long songId) {
        messageDao = new MessageDao(context);
        messageDao.open();
        messageDao.delete(messageDao.findById(songId));
        parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();

        Button favBtn = result.findViewById(R.id.slsAddToFavoriteListBtn);
        favBtn.setText("Add to Favorite");
        isSongFavorite = false;
    }

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
        favBtn.setText("Remove Favorite");
        isSongFavorite = true;
        songId         = newMessage.getId();
    }
}
