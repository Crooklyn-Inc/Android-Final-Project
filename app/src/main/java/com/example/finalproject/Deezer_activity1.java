package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Deezer_activity1 extends AppCompatActivity {
    private String artist = "";
    String api_url = "https://api.deezer.com/search/artist/?q=XXX&output=xml";
    String api_url_artist = "";
    Bitmap image = null;
    String tracklist;
    ProgressBar progressBar;
    TextView titleTextView, durationTextView, album_nameTextView, album_coverTextView;
    MyAdapter myAdapter;
    ArrayList<DeezerSongModel> songLinks = new ArrayList();
    ListView listView;
    SwipeRefreshLayout swipeRefresh;
    public static String ARTIST_DETAILS = "ARTIST_DETAILS";
    JSONArray dataArraySongs = null;
    DeezerSongDBHelper dbOpener;
    SQLiteDatabase db;
    boolean isSaved;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_activity1);
        DeezerSongDBHelper dbOpener = new DeezerSongDBHelper(this);

        dbOpener = new DeezerSongDBHelper(this);
        db = dbOpener.getWritableDatabase();
      //  if (db != null){


        listView = findViewById(R.id.ListView);
        progressBar = findViewById(R.id.progressBarDeezerActivity1);
        TextView songTitle = findViewById(R.id.songtitle);
        TextView songDuration = findViewById(R.id.duration);
        TextView songAlbumName = findViewById(R.id.album_name);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        artist = intent.getStringExtra(DeezerSongSearch.EXTRA_MESSAGE);

        api_url_artist = api_url.replace("XXX", artist);

        UserQuery userQuery = new UserQuery();
        userQuery.execute(api_url_artist);




        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent deezerAct2 = new Intent(Deezer_activity1.this, Deezer_activity2.class);
            JSONObject jsonObjectSongsTemp = null;

            try {
                jsonObjectSongsTemp = dataArraySongs.getJSONObject((int)position);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            deezerAct2.putExtra(ARTIST_DETAILS, jsonObjectSongsTemp.toString());
            startActivity(deezerAct2);
        });


        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            JSONObject jsonObjectSongsTemp = null;
            String songTitle2 = null;
            String songDuration2 = null;
            String songAlbum2 = null;
            String songAlbumImage2 = null;
            try {
                jsonObjectSongsTemp = dataArraySongs.getJSONObject((int)position);
                songTitle2 = jsonObjectSongsTemp.getString("title");
                songDuration2 = jsonObjectSongsTemp.getString("duration");
                songAlbum2 = jsonObjectSongsTemp.getJSONObject("album").getString("title");

                JSONArray jimageContributors = jsonObjectSongsTemp.getJSONArray("contributors");
                String s1 = jimageContributors.get(0).toString();
                JSONObject j2 = new JSONObject(s1);
                songAlbumImage2 = j2.getString("picture_small");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String finalSongTitle = songTitle2;
            String finalSongDuration = songDuration2;
            String finalSongAlbum = songAlbum2;
            String finalSongAlbumImage = songAlbumImage2;

            DeezerSongDBHelper dbOpener1 = new DeezerSongDBHelper(this);
            Boolean recordExist = dbOpener1.checkIfRecordExist(finalSongTitle, finalSongDuration);

         //   if(!recordExist) {
                alert.setTitle(getResources().getString(R.string.alertBuilderTitle))
                    .setMessage(getResources().getString(R.string.alertBuilderMsg1) + " " + (position+1))
                    .setPositiveButton(R.string.yes, (click, b) -> {
                        if(!recordExist) {
                        ContentValues updatedValues = new ContentValues();

                        updatedValues.put(DeezerSongDBHelper.COL_TITLE, finalSongTitle);
                        updatedValues.put(DeezerSongDBHelper.COL_DURATION, finalSongDuration);
                        updatedValues.put(DeezerSongDBHelper.COL_ALBUM_NAME, finalSongAlbum);
                        updatedValues.put(DeezerSongDBHelper.COL_ALBUM_IMAGE, finalSongAlbumImage);

                        //now call the insert function:
                        db.insert(DeezerSongDBHelper.DB_TABLE, null, updatedValues);
                        Toast.makeText(getApplicationContext(), R.string.dzdToast1, Toast.LENGTH_SHORT).show();}
                        else {
                            Toast.makeText(getApplicationContext(), R.string.dzdToast2, Toast.LENGTH_SHORT).show();
                                }
                        //Toast.makeText(getApplicationContext(), "Song is already exist in your favorites ", Toast.LENGTH_SHORT).show();}
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (click, arg) -> {
                    });
                alert.create().show();
                return true;


            //return true;
           // return false;
        });


    }


    public  class UserQuery extends AsyncTask<String, Integer, String> {
        String title, duration, album_name, album_cover;


        protected String doInBackground(String... params) {
            //Log.i(ACTIVITY_NAME, "In doInBackground");
            String result = "";

            songLinks.clear();
            try {
                String artistencode = params[0].replace(" ", "%20");
                URL url = new URL(artistencode);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                InputStream stream = conn.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);


                String parameter = null;
                String artistXml = "";
                boolean insideItem = false;
                boolean gotName = false;

                publishProgress(10);
                int eventType = parser.getEventType(); //The parser is currently at START_DOCUMENT
                 publishProgress(25);
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String artist22 = parser.getName();

                        if (parser.getName().equalsIgnoreCase("artist")) {
                            insideItem = true;
                            eventType = parser.next();
                            continue;
                        }
                        else if (parser.getName().equalsIgnoreCase("name")) {
                            gotName = true;
                        } else if (parser.getName().equalsIgnoreCase("tracklist")) {
                            if (artistXml != "") {
                                tracklist = parser.nextText();
                                //links.add(tracklist); // add links to the list

                                publishProgress(50);

                                URL songsUrl = new URL(tracklist);
                                //URL songsUrl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");
                                HttpURLConnection connSongsList = (HttpURLConnection) songsUrl.openConnection();
                                connSongsList.setReadTimeout(10000);
                                connSongsList.setConnectTimeout(15000);
                                connSongsList.setRequestMethod("GET");
                                connSongsList.setDoInput(true);
                                connSongsList.connect();

                                InputStream streamSongs = connSongsList.getInputStream();
                                int responseCode = connSongsList.getResponseCode();

                                if (responseCode == 200) {
                                    publishProgress(75);
                                    //create a JSON object from the response
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(streamSongs, "UTF-8"), 8);
                                    StringBuilder sb = new StringBuilder();

                                    String line = "";
                                    while ((line = reader.readLine()) != null)
                                    {
                                        sb.append(line + "\n");
                                    }
                                    result = sb.toString();
//                                    JSONObject jObject = new JSONObject(result);
//                                    JSONArray dataArray = jObject.getJSONArray("data");
//
//                                    for (int i = 0; i < dataArray.length(); i++) {
//                                        // create a JSONObject for fetching single user data
//                                        JSONObject dataDetail = dataArray.getJSONObject(i);
//                                        // fetch email and name and store it in arraylist
//                                        String songTitle = dataDetail.getString("title");
//                                        String songDuration = dataDetail.getString("duration");
//                                        songLinks.add(songTitle);
//
//                                        Log.e("song list", );
//                                    }

                                }

                                artistXml = "";
                                break;
                            }
                        }

                    } else if (eventType == XmlPullParser.TEXT) {
                        if (insideItem == true) {
                            String value22 = parser.getText();
                            //System.out.println("Text " + parser.getText());

                            if (gotName == true) {
                                if (parser.getText().toLowerCase().equals(artist.toLowerCase())) {
                                    artistXml = parser.getText();
                                    gotName = false;
                                } else {
                                    eventType = parser.next();
                                    continue;
                                }
                            }

                        }
                    }

                    eventType = parser.next(); //move to the next xml event and store it in a variable
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AsyncTask", "Error");
            }

            publishProgress(100);
            return result;
        }


        @Override
        protected void onProgressUpdate(Integer... value) {
            Log.i("Tag", "In onProgressUpdate");
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);

            try {
                    if(s != "")
                    {
                        JSONObject jObject = new JSONObject(s);
                        dataArraySongs = jObject.getJSONArray("data");

                    for (int i = 0; i < dataArraySongs.length(); i++) {

                        JSONObject jsonObjectSongs = dataArraySongs.getJSONObject(i);

                        String songTitle = jsonObjectSongs.getString("title");
                        String songDuration = jsonObjectSongs.getString("duration");
                        String songAlbum = jsonObjectSongs.getJSONObject("album").getString("title");

                        DeezerSongModel songmodel = new DeezerSongModel();
                        songmodel.setTitle(songTitle);
                        songmodel.setDuration(songDuration);
                        songmodel.setAlbum_name(songAlbum);
                        songLinks.add(songmodel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MyAdapter adapter = new MyAdapter(Deezer_activity1.this, songLinks);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);

        }


    }


//    class MyAdapter extends BaseAdapter {
//        Context context;
//        ArrayList<DeezerSongModel> arrayList;
//
//        public MyAdapter(Context context, ArrayList<DeezerSongModel> arrayList) {
//            this.context = context;
//            this.arrayList = arrayList;
//        }
//
//
//        @Override
//        public int getCount() {
//            return arrayList.size();
//        }
//
//        @Override
//        public DeezerSongModel getItem(int position) {
//            return arrayList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return getItem(position).getId();
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//    //        layout for this position
//
//            if (convertView ==  null) {
//                convertView = LayoutInflater.from(context).inflate(R.layout.list_songs_row, parent, false);
//            }
//            TextView title, duration, album_name;
//            title = (TextView) convertView.findViewById(R.id.songtitle);
//           // duration = (TextView) convertView.findViewById(R.id.duration);
//          //  album_name = (TextView) convertView.findViewById(R.id.album_name);
//            title.setText(arrayList.get(position).getTitle());
//           // duration.setText(arrayList.get(position).getDuration());
//            //album_name.setText(arrayList.get(position).getAlbum_name());
//
//            return convertView;
//        }
//
//    }
}

