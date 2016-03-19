package com.example.daniel.aroundme;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.daniel.aroundme.activities_and_fragments.MainActivity;
import com.example.daniel.aroundme.services_and_recievers.LocationService;

/**
 * Created by Daniel on 11/28/2015.
 */

//home screen widget
public class Widget extends AppWidgetProvider {
    private CountDownTimer timer;

    // our actions for our buttons
    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static String ACTION_WIDGET_LAUNCH = "ActionReceiverLaunch";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);


        //initialize image view And refresh button
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pending = PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_iv, pending);


        Intent active = new Intent(context, Widget.class);
        active.setAction(ACTION_WIDGET_REFRESH);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.refresh_widget, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    //actions when recieve to change image view And refresh button

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {
            Log.i("onReceive", ACTION_WIDGET_REFRESH);
            Intent locationService = new Intent(context, LocationService.class);
            context.startService(locationService);

        } else if (intent.getAction().equals(ACTION_WIDGET_LAUNCH)) {
            Log.i("onReceive", ACTION_WIDGET_LAUNCH);
        } else {
            super.onReceive(context, intent);
        }
    }

    //Called in response to the ACTION_APPWIDGET_ENABLED broadcast
    // when the a AppWidget for this provider is instantiated.
    @Override
    public void onEnabled(Context context) {
        Intent in = new Intent(context,LocationService.class);
        context.startService(in);
        super.onEnabled(context);
    }

    //Called in response to the ACTION_APPWIDGET_DELETED broadcast when one or more AppWidget
    // instances have been deleted.
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Intent in = new Intent(context,LocationService.class);
        context.stopService(in);
        super.onDeleted(context, appWidgetIds);
    }
}