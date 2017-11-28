package com.linder.find_bank.widgetproviders;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.linder.find_bank.R;
import com.linder.find_bank.model.Agente;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by linderhassinger on 11/27/17.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String TAG = MyWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Calling onUpdate...");

        for (int i = 0; i < appWidgetIds.length; i++) {
            Log.d(TAG, "appWidgetIds: " + appWidgetIds[i]);
            String message = "Hola mundo! " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            // Set layout
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            remoteViews.setTextViewText(R.id.text_widget, message);

            // Register an onClickListener on the image button
            Intent intent = new Intent(context, MyWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.button_widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

}

