package com.tinyappsdev.tinypos.ui.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.ui.HomeActivity;
import com.tinyappsdev.tinypos.ui.OrderMainActivity;


public class OrderStatusWidget extends AppWidgetProvider {
    public static final String ACTION_APPWIDGET_UPDATE
            = "com.tinyappsdev.tinypos.ui.Widget.OrderStatusWidget.ACTION_APPWIDGET_UPDATE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.order_status_widget);

        Intent intentHomeActivity = new Intent(context, HomeActivity.class);
        intentHomeActivity.setAction(Long.toString(System.currentTimeMillis()));
        Intent intentOrderMainActivity = new Intent(context, OrderMainActivity.class);
        intentOrderMainActivity.setAction(Long.toString(System.currentTimeMillis()));


        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addNextIntent(intentHomeActivity)
                .addNextIntent(intentOrderMainActivity)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.list_empty_msg, pendingIntent);
        views.setOnClickPendingIntent(R.id.title, pendingIntent);
        views.setPendingIntentTemplate(R.id.listview, pendingIntent);
        views.setRemoteAdapter(R.id.listview, new Intent(context, OrderStatusWidgetService.class));
        views.setEmptyView(R.id.listview, R.id.list_empty_msg);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview);
        }
    }

}

