package com.example.daniel.aroundme.activities_and_fragments;


import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.Widget;
import com.example.daniel.aroundme.database.Constants;
import com.example.daniel.aroundme.database.DBFabHandler;
import com.example.daniel.aroundme.database.DBHandler;
import com.example.daniel.aroundme.database.Place;
import com.example.daniel.aroundme.introduction.Introduction;
import com.example.daniel.aroundme.services_and_recievers.LocationService;
import com.example.daniel.aroundme.tablet.Fragment_list_tablet;
import com.example.daniel.aroundme.tablet.Fragment_maps_tablet;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;
import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.Locale;

//import android.support.v4.widget.DrawerLayout;


public class MainActivity extends AppCompatActivity implements Comunnicator {
    private static final int REQUEST_LOCATION = 1;


    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    protected LocationManager locationManager;
    private SharedPreferences languagePreferences;
    private SharedPreferences defaultUnitPrefernces;
    private SharedPreferences introPref;
    private SharedPreferences permissionPref;


    private SharedPreferences permission;
    private static final int REQUEST_STORAGE = 2;
    private static final int REQUEST_SHARE = 3;
    private Tracker tracker;
    private int b = 0;
    public static GoogleAnalytics analytics;
    private SharedPreferences.Editor edit;
    private boolean isFirst;
    private boolean needIntro;
    private boolean needPermission;
    private boolean inFavourites;
    ViewPager pager;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_main);


        pager= (ViewPager) findViewById(R.id.view_pager);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);

        FragmentManager manager=getSupportFragmentManager();

        //object of PagerAdapter passing fragment manager object as a parameter of constructor of PagerAdapter class.
        PagerAdapter adapter=new PagerAdapter(manager);

        //set Adapter to view pager
        pager.setAdapter(adapter);

        //set tablayout with viewpager
        tabLayout.setupWithViewPager(pager);

        // adding functionality to tab and viewpager to manage each other when a page is changed or when a tab is selected
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Setting tabs from adpater
        tabLayout.setTabsFromPagerAdapter(adapter);


        try{
            Mint.initAndStartSession(MainActivity.this, "877dec55");
        }catch (Exception e){
            e.printStackTrace();
        }

        //checking if its a new user to introduce the app
        introPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        needIntro = introPref.getBoolean("Need Intro", true);
        if (needIntro) {
            Intent intro = new Intent(MainActivity.this, Introduction.class);
            startActivity(intro);
            edit = introPref.edit();
            edit.putBoolean("Need Intro", false);
            edit.apply();
            addShortcut();
            needIntro = false;


        }

        //checking if user press on favourites home screen shortcut, so he gets to favourites fragment and not the main
        if (getIntent().getIntExtra("a", 0) == 100) {
            b = 1;
            Favorites favorites = (Favorites) getSupportFragmentManager().findFragmentByTag("favourites");
            favorites = new Favorites();
            FragmentManager fm = getSupportFragmentManager();
            inFavourites = true;
            fm.beginTransaction().replace(R.id.small_device, favorites, "favourites").commit();


        }

        //initialize google analytics tracker
        tracker = this.getDefaultTracker();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker("UA-73429639-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);


        if (getIntent().getBooleanExtra("LOGOUT", false)) {
            finish();
        }

        languagePreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        defaultUnitPrefernces = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        sharedFrefs();


        // starts location service in case home screen widget service is nor running
        int ids[] = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, Widget.class));

        if (ids.length > 0 && !isServiceRunning()) {
            try {


                Intent in = new Intent(this, LocationService.class);
                this.startService(in);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//
//        if (findViewById(R.id.small_device) != null && !inFavourites) {
//            FragmentList fragmentList = (FragmentList) getSupportFragmentManager().findFragmentByTag("list");
//
//            if (fragmentList == null) {
//                fragmentList = new FragmentList();
//                FragmentManager fm = getSupportFragmentManager();
//                fm.beginTransaction().add(R.id.small_device, fragmentList, "list").commit();
//
//            }
//        } else {
//        }
//
//
//
    }


    public void sharedFrefs() {

        //asking the user to pick background color to show up in next runs
        isFirst = defaultUnitPrefernces.getBoolean("First_time", true);
        if (isFirst) {


            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Default Unit");
            builder.setMessage("which default unit would you like?");
            builder.setPositiveButton("KM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor edit = defaultUnitPrefernces.edit();
                    edit.putInt("default", 1);

                    edit.apply();

                }
            });


            builder.setNegativeButton("Miles", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor edit = defaultUnitPrefernces.edit();
                    edit.putInt("default", 2);

                    edit.apply();
                }
            });
//            builder.show();


            SharedPreferences.Editor edit = defaultUnitPrefernces.edit();

            edit.putBoolean("First_time", false);

            edit.apply();

//            Snackbar.show(MainActivity.this).text(getResources().getString(R.string.colorsChange)).duration(2000));
        }
        int defaultUnitPreferncesInt = defaultUnitPrefernces.getInt("default", 0);
        int languageInt = languagePreferences.getInt("language", 9);


        if (languageInt == 8) {
            change("iw");
        }

        if (languageInt == 9) {
            change("en");
        }

        SharedPreferences.Editor edit2 = languagePreferences.edit();

        edit2.apply();


    }

    // checks if location service is running .
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.daniel.aroundme.services_and_recievers.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    @Override
    protected void onDestroy() {
        try {

            //using google analytics tracker to track onDestroy
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("OnDestroy is running")
                    .setAction("onDestroy")
                    .setLabel("OnDestroy is running")

                    .build());
            Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                    tracker,                                        // Currently used Tracker.
                    Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                    this);
            // Context of the application.

// Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(myHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //this method add places to map

    @Override
    public void locationPicker(double latitude, double longitude, String place_name, String vicinity, String image) {
        FragmentManager manager = getSupportFragmentManager();
//        FragmentMaps fb = (FragmentMaps) manager.findFragmentById(R.id.fragment_map);
        FragmentMaps fragmentMaps = (FragmentMaps) manager.findFragmentById(R.id.fragmentMaps);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (isGPSEnabled || isNetworkEnabled) {
            if (fragmentMaps == null) {
                fragmentMaps = new FragmentMaps();

                Bundle bundle = new Bundle();
                bundle.putDouble(Constants.COLUMN_LAT, latitude);
                bundle.putDouble(Constants.COLUMN_LNG, longitude);
                bundle.putString(Constants.PLACE_NAME_FROM_JSON, place_name);
                bundle.putString(Constants.COLUMN_VICINITY, vicinity);
                bundle.putString(Constants.COLUMN_PHOTOS, image);
                fragmentMaps.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.small_device, fragmentMaps, "F2").addToBackStack("F2").commit();
            }
        } else {
            showSettingsAlert();
        }
    }

    @Override
    public void locationTabletPicker(double latitude, double longitude, String place_name, String vicinity, String photos) {


        Fragment_maps_tablet maps_tablenew = (Fragment_maps_tablet) getSupportFragmentManager().findFragmentById(R.id.fragment3);
        maps_tablenew.addNew(latitude, longitude, place_name, vicinity);

    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {

        if (findViewById(R.id.tablet) != null) {
            super.onBackPressed();

        } else
            try {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
            } catch (Exception e) {
                super.onBackPressed();

                e.printStackTrace();
            }
    }

    //go to favourites fragment
    @Override
    public void favourites(Place place) {
        Favorites fav = new Favorites();
        FragmentManager manager = getSupportFragmentManager();

        DBFabHandler db = new DBFabHandler(MainActivity.this);
        db.addPlace(place);
        manager.beginTransaction().replace(R.id.small_device, fav, "fav").addToBackStack("fav").commit();


    }

    @Override
    public void goToFavo(double lat, double lng) {
        Favorites fav = new Favorites();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat",lat);
        bundle.putDouble("lng",lng);
        fav.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.small_device, fav, "fav").addToBackStack("fav").commit();

    }


    //go to settings fragment

    @Override
    public void prefs(double lat, double lng) {

            PrefrencesFragment prefsFragment = new PrefrencesFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble("lat",lat);
            bundle.putDouble("lng",lng);
            prefsFragment.setArguments(bundle);


            getSupportFragmentManager().beginTransaction().replace(R.id.small_device, prefsFragment, "prefs").addToBackStack("prefs").commit();



    }


    @Override
    public void multiTab() {
        Fragment_maps_tablet maps_tablenew = (Fragment_maps_tablet) getSupportFragmentManager().findFragmentById(R.id.fragment3);
        maps_tablenew.multipleMarks();
    }


    @Override
    public void multi(int y) {
        if (y == 5) {
            FragmentManager manager = getSupportFragmentManager();

            FragmentMaps fragmentMaps = (FragmentMaps) manager.findFragmentById(R.id.fragmentMaps);


            if (fragmentMaps == null) {
                fragmentMaps = new FragmentMaps();

                Bundle bundle = new Bundle();
                bundle.putInt("int", 5);
                fragmentMaps.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.small_device, fragmentMaps, "F2").addToBackStack("F2").commit();

            }
        }
    }


    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }


    //change language
    public void change(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }


    //starts streetView (called from maps)
    @Override
    public void streetView(double lat, double lng) {

        Intent intent = new Intent(MainActivity.this, StreetView.class);
        LatLng place = new LatLng(lat, lng);
        intent.putExtra(Constants.LAT, lat);
        intent.putExtra(Constants.LNG, lng);
        startActivity(intent);

    }

    //not yet available
    @Override
    public void recommended() {
        RecommendedFragment recommendedFragment = new RecommendedFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.small_device, recommendedFragment, "recom").addToBackStack("recom").commit();


    }

    @Override
    public void permissionsAgreed() {

        Intent intent = new Intent(MainActivity.this, Introduction.class);
        startActivity(intent);
    }

    @Override
    public void noPermission() {
        FragmentPermissions permissions = new FragmentPermissions();
        FragmentManager manager = getSupportFragmentManager();

        if (findViewById(R.id.small_device) == null) {
            manager.beginTransaction().replace(R.id.tablet, permissions, "permissions").addToBackStack("permissions").commit();
        } else if (findViewById(R.id.small_device) != null) {
            manager.beginTransaction().replace(R.id.small_device, permissions, "permissions").addToBackStack("permissions").commit();

        }
    }

    @Override
    public void defaultUnit(String unit) {

        FragmentList list = new FragmentList();
        Bundle bundle = new Bundle();
        bundle.putString("default",unit);
        list.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.small_device,list,"list").commit();

    }

    @Override
    public void tabletDefaultUnit(String unit) {

        Fragment_list_tablet fragment_list_tablet = new Fragment_list_tablet();
        fragment_list_tablet.onStart();


    }

    public void fabClicked (View v){
        DBHandler handler = new DBHandler(MainActivity.this);
        ArrayList<Place> list;
        list = handler.showAllLocationsArrayListAndSort();

        if (!list.isEmpty()) {

            try {
                multi(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Adding shortcut for MainActivity
    //on Home screen
    private void addShortcut() {

        Intent shortcutIntent = new Intent(MainActivity.this, MainActivity.class);


        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Around Me");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(MainActivity.this,
                        R.drawable.asset));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        MainActivity.this.sendBroadcast(addIntent);
    }


}



