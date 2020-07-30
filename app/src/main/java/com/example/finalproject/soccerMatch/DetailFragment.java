package com.example.finalproject.soccerMatch;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataFromActivity = getArguments();

        View result = inflater.inflate(R.layout.fragmet_smh, container, false);

        TextView titleSMH = result.findViewById(R.id.titleSMH);
        titleSMH.setText(dataFromActivity.getString("Title"));

        TextView titleTextSMH = result.findViewById(R.id.titleTextSMH);
        titleTextSMH.setText(dataFromActivity.getString("TitleText"));

        TextView versionSMH = result.findViewById(R.id.versionSMH);
        versionSMH.setText(dataFromActivity.getString("Version"));

        TextView versionTextSMH = result.findViewById(R.id.versionTextSMH);
        versionTextSMH.setText(dataFromActivity.getString("VersionText"));

        TextView authorSMH = result.findViewById(R.id.authorSMH);
        authorSMH.setText(dataFromActivity.getString("Author"));

        TextView authorTextSMH = result.findViewById(R.id.authorTextSMH);
        authorTextSMH.setText(dataFromActivity.getString("AuthorText"));


        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }
}
