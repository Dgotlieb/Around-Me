package com.example.daniel.aroundme.services_and_recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Daniel on 1/9/2016.
 */

//starting service in case user forced closed it
public class ServiceDestroyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debug", "ServeiceDestroy onReceive...");
        Log.d("debug", "action:" + intent.getAction());
        Log.d("debug", "Starting Service");
        context.startService(new Intent(context.getApplicationContext(), LocationService.class));

    }
}
