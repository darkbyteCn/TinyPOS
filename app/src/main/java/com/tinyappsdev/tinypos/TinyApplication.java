package com.tinyappsdev.tinypos;


import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.service.SyncService;

public class TinyApplication extends Application {
    private Object mLock = new Object();
    private Tracker mTracker;

    public Tracker getDefaultTracker() {
        synchronized (mLock) {
            if(mTracker == null) {
                GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
                mTracker = analytics.newTracker(R.xml.global_tracker);
            }
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppGlobal.createInstance(getApplicationContext());
        SyncService.Initialize(getApplicationContext());
    }

}
