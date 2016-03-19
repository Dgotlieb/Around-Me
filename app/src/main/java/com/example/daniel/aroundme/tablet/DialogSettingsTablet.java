package com.example.daniel.aroundme.tablet;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
import com.example.daniel.aroundme.database.Constants;
import com.example.daniel.aroundme.database.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Daniel on 12/20/2015.
 */
public class DialogSettingsTablet extends android.support.v4.app.DialogFragment {
//
//    private CheckBox hebrew;
//    private CheckBox english;
    private CheckBox km;
    private CheckBox miles;
    private ImageButton waze;
    private ImageButton maps;

    private SharedPreferences languagePreferences;
    private SharedPreferences defaultUnitPrefernces;
    private SharedPreferences.Editor edit2;
    private SharedPreferences.Editor edit;
    private Comunnicator comunnicator;
    public static final String FLAG_BROADCAST = "Pref";




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prefrences, container, false);

//        hebrew = (CheckBox)v.findViewById(R.id.hebrew_checkBox);
//        english =(CheckBox)v.findViewById(R.id.english_checkBox);
        km = (CheckBox)v.findViewById(R.id.km_checkBox);
        miles = (CheckBox)v.findViewById(R.id.miles_checkBox);
        waze = (ImageButton)v.findViewById(R.id.waze_imageButton);
        maps = (ImageButton)v.findViewById(R.id.google_maps_imageButton);
        waze.setVisibility(View.GONE);

        languagePreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        defaultUnitPrefernces = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edit2 = languagePreferences.edit();
        edit = defaultUnitPrefernces.edit();
        comunnicator = (Comunnicator)getActivity();




//        hebrew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                english.setChecked(false);
////                edit2.putInt("lang", 8);
////                change("iw");
////                edit2.apply();
//                Toast.makeText(getActivity(), "Language options will be available soon", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                hebrew.setChecked(false);
////                edit2.putInt("lang", 9);
////                change("en");
////                edit2.apply();
//
//                Toast.makeText(getActivity(), "Language options will be available soon", Toast.LENGTH_SHORT).show();
//
//            }
//        });

        km.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                miles.setChecked(false);
                edit.putInt("default", 1);

                edit.apply();

                Intent intent = new Intent(FLAG_BROADCAST);
                intent.putExtra("un",1);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            }
        });

        miles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                km.setChecked(false);
                edit.putInt("default", 2);

                edit.apply();

                Intent intent = new Intent(FLAG_BROADCAST);
                intent.putExtra("un",2);

                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            }
        });

//        waze.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    String url = "waze://?q=Hawaii";
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                } catch (ActivityNotFoundException ex) {
//                    Intent intent =
//                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
//                    startActivity(intent);
//                }
//
//            }
//        });

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