package com.example.finalproject.deezer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.R;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeezerFragmentDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeezerFragmentDetails extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();

        View result = inflater.inflate(R.layout.fragment_deezer_details, container, false);
        ImageView viAlbumImage = (ImageView) result.findViewById(R.id.albumImageView);
        String imageUrl = dataFromActivity.getString(Deezer_activity3.ALBUM_IMAGE);

        TextView albumTitle = (TextView) result.findViewById(R.id.albumTitle);
        albumTitle.setText("Album Name : \"" + dataFromActivity.getString(Deezer_activity3.ALBUM_NAME) + "\"");
        TextView title = (TextView) result.findViewById(R.id.title);
        title.setText("Song Name : \"" + dataFromActivity.getString(Deezer_activity3.TITLE) + "\"");
        TextView songDuration = (TextView) result.findViewById(R.id.songDuration);
       songDuration.setText("Duration : \"" + dataFromActivity.getString(Deezer_activity3.DURATION) + "\"");

       if(imageUrl != null) {
           new DeezerFragmentDetails.DownloadImageTask(viAlbumImage.findViewById(R.id.albumImageView))
                   .execute(imageUrl);
       }

        return result;
    }



    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;

        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

        }
    }

}

