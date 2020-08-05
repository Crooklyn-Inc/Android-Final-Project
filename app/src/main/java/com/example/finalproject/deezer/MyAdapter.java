package com.example.finalproject.deezer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.deezer.DeezerSongModel;

import java.util.ArrayList;
/*
 * @author Yulia Tsvetkova
 * @version 1
 * @ August 4, 2020
 */
/**
 * Class extends BaseAdapter and provides methods for retrieving count, item, id, or the view for the ListView.
 */
public   class MyAdapter extends BaseAdapter {
        Context context;
        ArrayList<DeezerSongModel> arrayList;

        public MyAdapter(Context context, ArrayList<DeezerSongModel> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }


        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public DeezerSongModel getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

    /**
     * Method to create a new row in the ListView and set the appropriate value.
     * @param position int index value for retrieving EarthyImage from elements ArrayList
     * @param convertView View object, will take old view and add a new row
     * @param parent ViewGroup parent for the view
     * @return View with new ListView values
     */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //        layout for this position

            if (convertView ==  null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_songs_row, parent, false);
            }
            TextView title, duration, album_name;
            title = (TextView) convertView.findViewById(R.id.songtitle);
            // duration = (TextView) convertView.findViewById(R.id.duration);
            //  album_name = (TextView) convertView.findViewById(R.id.album_name);
            title.setText(arrayList.get(position).getTitle());
            // duration.setText(arrayList.get(position).getDuration());
            //album_name.setText(arrayList.get(position).getAlbum_name());

            return convertView;
        }

    }

