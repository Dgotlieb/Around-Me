package com.example.daniel.aroundme.tablet;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
import com.example.daniel.aroundme.database.Constants;
import com.example.daniel.aroundme.database.DBFabHandler;
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

/**
 * Created by Daniel on 12/20/2015.
 */
public class DialogFragmentTablet extends android.support.v4.app.DialogFragment {

    private double lat;
    private double lng;
    private String place_name;
    private String vicinity;
    private String types;
    private String opening_hours;
    private String place_id;
    private String international_phone_number;
    private String phone_number;
    private String website;
    private String rating;
    private FragmentManager manager;
    public String image;
    public ImageView imageView;
    public ProgressBar progressBar;
    private ImageButton streetViewButton;
    private ImageButton mapDialogButton;
    private ImageButton shareDialogButton;
    private ImageButton favouritesDialogButton;
    private TextView vicinity_tv;
    private TextView type_tv;
    private TextView opening_tv;
    private TextView international_phone_number_tv;
    private TextView formatted_phone_number_tv;
    private TextView website_tv;
    private TextView rating_tv;
    private TextView distance_from_you_tv;
    private Place place;
    private Comunnicator comm;
    private double distanceFromUserInKm;
    private double distanceFromUserInMiles;
    private final static String NOTHING="";
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    protected LocationManager locationManager;







    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_tablet_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle!=null) {
            lat = bundle.getDouble(Constants.COLUMN_LAT);
            lng = bundle.getDouble(Constants.COLUMN_LNG);
            place_name = bundle.getString(Constants.COLUMN_NAME);
            image = bundle.getString(Constants.COLUMN_IMAGE_URL);
            vicinity = bundle.getString(Constants.COLUMN_VICINITY);
            types = bundle.getString(Constants.COLUMN_TYPES);
            opening_hours = bundle.getString(Constants.COLUMN_OPENING_HOURS);
            place_id = bundle.getString(Constants.COLUMN_ID);
            distanceFromUserInKm = bundle.getDouble(Constants.DISTANCE_IN_KM);
            distanceFromUserInMiles = bundle.getDouble(Constants.DISTANCE_IN_MILES);
            place = bundle.getParcelable("place");
            comm = (Comunnicator) getActivity();


        }


        if (isNetworkAvailable() ) {


            DownloadImageTask downloadTask = new DownloadImageTask(getActivity());
            downloadTask.execute(image);

            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyDgYYu3M2s-FZgVAp7xawEWjPnsnr24gFE";
            MyTask task = new MyTask();
            task.execute(new String[]{url});


        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        distance_from_you_tv = (TextView)rootview.findViewById(R.id.distance_from_you_tv);
        progressBar = (ProgressBar)rootview.findViewById(R.id.progressBarImage);
         imageView = (ImageView)rootview.findViewById(R.id.place_image);
        progressBar.setVisibility(View.VISIBLE);
        international_phone_number_tv = (TextView)rootview.findViewById(R.id.international_phone_number_tv);
        formatted_phone_number_tv = (TextView)rootview.findViewById(R.id.formatted_phone_number_tv);
        website_tv  = (TextView)rootview.findViewById(R.id.website_tv);
        rating_tv  = (TextView)rootview.findViewById(R.id.rating_tv);

        vicinity_tv = (TextView)rootview.findViewById(R.id.vicinity_tv);
        type_tv = (TextView)rootview.findViewById(R.id.types_tv);
        opening_tv = (TextView)rootview.findViewById(R.id.opening_tv);
        mapDialogButton = (ImageButton) rootview.findViewById(R.id.dialogButtonMap);
         streetViewButton = (ImageButton) rootview.findViewById(R.id.street_view_dialog);
         shareDialogButton = (ImageButton) rootview.findViewById(R.id.dialogButtonShare);
         favouritesDialogButton = (ImageButton) rootview.findViewById(R.id.dialogButtonFavourites);

        try {


            vicinity_tv.setText("Vicinity: " + vicinity);
            type_tv.setText("Type: " + types);

            if (opening_hours.equals("opening_hours")) {
                opening_tv.setVisibility(View.GONE);
            } else
                opening_tv.setText("Open now: " + opening_hours);

        }catch (Exception e){
            e.printStackTrace();
        }



        website_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent websiteUrl = new Intent(Intent.ACTION_VIEW);
                websiteUrl.setData(Uri.parse(website));
                startActivity(websiteUrl);
            }
        });

        international_phone_number_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+international_phone_number+""));
                startActivity(intent);
            }
        });

        formatted_phone_number_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone_number+""));
                startActivity(intent);

            }
        });


        streetViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comunnicator comunnicator = (Comunnicator) getActivity();
                comunnicator.streetView(lat, lng);

            }
        });

        shareDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("share");
                builder.setMessage("how would you like to share? ");
                builder.setCancelable(true);
                builder.setPositiveButton("E-mail", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        String[] recipients = new String[]{"", "",};
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your new Place ");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Place: " + place_name);
                        emailIntent.setType("message/rfc822");
                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                        dialog.dismiss();
                    }
                });


                builder.setNeutralButton("Whatsapp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "" + "My Place: " + place_name);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        dialog.dismiss();


                    }
                });
                builder.show();

            }
        });
        favouritesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();

                DBFabHandler db = new DBFabHandler(getActivity());
                db.addPlace(place);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, intent, 0);
                try {
                    Notification notification = new Notification.Builder(getActivity())
                            .setContentTitle("New Favourites  added: ")
                            .setContentText(place.getName() + ", " + place.getVicinity())
//                            .setSmallIcon(android.R.drawable.ic_dialog_map)

                            .setSmallIcon(R.drawable.notification_anim)
                            .setContentIntent(pi)
                            .build();

                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);

                    Snackbar.make(v,"Place added successfully",Snackbar.LENGTH_LONG);
                } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });



        // if button is clicked, close the custom dialog
        mapDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    comm.locationTabletPicker(lat,lng,place_name,vicinity,"photos");
                getDialog().dismiss();



            }
        });

        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(place_name);
        super.onViewCreated(view, savedInstanceState);
    }





    @Override
    public void onResume() {
        super.onResume();

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity(). getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class DownloadImageTask extends AsyncTask<String, Integer,Bitmap>
    {

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

                imageView.setImageBitmap(null);
            }catch (NullPointerException n){
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
                try{
                    progressBar.setVisibility(View.GONE);


                    String filename = place_name+".jpg";
                    File sd = Environment.getExternalStorageDirectory();
                    Bitmap myBitmap = BitmapFactory.decodeFile(sd+"/PlacesFolder/AroundMe/"+filename);

                    if (myBitmap!=null) {
                        imageView.setImageBitmap(myBitmap);
                    }else
                        imageView.setImageBitmap(result);
                    saveImageToFile(place_name,result);



                }catch (NullPointerException n){
                    n.printStackTrace();
                }


            }


        }



        private void saveImageToFile(String place_name, Bitmap result ) {


            String filename = place_name+".jpg";
            File sd = Environment.getExternalStorageDirectory();

//            File sd = new File (Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"+ filename + ".jpg");
//
//            if(!sd.exists())
//                sd.mkdirs();

            File file = new File(sd+"/PlacesFolder/AroundMe/");

            File parent = file.getParentFile();
            if (parent != null)
            { parent.mkdirs();}


//            if(!file.exists())
//                file.mkdirs();

//            File dest = new File(file, filename);
            File dest = new File(parent, filename);







            Bitmap bitmap =result;
            try {
                FileOutputStream out = new FileOutputStream(dest);
//                FileOutputStream out = new FileOutputStream(sd);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        private Bitmap downloadImage(String urlString) {
            URL url;
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

    class MyTask extends AsyncTask<String,Void,String> {


        @Override
        protected void onPreExecute() {



        }

        @Override
        protected String doInBackground(String... strings) {

            String response = sendHttpRequest(strings[0]);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {


            try {
                JSONObject object = new JSONObject(s);
                JSONObject array = object.getJSONObject("result");


                JSONArray address_components = array.getJSONArray("address_components");
                international_phone_number=NOTHING;
                if (array.has("international_phone_number")) {
                    international_phone_number = array.getString("international_phone_number");
                }
                phone_number = NOTHING;
                if (array.has("formatted_phone_number")) {
                    phone_number = array.getString("formatted_phone_number");
                }
                website= NOTHING;
                if (array.has("website")) {
                    website = array.getString("website");
                }
                rating=NOTHING;
                if (array.has("rating")) {
                    rating = array.getString("rating");
                }

                if (!international_phone_number.equals(NOTHING)) {
                    international_phone_number_tv.setText("International phone number: " + international_phone_number);
                }//else international_phone_number_tv.setVisibility(View.GONE);

                if (!phone_number.equals(NOTHING)) {
                formatted_phone_number_tv.setText("Phone number: "+phone_number);
                }//else formatted_phone_number_tv.setVisibility(View.GONE);

                if (!website.equals(NOTHING)) {
                    website_tv.setText("Website: "+website);
                }//else website_tv.setVisibility(View.GONE);

                if (!rating.equals(NOTHING)) {
                    rating_tv.setText("Rating: "+rating+"/10");
                }//else rating_tv.setVisibility(View.GONE);



    } catch (JSONException e) {
                e.printStackTrace();

            }

        }




        private String sendHttpRequest(String urlString) {
            BufferedReader input = null;
            HttpURLConnection httpCon = null;
            InputStream input_stream =null;
            InputStreamReader input_stream_reader = null;
            StringBuilder response = new StringBuilder();
            try{
                URL url = new URL(urlString);
                httpCon = (HttpURLConnection)url.openConnection();
                if(httpCon.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    return null;
                }

                input_stream = httpCon.getInputStream();
                input_stream_reader = new InputStreamReader(input_stream);
                input = new BufferedReader(input_stream_reader);
                String line ;
                while ((line = input.readLine())!= null){
                    response.append(line +"\n");
                }



            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(input!=null){
                    try {
                        input_stream_reader.close();
                        input_stream.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(httpCon != null){
                        httpCon.disconnect();
                    }
                }
            }
            return response.toString();
        }
    }
}