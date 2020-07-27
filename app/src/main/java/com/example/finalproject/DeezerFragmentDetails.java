package com.example.finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


        View result = inflater.inflate(R.layout.deezer_fragment_details_activity, container, false);
        ImageView albumImage = (ImageView) result.findViewById(R.id.albumImageView);
        TextView albumTitle = (TextView) result.findViewById(R.id.albumTitle);
        TextView title = (TextView) result.findViewById(R.id.title);
        TextView songDuration = (TextView) result.findViewById(R.id.songDuration);

        return result;
    }



    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }
}

