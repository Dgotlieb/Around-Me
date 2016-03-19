package com.example.daniel.aroundme.activities_and_fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.database.Constants;
import com.example.daniel.aroundme.database.DBHandler;
import com.example.daniel.aroundme.database.Place;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by Daniel on 12/8/2015.
 */
public class FragmentMaps extends Fragment implements StreetViewPanorama.OnStreetViewPanoramaChangeListener {
    private Toolbar toolbar;
    private GoogleMap myGoogleMap;
    private SupportMapFragment supportMapFragment;
    private LatLng my_location;
    private double lat;
    private double lng;
    private LatLng location;
    private String place_name;
//    private ImageButton satellite;
//    private ImageButton regular_show;
//    private ImageButton traffic;
    private ArrayList<Place> places;
    private ArrayList<LatLng> latLngs;
    private static final int REQUEST_LOCATION = 1;
    private int multi = 0;
    private boolean isMulti = false;
    private final long interval = 1 * 1000;
    private final long startTime = 30 * 1000;
    private boolean timerHasStarted = false;
    private Button takeMe;
    private Place place;
    private ImageView image;
    private TextView vicinity;
    private LatLng ll;
    private String vicinity_string;
    private String photo;
    private CountDownTimer timer;
    private ImageButton streetView;
    private SharedPreferences intro;
    private View v;
    private Bitmap bitmap;
    private boolean toNavigate = false;
    private Vibrator vibrator;
    private ImageButton screenShot;
    private SharedPreferences.Editor edit;
//    private ImageButton clearMap;
    private ArrayList <File>inFiles;
    private Bitmap bmp;
    private FabSpeedDial fabSpeedDial;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_maps, container, false);


        toolbar = (Toolbar) v.findViewById(R.id.mapsBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        try {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        satellite = (ImageButton) v.findViewById(R.id.maps_toolbar_satellite);
//        traffic = (ImageButton) v.findViewById(R.id.maps_toolbar_traffic);
//        regular_show = (ImageButton) v.findViewById(R.id.maps_toolbar_regular_show);
        streetView = (ImageButton) v.findViewById(R.id.toolbat_street_view);
        screenShot = (ImageButton) v.findViewById(R.id.toolbar_screenshot);
//        clearMap = (ImageButton) v.findViewById(R.id.toolbar_clear);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);


         fabSpeedDial = (FabSpeedDial) v.findViewById(R.id.fab_sheet);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }
        });

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_regular) {
                    regularShow();

                    return true;

                }

                if (menuItem.getItemId() == R.id.action_satellite) {
                    satelliteShow();

                    return true;
                }
                if (menuItem.getItemId() == R.id.action_traffic) {
                        traffic(1);

                    return true;
                }
                return true;


            }
        });

//
//        clearMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myGoogleMap.clear();
//            }
//        });



        //taking map view screenshot, and saving it in phone if user choose to
        screenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());

                alertDialog.setTitle("Screen Shot");

                Drawable shot = new BitmapDrawable(getResources(), bitmap);
                alertDialog.setMessage("what would you like to do?");
                alertDialog.setIcon(shot);


                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                            Bitmap bitmap;

                            @Override
                            public void onSnapshotReady(Bitmap snapshot) {
                                bitmap = snapshot;
                                try {
                                    long time = System.currentTimeMillis();

                                    File imagePath = new File(Environment.getExternalStorageDirectory() + "/" + String.valueOf(time) + ".png");

                                    FileOutputStream out = new FileOutputStream(imagePath);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        myGoogleMap.snapshot(callback);
                        Snackbar.make(v, "Screen shot was saved successfully", Snackbar.LENGTH_LONG).show();

                    }

                });


                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                );

                alertDialog.show();

            }
        });

//        traffic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                traffic(1);
//            }
//        });
//
//
//        satellite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                satelliteShow();
//
//            }
//        });

        streetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comunnicator comunnicator = (Comunnicator) getActivity();
                comunnicator.streetView(lat, lng);
            }
        });

//        regular_show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                regularShow();
//
//            }
//        });

        //recieving info from about place

        try {

            Bundle bundle = getArguments();
            if (bundle != null) {
                multi = bundle.getInt("int");
                if (multi == 5) {
                    isMulti = true;
                    toNavigate = true;

                } else


                    streetView.setVisibility(View.VISIBLE);
                lat = bundle.getDouble(Constants.COLUMN_LAT);
                lng = bundle.getDouble(Constants.COLUMN_LNG);
                place_name = bundle.getString(Constants.PLACE_NAME_FROM_JSON);
                vicinity_string = bundle.getString(Constants.COLUMN_VICINITY);
                photo = bundle.getString(Constants.COLUMN_PHOTOS);
                location = new LatLng(lat, lng);
                int x = bundle.getInt(Constants.TRAFFIC, 0);

                if (place_name.equals("You are here")) {
                    toNavigate = false;
                } else {
                    toNavigate = true;

                }

                if (x == 1) {
                    myGoogleMap.setTrafficEnabled(true);
                }
                my_location = new LatLng(lat, lng);
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        return v;

    }

    //show map with traffic updates
    public void traffic(int x) {

        if (x == 1) {
            myGoogleMap.setTrafficEnabled(true);

        }

    }

    //show map in satellite view
    public void satelliteShow() {
        try {


            myGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //show map in regular view
    public void regularShow() {
        try {
            myGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();


        }
    }

    @Override
    public void onResume() {


        super.onResume();
        try {
            myGoogleMap = supportMapFragment.getMap();


            if (myGoogleMap != null) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                //show  user location (blue dot) on map
                myGoogleMap.setMyLocationEnabled(true);
                //show button so user gets back to his location .
                myGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                //show compass on map

                myGoogleMap.getUiSettings().setCompassEnabled(true);
                if (isMulti) {
                    multipleMarks();
                }


            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            if (location.longitude != 0.0 || location.latitude != 0.0) {
                if (place_name.equals("You are here")) {
                    place_name = "";
                }
                myGoogleMap.addMarker(new MarkerOptions().position(location).title(place_name));
                myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));

                //this function lets user press on the marker (the place he choose)
                // and see dialog on map with all the place info
                //and navigate there with waze

                myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {


                        if (toNavigate) {
                            int ot = getResources().getConfiguration().orientation;

//                            if (ot != 2) {

                                try {


                                    boolean imageExist = false;
                                    File sd = Environment.getExternalStorageDirectory();
                                    File test = new File(sd + "/PlacesFolder/");
                                    getListFiles(test);
                                    for (int i = 0; i < inFiles.size(); i++) {
                                        if (inFiles.get(i).getName().equals(place_name + ".jpg")) {
                                            imageExist = true;

                                        }
                                    }
                                    if (imageExist) {
                                        File f = new File(sd + "/PlacesFolder/" + place_name + ".jpg");
                                        bmp = BitmapFactory.decodeFile(f.getAbsolutePath());


                                    } else {

                                        DownloadImageTask downloadTask = new DownloadImageTask(getActivity());
                                        downloadTask.execute(photo);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
//                            }
                        }
                        final Dialog markerDialog = new Dialog(getActivity());


                        markerDialog.setContentView(R.layout.marker_dialog);
                        markerDialog.setTitle(place_name);
                        vicinity = (TextView) markerDialog.findViewById(R.id.place_vicinity_textView);
                        vicinity.setText(vicinity_string);
                        image = (ImageView) markerDialog.findViewById(R.id.place_image_dialog);
                        takeMe = (Button) markerDialog.findViewById(R.id.take_me_button);
                        image.setImageBitmap(bmp);



                        markerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                timer.cancel();

                            }
                        });


                        try {


                            //timer is set to 10 seconds when time is up/user click on navigate button it starts navigating
                            timer = new CountDownTimer(10000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    takeMe.setText("Take me there: " + millisUntilFinished / 1000);
                                }

                                public void onFinish() {

                                    try {

                                        //vibration is added when time is up/user click on navigate button.
                                        vibrator.vibrate(300);

                                        String uri = "geo: " + location.latitude + "," + location.longitude;
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(uri)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Button cancel = (Button) markerDialog.findViewById(R.id.cancel_button);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {


                                    timer.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                markerDialog.dismiss();
                            }
                        });

                        takeMe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    vibrator.vibrate(300);

                                    String uri = "geo: " + location.latitude + "," + location.longitude;
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(uri)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        if (toNavigate) {
                            markerDialog.show();
                        } else {
                            try {
                                timer.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        return false;
                    }
                });
            }
        } catch (IllegalArgumentException | NullPointerException l) {
            l.printStackTrace();
        }
    }

    private List<File> getListFiles(File parentDir) {
        inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".jpg")){
                    inFiles.add(file);
                }
            }
        }
        return inFiles;

    }


    //this method is running in a for loop, to get all places from list, and add they're markers on map
    public void multipleMarks() {
        streetView.setVisibility(View.GONE);


        if (myGoogleMap == null)
            myGoogleMap = supportMapFragment.getMap();
        DBHandler handler = new DBHandler(getActivity());
        places = handler.showAllLocationsArrayListAndSort();

        for (int i = 0; i < places.size(); i++) {

            ll = new LatLng(places.get(i).getLatitude(), places.get(i).getLongitude());

            place = places.get(i);


            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());

            myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
        }
        myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 14));
        myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                return false;
            }
        });


    }


    //explains user the buttons when press help
    public void introduction() throws Exception {
        intro = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean isFirst = intro.getBoolean("First_ map", true);

        if (isFirst) {


            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(getActivity());
            showCaseBuilder.setStyle(R.style.introduction);
            showCaseBuilder.setTarget(new ViewTarget(((ImageButton) v.findViewById(R.id.toolbar_screenshot))));
            showCaseBuilder.setContentTitle("Screen shot");
            showCaseBuilder.setContentText("Take a screenShot of what is on the map");


            showCaseBuilder.build();
            showCaseBuilder.setShowcaseEventListener(new OnShowcaseEventListener() {
                @Override
                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                    ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(getActivity());

                    showCaseBuilder.setTarget(new ViewTarget(((FabSpeedDial) v.findViewById(R.id.fab_sheet))));
                    showCaseBuilder.setContentTitle("Map views");

                    showCaseBuilder.setContentText("Choose how do you want  \n" +
                            " to see the map");

                    showCaseBuilder.build();

                }


                @Override
                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseViewShow(ShowcaseView showcaseView) {

                }
            });

            edit = intro.edit();

            edit.putBoolean("First_ map", false);
            // TODO: 2/5/2016
//            edit.putBoolean("First_start", false);
            //// TODO: 2/5/2016


            edit.apply();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.help_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.help) {


            try {

                intro = PreferenceManager.getDefaultSharedPreferences(getActivity());


                edit = intro.edit();

                edit.putBoolean("First_ map", true);
                edit.apply();
                introduction();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

    }


    public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

        //        private Activity mActivity;
//        private ProgressDialog mDialog;

        DownloadImageTask(Activity activity) {
//            mActivity = activity;
//            mDialog = new ProgressDialog(getContext());
        }

        //Code performing long running operation goes in this method.  When onClick method is executed on click of button,
        // it calls execute method which accepts parameters and automatically calls
        // doInBackground method with the parameters passed.
        protected Bitmap doInBackground(String... urls) {
            Log.d("doInBackground", "starting download of image");
            Bitmap image = downloadImage(urls[0]);
            return image;
        }

        //This method is called before doInBackground method is called.
        protected void onPreExecute() {


            try {

                image.setImageBitmap(null);
            } catch (NullPointerException n) {
                n.printStackTrace();
            }

        }

        //This method is invoked by calling publishProgress anytime from doInBackground call this method.
        protected void onProgressUpdate(Integer... progress) {

        }

        //This method is called after doInBackground method completes processing.
        // Result from doInBackground is passed to this method.
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                try {


                    image.setImageBitmap(result);


                } catch (NullPointerException n) {
                    n.printStackTrace();
                }


            }


        }


        private Bitmap downloadImage(String urlString) {
            java.net.URL url;
            try {
                url = new URL(urlString);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                InputStream is = httpCon.getInputStream();
                int fileLength = httpCon.getContentLength();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead, totalBytesRead = 0;
                byte[] data = new byte[2048];
//                mDialog.setMax(fileLength);
                // Read the image bytes in chunks of 2048 bytes
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                    totalBytesRead += nRead;
                    publishProgress(totalBytesRead);
                }
                buffer.flush();
                byte[] image = buffer.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}


