package com.example.daniel.aroundme.services_and_recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Daniel on 1/9/2016.
 */

//BroadcastReceiver to know when boot is done
public class BootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context,LocationService.class);
        context.startService(in);
        Toast.makeText(context, "Location Services Running", Toast.LENGTH_SHORT).show();
    }
}
