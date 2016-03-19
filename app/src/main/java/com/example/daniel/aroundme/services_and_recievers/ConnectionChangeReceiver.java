package com.example.daniel.aroundme.services_and_recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.daniel.aroundme.activities_and_fragments.FragmentList;
import com.example.daniel.aroundme.activities_and_fragments.MainActivity;

/**
 * Created by Daniel on 2/10/2016.
 */

//this class detect connection changes
public class ConnectionChangeReceiver extends BroadcastReceiver {

    public static final String FLAG_BROADCAST = "ConnectionChangeReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

//        if(isConnected(context)) {
//            Log.d("ConnectionChange","connected");
//            Intent connection = new Intent(FLAG_BROADCAST);
//            intent.putExtra("connection",1);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(connection);
//        }else {
//            Intent connection = new Intent(FLAG_BROADCAST);
//            intent.putExtra("connection",0);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(connection);
//        }
    }

    //boolean to check connectivity
    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }
}