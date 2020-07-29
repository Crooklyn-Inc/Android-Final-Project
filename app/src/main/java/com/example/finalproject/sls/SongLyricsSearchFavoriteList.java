package com.example.finalproject.sls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.DeezerFragmentDetails;
import com.example.finalproject.R;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;

import java.util.List;

public class SongLyricsSearchFavoriteList extends AppCompatActivity {

    private MessageListAdapter              messageAdapter;
    private MessageDao                      messageDao;
    private boolean                         isTablet;
    private SongLyricsSearchFragmentDetails dFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sls_favorite_list);

        // create an instance of DAO
        messageDao = new MessageDao(this);
        // open DB connection
        messageDao.open();
        // load all saved data
        List<MessageDTO> messageList = messageDao.findAll();
        // bind loaded data to the UI
        messageAdapter = new MessageListAdapter(this, messageList);

        ListView theList = (ListView) findViewById(R.id.slsList);
        theList.setAdapter(messageAdapter);

        isTablet = findViewById(R.id.slsFragmentLocation) != null;

        theList.setOnItemLongClickListener((list, item, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SongLyricsSearchFavoriteList.this);
            builder.setTitle(getResources().getString(R.string.slsAlertDeleteTitle))
                .setMessage(buildAlertMessage(position, id))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes),
                    (dialog, which) -> deleteMessage(messageAdapter.getItem(position)))
                .setNegativeButton(getResources().getString(R.string.no),
                    (dialog, which) -> {});
            //Creating dialog box
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });

        theList.setOnItemClickListener((list, item, position, id) -> {
            Bundle dataToPass = new Bundle();
            dataToPass.putString(SongLyricsSearch.BAND, messageAdapter.getItem(position).getBand());
            dataToPass.putString(SongLyricsSearch.SONG, messageAdapter.getItem(position).getSong());
            dataToPass.putString(SongLyricsSearch.LYRICS, messageAdapter.getItem(position).getLyrics());

            if (isTablet) {
                dFragment = new SongLyricsSearchFragmentDetails(this, messageAdapter); //add a DetailFragment
                dFragment.setArguments(dataToPass);
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.slsFragmentLocation, dFragment) //Add the fragment in FrameLayout
                    .commit(); //actually load the fragment.
            } else {
                Intent intentSongLyrics = new Intent(SongLyricsSearchFavoriteList.this, SongLyricsSearchResult.class);
                intentSongLyrics.putExtra(SongLyricsSearch.BAND, messageAdapter.getItem(position).getBand());
                intentSongLyrics.putExtra(SongLyricsSearch.SONG, messageAdapter.getItem(position).getSong());
                intentSongLyrics.putExtra(SongLyricsSearch.LYRICS, messageAdapter.getItem(position).getLyrics());

                startActivity(intentSongLyrics);
            }
        });
    }

    private String buildAlertMessage(int position, long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.alertBuilderMsg1)).append(position).append("\n");
        sb.append(getResources().getString(R.string.alertBuilderMsg2)).append(id);
        return sb.toString();
    }

    protected void deleteMessage(MessageDTO message) {
        messageDao.delete(message);
        messageAdapter.remove(message);
        if (isTablet) {
            getSupportFragmentManager().beginTransaction().remove(dFragment).commit();
        } else {

        }
    }

    static class MessageListAdapter extends ArrayAdapter<MessageDTO> {
        public MessageListAdapter(@NonNull Context context, @NonNull List<MessageDTO> objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageDTO message = getItem(position);
            View       rowView = createRowView(message, parent);

            TextView band = rowView.findViewById(R.id.slsFavoriteRecordBand);
            band.setText(message.getBand());
            TextView song = rowView.findViewById(R.id.slsFavoriteRecordSong);
            song.setText(message.getSong());

            return rowView;
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        private View createRowView(MessageDTO currentMessage, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.activity_sls_favorite_record, parent, false);
        }
    }

}
