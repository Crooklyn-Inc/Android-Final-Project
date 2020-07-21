package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<String> songLinks = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_activity1);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        artist = intent.getStringExtra(DeezerSongSearch.EXTRA_MESSAGE);

        api_url_artist = api_url.replace("XXX", artist);

        UserQuery userQuery = new UserQuery();
        userQuery.execute(api_url_artist);

        progressBar = findViewById(R.id.progressBar);

        //ListView listView = findViewById(R.id.ListView);
        //listView.setAdapter(myAdapter = new MyAdapter());

    }


    private class UserQuery extends AsyncTask<String, Integer, String> {
        String title, duration, album_name, album_cover;


        protected String doInBackground(String... params) {
            //Log.i(ACTIVITY_NAME, "In doInBackground");
            try {
                URL url = new URL(params[0]);
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


                int eventType = parser.getEventType(); //The parser is currently at START_DOCUMENT
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String artist22 = parser.getName();
                        if (parser.getName().equalsIgnoreCase("artist")) {
                            insideItem = true;
                            eventType = parser.next();
                            continue;
                        }
//                        else if((parser.getName().equalsIgnoreCase("name") && (insideItem == true)) ) {
//                            if(parser.getAttributeValue(null, "name") == "Fantom") {
//                                artist = parser.getName();
//                            }
//                            else {
//                                eventType = parser.next();
//                                continue;
//                            }
//                        }

                        else if (parser.getName().equalsIgnoreCase("name")) {
                            gotName = true;
                        } else if (parser.getName().equalsIgnoreCase("tracklist")) {
                            if (artistXml != "") {
                                tracklist = parser.nextText();
                                //links.add(tracklist); // add links to the list

                                publishProgress(50);

                                URL songsUrl = new URL(tracklist);
                                HttpURLConnection connSongsList = (HttpURLConnection) songsUrl.openConnection();
                                connSongsList.setReadTimeout(10000);
                                connSongsList.setConnectTimeout(15000);
                                connSongsList.setRequestMethod("GET");
                                connSongsList.setDoInput(true);
                                connSongsList.connect();

                                InputStream streamSongs = conn.getInputStream();
                                int responseCode = connSongsList.getResponseCode();
                                if (responseCode == 200) {
                                    //create a JSON object from the response
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(streamSongs, "UTF-8"), 8);
                                    StringBuilder sb = new StringBuilder();

                                    String line = "";
                                    while ((line = reader.readLine()) != null)
                                    {
                                        sb.append(line + "\n");
                                    }
                                    String result = sb.toString();
                                    JSONObject jObject = new JSONObject(result);
                                    Log.e("song list", result);
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


            return "Finished tasks";
        }


//                        }else if (eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("tracklist")) {
//                            insideItem = false;
//                        }


//                    if (parser.getName().equals("tracklist")) {
//                        current = parser.getAttributeValue(null, "value");
//                        publishProgress(25);
//                        min = parser.getAttributeValue(null, "min");
//                        publishProgress(50);
//                        max = parser.getAttributeValue(null, "max");
//                        publishProgress(75);
//                    }
//                    if (parser.getName().equals("weather")) {
//                        iconName = parser.getAttributeValue(null, "icon");
//                        String iconFile = iconName+".png";
//                        if (fileExistance(iconFile)) {
//                            FileInputStream inputStream = null;
//                            try {
//                                inputStream = new FileInputStream(getBaseContext().getFileStreamPath(iconFile));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            icon = BitmapFactory.decodeStream(inputStream);
//                            Log.i(ACTIVITY_NAME, "Image already exists");
//                        } else {
//                            URL iconUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
//                            icon = getImage(iconUrl);
//                            FileOutputStream outputStream = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
//                            icon.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
//                            outputStream.flush();
//                            outputStream.close();
//                            Log.i(ACTIVITY_NAME, "Adding new image");
//                        }
//                        Log.i(ACTIVITY_NAME, "file name="+iconFile);
//                        publishProgress(100);
//                    }


        //        @Override
        protected void onProgressUpdate(Integer... value) {
            Log.i("Tag", "In onProgressUpdate");
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            String degree = Character.toString((char) 0x00B0);

            titleTextView.setText(title);
            durationTextView.setText(duration);
            album_nameTextView.setText(album_name);
            album_coverTextView.setText("Album " + album_cover);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songLinks.size();
        }

        @Override
        public String getItem(int position) {
            return songLinks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
    //        layout for this position
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.list_songs_row, parent, false );
            TextView textView = newView.findViewById(R.id.textGoesHere);
            textView.setText((String)getItem(position));

            return newView;
        }

    }
}

