package com.example.daniel.aroundme.activities_and_fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.database.Constants;

import java.util.Locale;

/**
 * Created by Daniel on 2/1/2016.
 */

// Preferences fragment to change settings
public class PrefrencesFragment extends Fragment {


    private CheckBox km;
    private CheckBox miles;
    private ImageButton waze;
    private ImageButton maps;

    private SharedPreferences languagePreferences;
    private SharedPreferences defaultUnitPrefernces;
    private SharedPreferences.Editor edit2;
    private SharedPreferences.Editor edit;
    private int defaultU;
    public static final String FLAG_BROADCAST = "Pref";

    private Comunnicator comunnicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prefrences, container, false);

        km = (CheckBox)v.findViewById(R.id.km_checkBox);
        miles = (CheckBox)v.findViewById(R.id.miles_checkBox);
        waze = (ImageButton)v.findViewById(R.id.waze_imageButton);
        maps = (ImageButton)v.findViewById(R.id.google_maps_imageButton);
        comunnicator = (Comunnicator)getActivity();


        //using sharedPreferences for language and default unit
        languagePreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        defaultUnitPrefernces = PreferenceManager.getDefaultSharedPreferences(getActivity());
         edit2 = languagePreferences.edit();
//        defaultU = defaultUnitPrefernces.getInt("default", 1);

//        defaultUnitPrefernces = getActivity().getSharedPreferences("my_preferences", 0x0000);
        edit = defaultUnitPrefernces.edit();




        km.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                miles.setChecked(false);
                edit.putInt("default", 1);

                edit.commit();
//                comunnicator.defaultUnit(Constants.DISTANCE_IN_KM);
                Intent intent = new Intent(FLAG_BROADCAST);
                intent.putExtra("un", 1);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });

        miles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                km.setChecked(false);
                edit.putInt("default", 2);

                edit.commit();
//                comunnicator.defaultUnit(Constants.DISTANCE_IN_MILES);

                Intent intent = new Intent(FLAG_BROADCAST);
                intent.putExtra("un", 2);

                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            }
        });

        waze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String url = "waze://?q=Hawaii";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }

            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });



    return v;


    }

    public void  change(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }
}
