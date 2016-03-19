package com.example.daniel.aroundme.tablet;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.activities_and_fragments.DialogFragment;
import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
import com.example.daniel.aroundme.database.Constants;
import com.example.daniel.aroundme.database.DBHandler;
import com.example.daniel.aroundme.database.Place;
import com.example.daniel.aroundme.services_and_recievers.ConnectionChangeReceiver;
import com.example.daniel.aroundme.services_and_recievers.FetchAddressIntentService;
import com.example.daniel.aroundme.services_and_recievers.NearbyLocationIntentService;
import com.example.daniel.aroundme.services_and_recievers.TextSearchIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Daniel on 2/20/2016.
 */
public class Fragment_list_tablet extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button keywordButton;
    private Button nearbyButton;
    private EditText editSearch;
    private Spinner categorySpinner;
    private ImageButton searchButton;
    private ImageButton increaseDistanceButton;
    private ImageButton decreaseDistanceButton;
    private TextView distanceTV;
    private TextView tv;
        private ImageButton settings;
    private ListView placesList;
    private Comunnicator comm;
    private ListView lv;
    protected android.location.Location mLastLocation;
    protected GoogleApiClient mGoogleApiClient;
    private double defaultDistance;
    private ProgressBar progressBar;
    private AddressResultReceiver mResultReceiver;

    private Toolbar toolbar;
    String spinnerChoice;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    protected LocationManager locationManager;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected boolean mAddressRequested;
    protected String mAddressOutput;
    protected TextView mLocationAddressTextView;
        private ImageButton Exit;
    private double lat;
    private double distance;
    private double lng;
    private String search_source = Constants.KEYWORD;
    private Comunnicator comunnicator;
    private String words;
    private ArrayAdapter<Place> placeArrayAdapter;
    private ArrayList<Place> placeArray;
    private View v;
    private SwipeRefreshLayout swipeLayout;
    private android.support.v4.app.FragmentManager manager;
    private String imageUrl;
    private Dialog dialog;
        private ImageButton multipleMarkers;
    private ImageView imageView;
    private ImageButton buttonSpeak;
    private TextView keyword_example;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton clear_et;
    private SharedPreferences intro;
        private ImageButton favourites_button;
    private TextView default_unit_tv;
    private boolean hasConnection=true;
    private CountDownTimer timer;
    private Snackbar snackbar;
    private int flag;
    private SharedPreferences defaultShared;
    private boolean isKm;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_tablet,container,false);
        comm = (Comunnicator) getActivity();

        if ( Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                comm.noPermission();
            }
        }

        keywordButton = (Button) v.findViewById(R.id.keyword_button);
        nearbyButton = (Button) v.findViewById(R.id.nearby_button);
        editSearch = (EditText) v.findViewById(R.id.edit_search);
        categorySpinner = (Spinner) v.findViewById(R.id.category_spinner);
        searchButton = (ImageButton) v.findViewById(R.id.search_button);
        increaseDistanceButton = (ImageButton) v.findViewById(R.id.increase_distance_button);
        decreaseDistanceButton = (ImageButton) v.findViewById(R.id.deccrease_distance_button);
        distanceTV = (TextView) v.findViewById(R.id.distanceTV);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        tv = (TextView) v.findViewById(R.id.tv);
        placesList = (ListView) v.findViewById(R.id.places_list);
        default_unit_tv = (TextView)v.findViewById(R.id.default_unit_tv);
        settings = (ImageButton) v.findViewById(R.id.toolBarSettings);
        favourites_button = (ImageButton)v.findViewById(R.id.toolBar_favo);
        setHasOptionsMenu(true);
        settings = (ImageButton) v.findViewById(R.id.toolBarSettings);
        Exit = (ImageButton) v.findViewById(R.id.toolBarExit);
        placeArray = new ArrayList<Place>();
        placeArrayAdapter = new ArrayAdapter<Place>(getActivity(), android.R.layout.simple_list_item_1, placeArray);
        placesList.setAdapter(placeArrayAdapter);
        distance = 10;
        imageView = (ImageView) getActivity().findViewById(R.id.place_image);
        buttonSpeak = (ImageButton) v.findViewById(R.id.btnSpeak);
        keyword_example = (TextView) v.findViewById(R.id.keyword_example);
        multipleMarkers = (ImageButton)v.findViewById(R.id.toolBarDelete);
        clear_et = (ImageButton) v.findViewById(R.id.clear_et);
        clear_et.setVisibility(View.GONE);

        keywordButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));


        defaultShared = PreferenceManager.getDefaultSharedPreferences(getActivity());

        flag = defaultShared.getInt("default", 1);

        if (flag==1){
            default_unit_tv.setText("KM");
        }else if (flag==2){
            default_unit_tv.setText("Miles");

        }

        Bundle bundle = getArguments();
        if (bundle!=null){
            String unit = bundle.getString("default");
        }


        try {
            notifydata();
        }catch (Exception e){
            e.printStackTrace();
        }

        IntentFilter defaultUn = new IntentFilter(DialogSettingsTablet.FLAG_BROADCAST);
        DefaultReciever defaRec = new DefaultReciever();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(defaRec, defaultUn);


        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView) v.findViewById(R.id.location_address_view);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        updateUIWidgets();
//        comm.placeSender(placeArray);
        placeArrayAdapter.notifyDataSetChanged();

        try {
            fetchAddressButtonHandler(v);


            defaultDistance = Double.valueOf(String.valueOf(distanceTV.getText()));
        } catch (NullPointerException n) {
            n.printStackTrace();
        }


        clear_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearch.setText("");
            }
        });


        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        if (!isNetworkAvailable()) {
//            final Snackbar snackbar = Snackbar.make(v, "No internet connection, working offline", Snackbar.LENGTH_INDEFINITE);
//            snackbar.show();
//            snackbar.setAction("Got it", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    snackbar.dismiss();
//                }
//            });
        }

                Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("LOGOUT", true);
                startActivity(intent);

                getActivity().finish();
            }
        });




          multipleMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBHandler handler = new DBHandler(getActivity());
                ArrayList <Place> list;
                list = handler.showAllLocationsArrayListAndSort();


                if (!list.isEmpty()) {
                    comm.multiTab();
                }else {
                    snackbar = Snackbar.make(v, "List is empty", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("Got it", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            snackbar.dismiss();
                        }
                    });                }


            }
        });

        favourites_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                comm.goToFavoFromTablet();

                DialogFavouritesTablet dialogFragment = new DialogFavouritesTablet();
                manager = getChildFragmentManager();
                dialogFragment.show(manager, "dialog");
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DialogSettingsTablet dialogFragment = new DialogSettingsTablet();
                manager = getChildFragmentManager();
                dialogFragment.show(manager, "dialog");

                //// TODO: 2/20/2016 to change settings+favourites fragment to dialog fragment 
            }
        });


        IntentFilter nearbyFilter = new IntentFilter(NearbyLocationIntentService.FLAG_BROADCAST);
        NewPlaceReciever newPlaceReciever = new NewPlaceReciever();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(newPlaceReciever, nearbyFilter);


        IntentFilter textSearchFilter = new IntentFilter(TextSearchIntentService.FLAG_BROADCAST);
        TextSearchReciever textSearchReciever = new TextSearchReciever();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(textSearchReciever, textSearchFilter);

        IntentFilter connectionLost = new IntentFilter(ConnectionChangeReceiver.FLAG_BROADCAST);
        ConnectionChange noConnection = new ConnectionChange();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(noConnection, connectionLost);


        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setColorSchemeResources(android.R.color.holo_green_dark,
                        android.R.color.holo_red_dark,
                        android.R.color.holo_blue_dark,
                        android.R.color.holo_orange_dark);

                try {


                    notifydata();
                }catch (Exception e){
                    e.printStackTrace();
                }

                swipeLayout.setRefreshing(false);

            }
        });


        mLocationAddressTextView = (TextView) v.findViewById(R.id.location_address_view);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";

        comm = (Comunnicator) getActivity();
//        comm.placeSender(placeArray);
        placeArrayAdapter.notifyDataSetChanged();

        try {

            defaultDistance = Double.valueOf(String.valueOf(distanceTV.getText()));
        } catch (NullPointerException n) {
            n.printStackTrace();
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        spinnerChoice = "bar";
                        break;
                    case 1:
                        spinnerChoice = "bank";
                        break;
                    case 2:
                        spinnerChoice = "airport";
                        break;
                    case 3:
                        spinnerChoice = "police";
                        break;
                    case 4:
                        spinnerChoice = "hospital";
                        break;
                    case 5:
                        spinnerChoice = "restaurant";
                        break;
                    case 6:
                        spinnerChoice = "food";
                        break;
                    case 7:
                        spinnerChoice = "gym";
                        break;
                    case 8:
                        spinnerChoice = "accounting";
                        break;
                    case 9:
                        spinnerChoice = "amusement_park";
                        break;
                    case 10:
                        spinnerChoice = "aquarium";
                        break;
                    case 11:
                        spinnerChoice = "art_gallery";
                        break;
                    case 12:
                        spinnerChoice = "atm";
                        break;
                    case 13:
                        spinnerChoice = "bakery";
                        break;
                    case 14:
                        spinnerChoice = "beauty_salon";
                        break;
                    case 15:
                        spinnerChoice = "bicycle_store";
                        break;
                    case 16:
                        spinnerChoice = "book_store";
                        break;
                    case 17:
                        spinnerChoice = "bowling_alley";
                        break;
                    case 18:
                        spinnerChoice = "bus_station";
                        break;
                    case 19:
                        spinnerChoice = "cafe";
                        break;
                    case 20:
                        spinnerChoice = "campground";
                        break;
                    case 21:
                        spinnerChoice = "car_dealer";
                        break;
                    case 22:
                        spinnerChoice = "car_rental";
                        break;
                    case 23:
                        spinnerChoice = "car_repair";
                        break;
                    case 24:
                        spinnerChoice = "car_wash";
                        break;
                    case 25:
                        spinnerChoice = "casino";
                        break;
                    case 26:
                        spinnerChoice = "cemetery";
                        break;
                    case 27:
                        spinnerChoice = "church";
                        break;
                    case 28:
                        spinnerChoice = "city_hall";
                        break;
                    case 29:
                        spinnerChoice = "clothing_store";
                        break;
                    case 30:
                        spinnerChoice = "convenience_store";
                        break;
                    case 31:
                        spinnerChoice = "courthouse";
                        break;
                    case 32:
                        spinnerChoice = "dentist";
                        break;
                    case 33:
                        spinnerChoice = "department_store";
                        break;
                    case 34:
                        spinnerChoice = "doctor";
                        break;
                    case 35:
                        spinnerChoice = "electrician";
                        break;
                    case 36:
                        spinnerChoice = "electronics_store";
                        break;
                    case 37:
                        spinnerChoice = "embassy";
                        break;
                    case 38:
                        spinnerChoice = "establishment";
                        break;
                    case 39:
                        spinnerChoice = "finance";
                        break;
                    case 40:
                        spinnerChoice = "fire_station";
                        break;
                    case 41:
                        spinnerChoice = "florist";
                        break;
                    case 42:
                        spinnerChoice = "funeral_home";
                        break;
                    case 43:
                        spinnerChoice = "furniture_store";
                        break;
                    case 44:
                        spinnerChoice = "gas_station";
                        break;
                    case 45:
                        spinnerChoice = "general_contractor";
                        break;
                    case 46:
                        spinnerChoice = "grocery_or_supermarket";
                        break;
                    case 47:
                        spinnerChoice = "hair_care";
                        break;
                    case 48:
                        spinnerChoice = "hardware_store";
                        break;
                    case 49:
                        spinnerChoice = "health";
                        break;
                    case 50:
                        spinnerChoice = "hindu_temple";
                        break;
                    case 51:
                        spinnerChoice = "home_goods_store";
                        break;
                    case 52:
                        spinnerChoice = "insurance_agency";
                        break;
                    case 53:
                        spinnerChoice = "jewelry_store";
                        break;
                    case 54:
                        spinnerChoice = "laundry";
                        break;
                    case 55:
                        spinnerChoice = "lawyer";
                        break;
                    case 56:
                        spinnerChoice = "library";
                        break;
                    case 57:
                        spinnerChoice = "liquor_store";
                        break;
                    case 58:
                        spinnerChoice = "local_government_office";
                        break;
                    case 59:
                        spinnerChoice = "locksmith";
                        break;
                    case 60:
                        spinnerChoice = "lodging";
                        break;
                    case 61:
                        spinnerChoice = "meal_delivery";
                        break;
                    case 62:
                        spinnerChoice = "meal_takeaway";
                        break;
                    case 63:
                        spinnerChoice = "mosque";
                        break;
                    case 64:
                        spinnerChoice = "movie_rental";
                        break;
                    case 65:
                        spinnerChoice = "movie_theater";
                        break;
                    case 66:
                        spinnerChoice = "moving_company";
                        break;
                    case 67:
                        spinnerChoice = "museum";
                        break;
                    case 68:
                        spinnerChoice = "night_club";
                        break;
                    case 69:
                        spinnerChoice = "painter";
                        break;
                    case 70:
                        spinnerChoice = "park";
                        break;
                    case 71:
                        spinnerChoice = "parking";
                        break;
                    case 72:
                        spinnerChoice = "pet_store";
                        break;
                    case 73:
                        spinnerChoice = "pharmacy";
                        break;
                    case 74:
                        spinnerChoice = "physiotherapist";
                        break;
                    case 75:
                        spinnerChoice = "place_of_worship";
                        break;
                    case 76:
                        spinnerChoice = "plumber";
                        break;
                    case 77:
                        spinnerChoice = "post_office";
                        break;
                    case 78:
                        spinnerChoice = "real_estate_agency";
                        break;
                    case 79:
                        spinnerChoice = "roofing_contractor";
                        break;
                    case 80:
                        spinnerChoice = "rv_park";
                        break;
                    case 81:
                        spinnerChoice = "school";
                        break;
                    case 82:
                        spinnerChoice = "shoe_store";
                        break;
                    case 83:
                        spinnerChoice = "shopping_mall";
                        break;
                    case 84:
                        spinnerChoice = "spa";
                        break;
                    case 85:
                        spinnerChoice = "stadium";
                        break;
                    case 86:
                        spinnerChoice = "storage";
                        break;
                    case 87:
                        spinnerChoice = "store";
                        break;
                    case 88:
                        spinnerChoice = "subway_station";
                        break;
                    case 89:
                        spinnerChoice = "synagogue";
                        break;
                    case 90:
                        spinnerChoice = "taxi_stand";
                        break;
                    case 91:
                        spinnerChoice = "train_station";
                        break;
                    case 92:
                        spinnerChoice = "travel_agency";
                        break;
                    case 93:
                        spinnerChoice = "university";
                        break;
                    case 94:
                        spinnerChoice = "veterinary_care";
                        break;
                    case 95:
                        spinnerChoice = "zoo";
                        break;


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        keywordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editSearch.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
                categorySpinner.setVisibility(View.GONE);
                distanceTV.setVisibility(View.GONE);
                increaseDistanceButton.setVisibility(View.GONE);
                decreaseDistanceButton.setVisibility(View.GONE);
                tv.setVisibility(View.GONE);
                keyword_example.setVisibility(View.VISIBLE);
                search_source = Constants.KEYWORD;
                default_unit_tv.setVisibility(View.GONE);
                buttonSpeak.setVisibility(View.VISIBLE);
                keywordButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                nearbyButton.setBackgroundColor(Color.WHITE);


            }
        });

        nearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editSearch.setVisibility(View.GONE);
                clear_et.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                categorySpinner.setVisibility(View.VISIBLE);
                distanceTV.setVisibility(View.VISIBLE);
                increaseDistanceButton.setVisibility(View.VISIBLE);
                decreaseDistanceButton.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                keyword_example.setVisibility(View.GONE);
                buttonSpeak.setVisibility(View.GONE);
                search_source = Constants.NEARBY;

                default_unit_tv.setVisibility(View.VISIBLE);
                keywordButton.setBackgroundColor(Color.WHITE);
                nearbyButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));


            }
        });

        increaseDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = Double.parseDouble(distanceTV.getText().toString());
                distance += 1;
                distanceTV.setText(String.valueOf(distance));
            }
        });

        decreaseDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = Double.parseDouble(distanceTV.getText().toString());
                distance -= 1;
                distanceTV.setText(String.valueOf(distance));
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ///// TODO: 2/9/2016
                ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (mWifi.isConnected() == true || mMobile.isConnected() == true) {


                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(100);
                    progressBar.setProgress(2);
                    progressBar.setSecondaryProgress(5);
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    // getting GPS status
                    isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                    // getting network status
                    isNetworkEnabled = locationManager
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (isGPSEnabled || isNetworkEnabled && mLastLocation != null && isNetworkAvailable()) {
                        // no network provider is enabled
                        String search = editSearch.getText().toString().trim();

                        try {
                            search = URLEncoder.encode(search, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        if (search_source.equals(Constants.NEARBY)) {

                          try{
                            NearbyLocationIntentService.startActionNearbySearch(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), (int) distance * 1000, spinnerChoice, mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        }
                        if (search_source.equals(Constants.KEYWORD)) {
                            if (!search.equals(null) && !search.equals("")) {
                                try {
                                    TextSearchIntentService.startActionTextSearch(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), (int) distance * 1000, spinnerChoice, search);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                } else {
                                Snackbar.make(v, "nothing to search", Snackbar.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        }
                    }
                } else {

                    progressBar.setVisibility(View.GONE);

                    showSettingsAlert();
                }
            }
        });

        buildGoogleApiClient();


        return v;
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
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


    public void notifydata() {

        try {


            DBHandler handler = new DBHandler(getActivity());
            //placeArray = new ArrayList<>();
            // placeArray = null;
            placeArray = handler.showAllLocationsArrayListAndSort();
            for (int i = 0; i <placeArray.size() ; i++) {

                if (flag==1) {
                    placeArray.get(i).setIsKM(true); // TODO: 3/2/2016
                }else if (flag==2){
                    placeArray.get(i).setIsKM(false); // TODO: 3/2/2016

                }

                placeArray.get(i).setDistanceFromUser(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            }
            placeArrayAdapter = new ArrayAdapter<Place>(getActivity(), android.R.layout.simple_list_item_1, placeArray);
            placesList.setAdapter(placeArrayAdapter);
            placeArrayAdapter.notifyDataSetChanged();
        }catch (NullPointerException n){
            n.printStackTrace();
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        notifydata();


        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                final Place itemClickPlace = placeArray.get(position);
//
                imageUrl = itemClickPlace.getPhotos();


                Location loc = new Location("a");


                if (isGPSEnabled && isNetworkEnabled) {

                    loc.setLatitude(mLastLocation.getLatitude());
                    loc.setLongitude(mLastLocation.getLongitude());
                }

                Location location_b = new Location("b");
                location_b.setLatitude(itemClickPlace.getLatitude());
                location_b.setLongitude(itemClickPlace.getLongitude());

                double distance_km = (int) loc.distanceTo(location_b) / 1000;
                double distance_miles = (int) loc.distanceTo(location_b) / 1000 * 1.60934;

                DialogFragmentTablet dialogFragment = new DialogFragmentTablet();
                manager = getChildFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putDouble(Constants.DISTANCE_IN_KM, distance_km);
                bundle.putDouble(Constants.DISTANCE_IN_MILES, distance_miles);

                bundle.putString(Constants.COLUMN_NAME, itemClickPlace.getName());
                bundle.putDouble(Constants.COLUMN_LAT, itemClickPlace.getLatitude());
                bundle.putDouble(Constants.COLUMN_LNG, itemClickPlace.getLongitude());
                bundle.putString(Constants.COLUMN_VICINITY, itemClickPlace.getVicinity());
                bundle.putString(Constants.COLUMN_TYPES, itemClickPlace.getTypes());
                bundle.putString(Constants.COLUMN_OPENING_HOURS, itemClickPlace.getOpening_hours());
                bundle.putString(Constants.COLUMN_IMAGE_URL, imageUrl);
                bundle.putString(Constants.COLUMN_ID, itemClickPlace.getId());
                bundle.putParcelable("place", itemClickPlace);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(manager, "dialog");

//                comm.locationTabletPicker(itemClickPlace.getLatitude(),itemClickPlace.getLongitude(),"name","vicinity","photos");


            }
//                });
//
////                dialog.show();
//            }
//
        });
        super.onViewCreated(view, savedInstanceState);

    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(), "Sorry! Your device doesn\\'t support speech input", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        lng = mLastLocation.getLongitude();
        lat = mLastLocation.getLatitude();

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    protected void displayAddressOutput() {
        try {


            mLocationAddressTextView.setText("");
            if (!mAddressOutput.equals("Sorry, the service is not available")) {
//                mLocationAddressTextView.setText("Current address: " + mAddressOutput); // TODO: 2/20/2016
                mLocationAddressTextView.setText("");



                hasConnection=true;
            } else {
//                mLocationAddressTextView.setText("Sorry. current address is unavailable"); // TODO: 2/20/2016
                mLocationAddressTextView.setText("");

                hasConnection=false;
            }
        }catch (NullPointerException n){
            n.printStackTrace();
        }
    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }



    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    public void fetchAddressButtonHandler(View view) {
        // We only start the service to fetch the address if GoogleApiClient is connected.
        try {
            if (mGoogleApiClient.isConnected() && mLastLocation != null)
                mLastLocation.getLongitude();

            startIntentService();
        } catch (NullPointerException p) {
            p.printStackTrace();
        }


        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
//            Toast.makeText(getContext(),mLatitudeLabel + " "+ mLastLocation.getLatitude()+ " " + mLongitudeLabel + " " + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
        }
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }
        // It is possible that the user presses the button to get the address before the
        // GoogleApiClient object successfully connects. In such a case, mAddressRequested
        // is set to true, but no attempt is made to fetch the address (see
        // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
        // user has requested an address, since we now have a connection to GoogleApiClient.
        if (mAddressRequested) {
            try {


                startIntentService();
            }catch (Exception e){
                showSettingsAlert();

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void updateUIWidgets() {
        if (mAddressRequested) {
//            progressBar.setVisibility(ProgressBar.VISIBLE);
        } else {
//            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
//        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {


            timer = new CountDownTimer(1000, 20) {

                public void onTick(long millisUntilFinished) {


                }

                public void onFinish() {
                    notifydata();


                }

            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();

    }


    public class NewPlaceReciever extends BroadcastReceiver {

        int counter =0;

        @Override
        public void onReceive(Context context, Intent intent) {


            progressBar.setVisibility(View.GONE);

            if (intent!=null) {

//                if (counter>0) {
//                    DBHandler handler = new DBHandler(context);
//                    handler.removeAllLocations();
//                    counter=+1;
//
//
//                }
                notifydata();
            }

//                snackbar = Snackbar.make(v, "No results", Snackbar.LENGTH_INDEFINITE);
//            snackbar.show();
//            snackbar.setAction("Got it", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    snackbar.dismiss();

//            });
        }


    }

    public class TextSearchReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            progressBar.setVisibility(View.GONE);

            try {


                if (intent != null ) {

                    notifydata();
                } else

                    Toast.makeText(context, "No results", Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }

        }
    }

    public class ConnectionChange extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            try {


                final Snackbar snackbar = Snackbar.make(v, "Connection lost , \n some options might not work", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                snackbar.setAction("Got it", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        snackbar.dismiss();
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            try {


                if (resultCode == Constants.SUCCESS_RESULT) {
                    showToast(getString(R.string.address_found));
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

    public class DefaultReciever extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {



            if (intent != null) {
                int def = intent.getIntExtra("un",1);

                if (def==1){
                    flag=1;
                }else if (def==2){
                    flag=2;
                }

                if (flag==1){
                    default_unit_tv.setText("KM");
                }else if (flag==2){
                    default_unit_tv.setText("Miles");

                }

                notifydata();
            }


        }


    }
}
