package com.example.daniel.aroundme.database;

/**
 * Created by Daniel on 9/18/2015.
 */
public class Constants {

    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "PlacesListDB.db";
    public final static String DATABASE_FAVORITES = "PlacesFavoritesDB.db";

    public final static String TABLE_NAME = "Places";
    public final static String COLUMN_LNG = "longitude";
    public final static String COLUMN_LAT = "latitude";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_ICON = "icon";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_OPENING_HOURS = "opening_hours";
    public final static String COLUMN_PHOTOS = "photos";
    public final static String COLUMN_PLACE_ID = "place_id";
    public final static String COLUMN_REFERENCE = "reference";
    public final static String COLUMN_TYPES = "types";
    public final static String COLUMN_VICINITY = "vicinity";
    public final static String COLUMN_PHOTO_REFERENCE = "photo_reference";
    public final static String COLUMN_IMAGE_URL = "imageUrl";
    public final static String COLUMN_IMAGE_WIDTH = "width";
    public final static String COLUMN_IMAGE_HEIGHT = "height";


    public final static String RESULTS = "results";
    public final static String GEOMETRY = "geometry";
    public final static String LOCATION = "location";
    public final static String LAT = "lat";
    public final static String LNG = "lng";
    public final static String DISTANCE = "distance";
    public final static String DISTANCE_IN_KM = "distance_in_km";

    public final static String DISTANCE_IN_MILES = "distance_in_miles";

    public final static String SPINNER_CHOICE = "spinnerChoice";
    public final static String WORDS = "words";
    public final static String TRAFFIC = "traffic";
    public final static String PLACE_NAME_FROM_JSON = "place_name";
    public final static String PLACE = "place";
    public final static String KEYWORD = "keyword";
    public final static String NEARBY = "nearby";
    public final static String STOP_PROGRESS = "stop_progress";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";


}
