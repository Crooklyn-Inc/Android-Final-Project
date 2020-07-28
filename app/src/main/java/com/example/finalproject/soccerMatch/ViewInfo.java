package com.example.finalproject.soccerMatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.SoccerMatchHighlights;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.database.sqlite.SQLiteDatabase;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.finalproject.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ViewInfo extends AppCompatActivity {
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
        Match m = getMatch((int)x);


        competitionTitle.setText(m.getCompetitionName());
        team1.setText(m.getTeam1());
        team2.setText(m.getTeam2());
        String dateF = m.getDate();
        date.setText(m.getDate());
        videoView.setOnClickListener(click -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getVideoUrl()));
            startActivity(i);
        });

        addToFavourites.setOnClickListener(click -> {


            myOpener.insertData(m.getId(), m.getTitle(), m.getThumbnail(), m.getDate(), m.getCompetitionName(), m.getVideoUrl(), m.getTeam1(), m.getTeam2());
            Toast.makeText(getApplicationContext(), "Match has been added!", Toast.LENGTH_SHORT).show();

        });


        MatchQuery reqInfo = new MatchQuery();
        reqInfo.execute(m.getThumbnail());
        fileName = m.getThumbnail().substring(30, m.getThumbnail().length());

    }

    public Match getMatch(int id){
        Match m = null;

        for (int i = 0; i<SoccerMatchHighlights.matchArrayList.size(); i++){
            if (SoccerMatchHighlights.matchArrayList.get(i).getId() == SoccerMatchHighlights.matchArrayList.get((int)id).getId()){
                m = SoccerMatchHighlights.matchArrayList.get((int)i);
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