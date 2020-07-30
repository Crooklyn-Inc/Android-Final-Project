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

