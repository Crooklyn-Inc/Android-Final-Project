package com.example.finalproject.soccerMatch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.material.navigation.NavigationView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * ViewInfo class is used to display a single match information on a separate layout.
 */
public class ViewInfo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public ImageView thumbnail;
    public String fileName;
    public RelativeLayout rLayout;
    public Button videoView;
    public Button addToFavourites;
    public static MyOpener myOpener;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarSMH);
        setSupportActionBar(myToolbar);

        NavigationView navigationView = findViewById(R.id.smh_nav_menu);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout smhDrawer = findViewById(R.id.smh_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                smhDrawer, myToolbar, R.string.open, R.string.close);
        smhDrawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();

        String source = intent.getStringExtra("Source");


        myOpener = new MyOpener(this);

        addToFavourites = findViewById(R.id.addToFavourites);

        if (source != null && source.equals("LOF")) {
            addToFavourites.setVisibility(View.INVISIBLE);
        }

        TextView competitionTitle = findViewById(R.id.competitionTitle);
        TextView team1 = findViewById(R.id.team1);
        TextView team2 = findViewById(R.id.team2);
        TextView date = findViewById(R.id.date);
        rLayout = findViewById(R.id.relativeLayoutSMH);
        thumbnail = findViewById(R.id.thumbnail);
        videoView = findViewById(R.id.videoURL);

        long x = intent.getLongExtra("id", 0);
        Match m = getMatch((int) x);


        competitionTitle.setText(m.getCompetitionName());
        team1.setText(m.getTeam1());
        team2.setText(m.getTeam2());
        String dateF = m.getDate();
        dateF = dateF.substring(0, dateF.length() - 5);
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MM yyyy E HH:mm");

            Date date1 = inputFormat.parse(dateF);

            dateF = outputFormat.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText(dateF);

//        date.setText(m.getDate());

        videoView.setOnClickListener(click -> {

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getVideoUrl()));
            startActivity(i);
        });

        addToFavourites.setOnClickListener(click -> {
            myOpener.insertData(m.getId(), m.getTitle(), m.getThumbnail(), m.getDate(), m.getCompetitionName(), m.getVideoUrl(), m.getTeam1(), m.getTeam2());
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.addedSMH), Toast.LENGTH_SHORT).show();
        });


        MatchQuery reqInfo = new MatchQuery();
        reqInfo.execute(m.getThumbnail());
        fileName = m.getThumbnail().substring(30, m.getThumbnail().length());

    }

    /**
     * Method to find a proper match that we want to display.
     *
     * @param id - int that is passed as a DB id number of a specific match.
     * @return - a proper match from the array.
     */
    public Match getMatch(int id) {
        Match m = null;

        for (int i = 0; i < SoccerMatchHighlights.matchArrayList.size(); i++) {
            if (SoccerMatchHighlights.matchArrayList.get(i).getId() == SoccerMatchHighlights.matchArrayList.get((int) id).getId()) {
                m = SoccerMatchHighlights.matchArrayList.get((int) i);
            }
        }
        return m;
    }

    public MyOpener getMyOpener() {
        return myOpener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File dir = getFilesDir();
        File f = new File(dir, fileName);
        boolean delete = f.delete();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.smhInstructions) {

            AlertDialog.Builder alertD = new AlertDialog.Builder(ViewInfo.this);
            alertD.setTitle(getResources().getString(R.string.infoSMH))
                    .setMessage(getResources().getString(R.string.explSMH))
                    .setNegativeButton(getResources().getString(R.string.backSMH), ((dialog, which) -> {

                    })).create().show();

        } else if (item.getItemId() == R.id.smhAPI) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.scorebat.com/video-api/"));
            startActivity(intent);

        } else if (item.getItemId() == R.id.smhDonate) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.donationTitle);
            alert.setMessage(R.string.donationMessage);
            final EditText input = new EditText(this);
            input.setHint("$$$");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            alert.setView(input);
            alert.setPositiveButton((R.string.thanks), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Toast.makeText(getApplicationContext(), "Thank you for your donation!", Toast.LENGTH_LONG).show();
                }
            });
            alert.setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Put actions for CANCEL button here, or leave in blank
                }
            });
            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.smh_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class MatchQuery extends AsyncTask<String, Integer, String> {

        private Bitmap imageTemp;
        private Drawable d;

        @Override
        protected String doInBackground(String... strings) {

            String fileName = strings[0].substring(30, strings[0].length());
            try {
                URL urlImage = new URL(strings[0]);
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) urlImage.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    imageTemp = BitmapFactory.decodeStream(connection.getInputStream());
                }
                FileOutputStream outputStream = null;

                try {
                    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    imageTemp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception ex) {

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            d = new BitmapDrawable(getResources(), imageTemp);

            return "Done";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            thumbnail.setImageBitmap(imageTemp);
//            rLayout.setBackground(d);
        }


    }
}