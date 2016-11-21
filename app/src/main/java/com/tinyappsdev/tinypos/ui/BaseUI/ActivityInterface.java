package com.tinyappsdev.tinypos.ui.BaseUI;

import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.tinyappsdev.tinypos.helper.ConfigCache;


public interface ActivityInterface {
    void registerMsgHandler(Handler handler);
    void unregisterMsgHandler(Handler handler);
    ConfigCache getConfigCache();
    SharedPreferences getSharedPreferences();
}
