package com.example.finalproject.Deezer_song;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.finalproject.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Deezer_activity1 extends AppCompatActivity {
    private String artist = "fantompower";
    String api_url = "https://api.deezer.com/search/artist/?q=XXX&output=xml";
    String api_url_artist = "";
    Bitmap image = null;
    String tracklist;
    ProgressBar progressBar;
    TextView titleTextView, durationTextView, album_nameTextView, album_coverTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_activity1);
        UserQuery userQuery = new UserQuery();

        api_url_artist = api_url.replace("XXX", artist);
        userQuery.execute(api_url_artist);

    }

    private class UserQuery extends AsyncTask<String, Integer, String> {
        String title, duration, album_name, album_cover;

        ArrayList<String> links = new ArrayList();
        ArrayList<String> headlines = new ArrayList();

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

                                URL listSongUrl = new URL(tracklist);
                                HttpURLConnection iconConnection = (HttpURLConnection) listSongUrl.openConnection();
                                iconConnection.connect();
                                int responseCode = iconConnection.getResponseCode();
                                if (responseCode == 200) {
                                    image = BitmapFactory.decodeStream(iconConnection.getInputStream());
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
                                if (parser.getText().equals(artist)) {
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
            album_coverTextView.setText("Tha album " + album_cover);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}




//    protected static final String ACTIVITY_NAME = "WeatherForecast";
//    private ProgressBar progressBar;
//private TextView title;
//    private TextView minTemp;
//    private TextView maxTemp;
//    private ImageView weatherImage;

