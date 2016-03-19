package com.example.daniel.aroundme.activities_and_fragments;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.database.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaClickListener;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaLongClickListener;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Timestamp;
import java.sql.Time;

/**
 * Created by Daniel on 2/9/2016.
 */

//streetView activity
public class StreetView extends AppCompatActivity  implements OnStreetViewPanoramaChangeListener, OnStreetViewPanoramaCameraChangeListener,
        OnStreetViewPanoramaClickListener, OnStreetViewPanoramaLongClickListener {


    private static  LatLng PLACE ;

    private StreetViewPanorama mStreetViewPanorama;



    private int mPanoChangeTimes = 0;

    private int mPanoCameraChangeTimes = 0;

    private int mPanoClickTimes = 0;

    private int mPanoLongClickTimes = 0;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strret_view);


        Intent intent = getIntent();
        if (intent!=null) {
            double lat = intent.getDoubleExtra(Constants.LAT, 0);
            double lng = intent.getDoubleExtra(Constants.LNG, 0);

            PLACE = new LatLng(lat, lng);
        }




        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        mStreetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                StreetView.this);
                        mStreetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(
                                StreetView.this);
                        mStreetViewPanorama.setOnStreetViewPanoramaClickListener(
                                StreetView.this);
                        mStreetViewPanorama.setOnStreetViewPanoramaLongClickListener(
                                StreetView.this);


                        if (savedInstanceState == null) {
                            mStreetViewPanorama.setPosition(PLACE);
                        }
                    }
                });
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        if (location != null) {

            Log.d("Times panorama changed=", String.valueOf(++mPanoChangeTimes));

        }
    }



    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
        Log.d("Times camera changed=",String.valueOf(++mPanoCameraChangeTimes));
    }

    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {
        Point point = mStreetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
            mPanoClickTimes++;
            Log.d("Times clicked=", String.valueOf(mPanoClickTimes + " : " + point.toString()));

            mStreetViewPanorama.animateTo(
                    new StreetViewPanoramaCamera.Builder()
                            .orientation(orientation)
                            .zoom(mStreetViewPanorama.getPanoramaCamera().zoom)
                            .build(), 1000);
        }
    }

    @Override
    public void onStreetViewPanoramaLongClick(StreetViewPanoramaOrientation orientation) {
        Point point = mStreetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
            mPanoLongClickTimes++;
            Log.d("Times long clicked==", String.valueOf(mPanoLongClickTimes + " : " + point.toString()));

        }
    }
    }

