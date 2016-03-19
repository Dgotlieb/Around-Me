package com.example.daniel.aroundme.activities_and_fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.daniel.aroundme.Comunnicator;
import com.example.daniel.aroundme.R;

/**
 * Created by Daniel on 2/5/2016.
 */


    /*this fragment is for getting the necassary permissions
     for api23 and above according to the documantation:

     http://developer.android.com/training/permissions/requesting.html
     */

public class FragmentPermissions extends Fragment {
    private Switch location;
    private Switch storage;
    private Switch share;
    private Button agree;
    private SharedPreferences permissions;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;
    private static final int REQUEST_SHARE = 3;
    private Comunnicator comunnicator;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.permissions_fragment, container, false);
        location = (Switch) v.findViewById(R.id.switch_location);
        storage = (Switch) v.findViewById(R.id.switch_storage);
        share = (Switch) v.findViewById(R.id.switch_share);
        agree = (Button) v.findViewById(R.id.permissions_agree);





        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION);

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        REQUEST_LOCATION);

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.INTERNET},
                        REQUEST_LOCATION);

            }
        });

        storage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE);

            }
        });

        share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        REQUEST_SHARE);

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_SHARE);

            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location.isChecked() && share.isChecked() && storage.isChecked()) {
                    comunnicator = (Comunnicator) getActivity();
                    comunnicator.permissionsAgreed();
                } else {

                    final Snackbar snackbar = Snackbar.make(v, "please turn on switches", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            snackbar.dismiss();
                        }
                    });
                }

            }
        });


        return v;
    }


    public void api23permissions() {

        //asking the user to pick background color to show up in next runs
        boolean isFirst = permissions.getBoolean("permissions", true);
        if (isFirst) {


            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Permissions");
            builder.setMessage("In order for around me to work properly you need to allow the following: ");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_LOCATION);

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                            REQUEST_LOCATION);

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.INTERNET},
                            REQUEST_LOCATION);
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE);
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS},
                            REQUEST_SHARE);

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_SHARE);


                }
            });


            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDestroy();
                }
            });
            builder.show();


            SharedPreferences.Editor edit = permissions.edit();

            edit.putBoolean("permissions", false);

            edit.apply();

        }

    }
}
