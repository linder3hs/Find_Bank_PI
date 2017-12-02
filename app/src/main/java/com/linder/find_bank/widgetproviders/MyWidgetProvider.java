package com.linder.find_bank.widgetproviders;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.linder.find_bank.R;
import com.linder.find_bank.activities.HomeActivity;
import com.linder.find_bank.model.Agente;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by linderhassinger on 11/27/17.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    // Variables
    private ListView list;
    String[] sistemas = {"Ubuntu", "Android", "iOS", "Windows", "Mac OSX",
            "Google Chrome OS", "Debian", "Mandriva", "Solaris", "Unix"};
    private static final String TAG = MyWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Calling onUpdate...");

        for (int i = 0; i < appWidgetIds.length; i++) {
            Log.d(TAG, "appWidgetIds: " + appWidgetIds[i]);
            Intent intent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);



            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            remoteViews.setOnClickPendingIntent(R.id.button_widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

            //String message = "Hola mundo! " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            // Set layout
            //RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);



            // Register an onClickListener on the image button
            //Intent intent2 = new Intent(context, MyWidgetProvider.class);
            //intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            //PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //remoteViews.setOnClickPendingIntent(R.id.button_widget, pendingIntent);
            //appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

}

