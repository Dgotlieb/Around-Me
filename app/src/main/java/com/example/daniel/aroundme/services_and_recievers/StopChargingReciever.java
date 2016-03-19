//package com.example.daniel.aroundme.services_and_recievers;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.widget.Toast;
//
//import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
//
///**
// * Created by Daniel on 11/21/2015.
// */
//// BroadcastReceiver to detect if User is stoped charging
//
//public class StopChargingReciever extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "User is not charging anymore...", Toast.LENGTH_SHORT).show();
//
//        Intent i = new Intent(context, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//    }
//}
