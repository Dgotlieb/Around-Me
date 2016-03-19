package com.example.daniel.aroundme.services_and_recievers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.Widget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Daniel on 12/25/2015.
 */

//this service is running in background to recieve user location once every hour and change widget accordingly.
// look at: SIXTY_MINUTES
public class LocationService extends Service {
    private Context context = this;
    private static final int SIXTY_MINUTES = 1000 * 60 * 60;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    public static final String FLAG_BROADCAST = "LocationService";
    private Intent intent;
    private String errorMessage = "";

    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(FLAG_BROADCAST);
        try {


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            ComponentName componentName = new ComponentName(context, Widget.class);
            remoteViews.setTextViewText(R.id.widgettextView, "Connecting...");
            appWidgetManager.updateAppWidget(componentName, remoteViews);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onStart(Intent intent, int startId) {


        try {


            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, SIXTY_MINUTES, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SIXTY_MINUTES, 0, listener);
        }catch (Exception e){
            Log.d("location_service","getting location manager");

            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > SIXTY_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -SIXTY_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        Log.v("STOP_SERVICE", "DONE");

//        locationManager.removeUpdates(listener);

//        Intent in = new Intent();
//        in.setAction("StartkilledService");
//        sendBroadcast(in);
//        Log.d("Service Killed", "YouWillNeverKillMe");
        sendBroadcast(new Intent("YouWillNeverKillMe"));
        super.onDestroy();

    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }




    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("*********", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                fetch(loc);

                LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);

            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        public void fetch(Location location){
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            // Address found using the Geocoder.
            List<Address> addresses = null;

            try {
                // Using getFromLocation() returns an array of Addresses for the area immediately
                // surrounding the given latitude and longitude. The results are a best guess and are
                // not guaranteed to be accurate.
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // In this sample, we get just a single address.
                        1);
            } catch (IOException ioException) {
                Log.d("location_service","I/O exception");

                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);

            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
                }
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }

                try {
                String street = addressFragments.get(0);
                String settelment = addressFragments.get(1);


                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
                    ComponentName componentName = new ComponentName(context, Widget.class);

                    remoteViews.setTextViewText(R.id.widgettextView, street + ", " + settelment);
                    remoteViews.setImageViewResource(R.id.widget_iv, 0);

                    remoteViews.setImageViewResource(R.id.widget_iv, R.drawable.anim);
                    remoteViews.setImageViewResource(R.id.refresh_widget, R.drawable.refresh_anim);


//                    remoteViews.setViewVisibility(R.id.refresh_widget, View.VISIBLE);
//                    remoteViews.setViewVisibility(R.id.widget_progressBar, View.INVISIBLE);

                    appWidgetManager.updateAppWidget(componentName, remoteViews);


                    appWidgetManager.updateAppWidget(componentName, remoteViews);
                }catch (Exception e){
                    Log.d("location_service_widget","crash_updating");

                    e.printStackTrace();
                }
            }
        }
        }


}