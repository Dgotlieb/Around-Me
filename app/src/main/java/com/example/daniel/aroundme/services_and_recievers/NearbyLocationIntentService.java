package com.example.daniel.aroundme.services_and_recievers;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.example.daniel.aroundme.database.Constants;
import com.example.daniel.aroundme.database.DBHandler;
import com.example.daniel.aroundme.database.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NearbyLocationIntentService extends IntentService {

    private static final String ACTION_NEARBY = "com.example.daniel.aroundme.action.nearby";
    public static final String FLAG_BROADCAST = "NearbyLocationIntentService";
    private static final double EXTRA_LAT = 0;
    private static final double EXTRA_LNG = 0;
    private static final double DISTANCE = 0;
    private static final String SPINNER_CHOICE = "";


    public static void startActionNearbySearch(Context context, double lat, double lng, double distance, String spinnerChoice, double userLat, double userLng) {
        Intent intent = new Intent(context, NearbyLocationIntentService.class);
        intent.setAction(ACTION_NEARBY);
        intent.putExtra(Constants.LAT, lat);
        intent.putExtra(Constants.LNG, lng);
        intent.putExtra(Constants.DISTANCE, distance);
        intent.putExtra(Constants.SPINNER_CHOICE, spinnerChoice);

        intent.putExtra("userlat", userLat);
        intent.putExtra("userlng", userLng);
        context.startService(intent);
    }


    public NearbyLocationIntentService() {
        super("NearbyLocationIntentService");
    }

    //This method is invoked on the worker thread with a request to process.

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEARBY.equals(action)) {
                final double lat = intent.getDoubleExtra(Constants.LAT, EXTRA_LAT);
                final double lng = intent.getDoubleExtra(Constants.LNG, EXTRA_LNG);
                final double distance = intent.getDoubleExtra(Constants.DISTANCE, DISTANCE);
                final String choice = intent.getStringExtra(Constants.SPINNER_CHOICE);
                final double userlat = intent.getDoubleExtra("userlat", 0);
                final double userlng = intent.getDoubleExtra("userlng", 0);

                handleActionFoo(lat, lng, distance, choice, userlat, userlng);


            }
        }
    }

    //getting the places json using the api key
    private void handleActionFoo(double lat, double lng, double distance, String choice, double userlat, double userlng) {

        DBHandler handler = new DBHandler(this);
        handler.removeAllLocations();


        String url[] = {"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=" + distance + "&types=" + choice + "&key=AIzaSyDgYYu3M2s-FZgVAp7xawEWjPnsnr24gFE"};
        try {

            String response = sendHttpRequest(url[0]);
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray(Constants.RESULTS);


            for (int i = 0; i < array.length(); i++) {
                JSONObject geometry = array.getJSONObject(i).getJSONObject(Constants.GEOMETRY);
                JSONObject location = geometry.getJSONObject(Constants.LOCATION);
                double latitude = location.getDouble(Constants.LAT);
                double longitude = location.getDouble(Constants.LNG);
                String icon = array.getJSONObject(i).getString(Constants.COLUMN_ICON);
                String id = array.getJSONObject(i).getString(Constants.COLUMN_ID);
                String name = array.getJSONObject(i).getString(Constants.COLUMN_NAME);

                String photo = Constants.COLUMN_PHOTOS;
                int width = 0;
                int height = 0;
                String typesA;
                String typesB;
                String typesC;


                if (array.getJSONObject(i).has(Constants.COLUMN_PHOTOS)) {
                    JSONArray photos = array.getJSONObject(i).getJSONArray(Constants.COLUMN_PHOTOS);
                    photo = photos.getJSONObject(0).getString(Constants.COLUMN_PHOTO_REFERENCE);
                    width = photos.getJSONObject(0).getInt(Constants.COLUMN_IMAGE_WIDTH);
                    height = photos.getJSONObject(0).getInt(Constants.COLUMN_IMAGE_HEIGHT);

                }


                String opening_hours = Constants.COLUMN_OPENING_HOURS;
                if (array.getJSONObject(i).has(Constants.COLUMN_OPENING_HOURS)) {
                    JSONObject opening = array.getJSONObject(i).getJSONObject(Constants.COLUMN_OPENING_HOURS);
                    opening_hours = opening.getString("open_now");
                }

                String place_id = array.getJSONObject(i).getString(Constants.COLUMN_PLACE_ID);
                String reference = array.getJSONObject(i).getString(Constants.COLUMN_REFERENCE);

                String types = Constants.COLUMN_TYPES;
                if (array.getJSONObject(i).has(Constants.COLUMN_TYPES)) {
                    JSONArray type = array.getJSONObject(i).getJSONArray(Constants.COLUMN_TYPES);
                    typesA = type.getString(0);
                    typesB = type.getString(1);
                    typesC = type.getString(2);
                    types = typesA + ", " + typesB + ", " + typesC;
                }


                String vicinity = array.getJSONObject(i).getString(Constants.COLUMN_VICINITY);

                Location loc = new Location("a");
                loc.setLatitude(userlat);
                loc.setLongitude(userlng);

                Location location_b = new Location("b");
                location_b.setLatitude(latitude);
                location_b.setLongitude(longitude);

                double distance_km = (int) loc.distanceTo(location_b) / 1000;
                double distance_miles = (int) loc.distanceTo(location_b) / 1000 * 1.60934;


                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + photo + "&sensor=false&maxheight=" + String.valueOf(height) + "&maxwidth=" + String.valueOf(width) + "&key=AIzaSyDgYYu3M2s-FZgVAp7xawEWjPnsnr24gFE";


                //adding place to db and sending the broadcast to main
                Place place = new Place(latitude, longitude, icon, id, name, opening_hours, photoUrl, place_id, reference, types, vicinity);
                handler.addPlace(place);

                Intent intent = new Intent(FLAG_BROADCAST);

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


    private String sendHttpRequest(String urlString) {
        BufferedReader input = null;
        HttpURLConnection httpCon = null;
        InputStream input_stream = null;
        InputStreamReader input_stream_reader = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            httpCon = (HttpURLConnection) url.openConnection();
            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            input_stream = httpCon.getInputStream();
            input_stream_reader = new InputStreamReader(input_stream);
            input = new BufferedReader(input_stream_reader);
            String line;
            while ((line = input.readLine()) != null) {
                response.append(line + "\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input_stream_reader.close();
                    input_stream.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpCon != null) {
                    httpCon.disconnect();
                }
            }
        }
        return response.toString();


    }


}
