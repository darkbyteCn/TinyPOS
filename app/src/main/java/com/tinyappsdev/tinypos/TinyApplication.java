package com.tinyappsdev.tinypos;


import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class TinyApplication extends Application {
    private Tracker mTracker;
    private Object mLock = new Object();

    public Tracker getDefaultTracker() {
        synchronized (mLock) {
            if(mTracker == null) {
                GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
                mTracker = analytics.newTracker(R.xml.global_tracker);
            }
        }
        return mTracker;
    }

}
