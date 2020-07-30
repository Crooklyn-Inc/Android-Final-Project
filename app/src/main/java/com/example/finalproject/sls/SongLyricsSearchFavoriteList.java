package com.example.finalproject.sls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.deezer.DeezerSongSearch;
import com.example.finalproject.geo.GeoDataSource;
import com.example.finalproject.R;
import com.example.finalproject.soccerMatch.SoccerMatchHighlights;
import com.example.finalproject.sls.data.MessageDTO;
import com.example.finalproject.sls.database.MessageDao;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class SongLyricsSearchFavoriteList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MessageListAdapter              messageAdapter;
    private MessageDao                      messageDao;
    private boolean                         isTablet;
    private SongLyricsSearchFragmentDetails dFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sls_favorite_list);

        Toolbar myToolbar = findViewById(R.id.slsToolbar);
        setSupportActionBar(myToolbar);

        NavigationView navigationView = findViewById(R.id.slsNavView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout          drawer = findViewById(R.id.slsDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
        Toast.makeText(this, getResources().getString(R.string.slsFavDeleted), Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sls_nav_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.geoMenuItem:
                message = "Opening GeoData";
                startActivity(new Intent(this, GeoDataSource.class));
                break;
            case R.id.soccerMenuItem:
                message = "Opening Soccer";
                startActivity(new Intent(this, SoccerMatchHighlights.class));
                break;
            case R.id.deezerMenuItem:
                message = "Opening Deezer";
                startActivity(new Intent(this, DeezerSongSearch.class));
                break;
            case R.id.aboutProject:
                Toast.makeText(this, getResources().getString(R.string.slsToolAboutProject), Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.appInstructionsMenuItem) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.geoInstructionsMenuItem)
                .setIcon(R.drawable.ic_lyric_song)
                .setMessage(R.string.slsInstructionsMessage)
                .setPositiveButton(R.string.ok, (click, arg) -> {})
                .create()
                .show();
        } else if (item.getItemId() == R.id.aboutApiMenuItem) {
            startActivity(new Intent(Intent.ACTION_VIEW));
        } else if (item.getItemId() == R.id.donateToProjectMenuItem) {

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(40, 0, 40, 0);
            final EditText input = new EditText(this);
            input.setLayoutParams(lp);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setLines(1);
            input.setMaxLines(1);
            input.setHint(R.string.geoThreeCurrencySigns);
            container.addView(input, lp);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.geoDonationTitle)
                .setIcon(R.drawable.ic_geo_donate)
                .setMessage(R.string.geoDonationMessage)
                .setView(container)
                .setPositiveButton(getResources().getString(R.string.geoThankYou), (click, arg) -> {})
                .setNegativeButton(R.string.geoCancel, (click, arg) -> { })
                .setView(container)
                .create()
                .show();
        }

        return false;
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
