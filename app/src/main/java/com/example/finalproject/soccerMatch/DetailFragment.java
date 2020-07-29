package com.example.finalproject.soccerMatch;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;

/**
 * This class is used to display a simple fragment that call when user
 * press an info button to view information about the author of the project
 */
public class DetailFragment extends Fragment {

    private Bundle dataFromActivity;
    private int id;
    private AppCompatActivity parentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataFromActivity = getArguments();

        View result = inflater.inflate(R.layout.fragmet_smh, container, false);
        TextView titleSMH = (TextView) result.findViewById(R.id.titleSMH);
        TextView titleTextSMH = (TextView) result.findViewById(R.id.titleTextSMH);
        TextView versionSMH = (TextView) result.findViewById(R.id.versionSMH);
        TextView versionTextSMH = (TextView) result.findViewById(R.id.versionTextSMH);
        TextView authorSMH = (TextView) result.findViewById(R.id.authorSMH);
        TextView authorTextSMH = (TextView) result.findViewById(R.id.authorTextSMH);

        ImageView photoSMH = (ImageView) result.findViewById(R.id.photoSMH);


        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }
}
