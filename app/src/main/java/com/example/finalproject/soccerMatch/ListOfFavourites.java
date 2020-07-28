package com.example.finalproject.soccerMatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;
import com.example.finalproject.SoccerMatchHighlights;
import com.google.android.material.snackbar.Snackbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class ListOfFavourites extends AppCompatActivity {
    public ArrayList<Match> matchArray = new ArrayList<>();
    public MyOpener myOpener = ViewInfo.myOpener;

    public static Intent i;

    ListView listOfFavourites;
    public MyListAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_favourites);



        listOfFavourites = findViewById(R.id.listOfFavourites);
        myAdapter = new MyListAdapter();
        listOfFavourites.setAdapter(myAdapter);

        showMatches();


        listOfFavourites.setOnItemLongClickListener((parent, view, position, id) -> {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("You are about to delete " + matchArray.get(position) + " match from your list")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Match selected = matchArray.get(position);
                        myOpener.deleteMessage(selected);
                        matchArray.remove(position);
                        myAdapter.notifyDataSetChanged();

                    })
                    .setNegativeButton("NO", (dialog, which) -> {

                    });

            alertBuilder.create().show();
            return true;
        });


    }

    public class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return matchArray.size();
        }

        public Match getItem(int position) {
            return matchArray.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }


        public View getView(int position, View old, ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View newView;
            Match m = (Match) getItem(position);


            newView = inflater.inflate(R.layout.activity_soccer_match_highlights_list_of_favourites, parent, false);
            Button b = (Button) newView.findViewById(R.id.title);
            b.setOnClickListener(click -> {
                i = new Intent(ListOfFavourites.this, ViewInfo.class);
                i.putExtra("Source", "LOF");

                i.putExtra("id", m.getId());
                startActivity(i);
            });
            Button remove = (Button) newView.findViewById(R.id.removeBtnSMH);

            remove.setOnLongClickListener(click -> {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ListOfFavourites.this);
                alertBuilder.setTitle("Are you sure tou want to delete")
                        .setMessage("Match " + matchArray.get(position).getTitle())
                        .setPositiveButton("YES", (dialog, which) -> {
                            Match selected = matchArray.get(position);
                            myOpener.deleteMessage(selected);
                            matchArray.remove(position);
                            myAdapter.notifyDataSetChanged();
                            Snackbar.make(remove, "Item Deleted Successfully!", Snackbar.LENGTH_SHORT).show();

                        })
                        .create().show();


                return true;
            });


            TextView textView = newView.findViewById(R.id.title);
            textView.setText(m.getTitle());

            return newView;
        }


    }


    private void showMatches() {
        myOpener.getWritableDatabase();
        Cursor c = myOpener.viewDataDb();

        if (c.getCount() != 0) {
            while (c.moveToNext()) {
                Match m = new Match(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7));
                matchArray.add(m);
                listOfFavourites.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        }
    }
}