package com.example.finalproject.geo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.finalproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeoCityDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeoCityDetailsFragment extends Fragment {

    public interface OnCityStatusChangeListener {
        public void onCityStatusChange(Long index);
    }

    private OnCityStatusChangeListener onCityStatusChangeListener;
    ListView geoListViewCityAttributes;
    Button geoBtnAddRemoveFavourites;
    private Bundle incomingBundle;
    private long incomingCityId;
    private static final String GOOGLE_MAP_REQUEST = "https://www.google.com/maps/@?api=1&map_action=map&center=%S,%S&zoom=15";

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public GeoCityDetailsFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment GeoCityDetailsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static GeoCityDetailsFragment newInstance(String param1, String param2) {
//        GeoCityDetailsFragment fragment = new GeoCityDetailsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onCityStatusChangeListener = (OnCityStatusChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCityStatusChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        incomingBundle = getArguments();
        incomingCityId = incomingBundle.getLong(GeoDataSource.ATTR_MAP.get(0).string);

        View inflatedView = inflater.inflate(R.layout.fragment_geo_city_details, container, false);

        ImageView geoImgViewFavouriteSign = inflatedView.findViewById(R.id.geoImgViewFavouriteSign);
        geoImgViewFavouriteSign.setImageResource(incomingCityId > 0 ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        Button geoBtnAddRemoveFavourites = inflatedView.findViewById(R.id.geoBtnAddRemoveFavourites);
        geoBtnAddRemoveFavourites.setText(incomingCityId > 0 ? R.string.geoBtnRemoveFromFavourites : R.string.geoBtnAddToFavourites);

        geoListViewCityAttributes = inflatedView.findViewById(R.id.geoListViewCityAttributes);
        geoListViewCityAttributes.setAdapter(new GeoListViewCityAttrAdapter());

        geoBtnAddRemoveFavourites = inflatedView.findViewById(R.id.geoBtnAddRemoveFavourites);

        geoBtnAddRemoveFavourites.setOnClickListener( v -> onCityStatusChangeListener.onCityStatusChange(incomingCityId));

        Button geoBtnGoToGoogleMaps = inflatedView.findViewById(R.id.geoBtnGoToGoogleMaps);

        geoBtnGoToGoogleMaps.setOnClickListener( v -> {
            String latStr = String.valueOf(incomingBundle.getDouble(GeoDataSource.ATTR_MAP.get(GeoDataSource.LAT_INDEX).string));
            String lonStr = String.valueOf(incomingBundle.getDouble(GeoDataSource.ATTR_MAP.get(GeoDataSource.LON_INDEX).string));
            String urlString = String.format(GOOGLE_MAP_REQUEST, latStr, lonStr);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            startActivity(browserIntent);
       });

        return inflatedView;
    }

    class GeoListViewCityAttrAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return GeoDataSource.ATTR_MAP.size() - 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                long cityID = getItemId(position);
                return (cityID > 0) ? String.valueOf(cityID) : getString(R.string.geoNotApplicable);
            }
            else if (GeoDataSource.ATTR_MAP.get(position).isReal) {
                return String.format("%.6f\u00B0", Math.round(incomingBundle.getDouble(GeoDataSource.ATTR_MAP.get(position).string) * 1000000d) / 1000000d);
            }
            else {
                return incomingBundle.getString(GeoDataSource.ATTR_MAP.get(position).string);
            }
        }

        @Override
        public long getItemId(int position) {
            return incomingBundle.getLong(GeoDataSource.ATTR_MAP.get(0).string);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View newView;
            LayoutInflater inflater = getLayoutInflater();

            if (convertView == null) {
                newView = inflater.inflate(R.layout.geo_city_attribute_row, parent, false);
            }
            else newView = convertView;

            TextView geoTxtViewCityAttrName = newView.findViewById(R.id.geoTxtViewCityAttrName);
            geoTxtViewCityAttrName.setText(getString(GeoDataSource.ATTR_MAP.get(position).resID));

            TextView geoTxtViewCityAttrValue = newView.findViewById(R.id.geoTxtViewCityAttrValue);
            geoTxtViewCityAttrValue.setText((String) getItem(position));

            return newView;
        }
    }
}