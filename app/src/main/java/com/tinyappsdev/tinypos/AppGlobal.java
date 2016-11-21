package com.tinyappsdev.tinypos;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.LoginActivity;

import java.util.HashSet;
import java.util.Set;

public class AppGlobal {
    public static final String TAG = AppGlobal.class.getSimpleName();
    private static AppGlobal sAppGlobal;

    public static AppGlobal createInstance(Context context) {
        Context appContext = context.getApplicationContext();
        synchronized (AppGlobal.class) {
            if(sAppGlobal != null) {
                sAppGlobal.mUiApiCallClient.destroy();
                sAppGlobal.mBgApiCallClient.destroy();
                sAppGlobal.mMsgHandlers.clear();
            }

            Log.i(TAG, "AppGlobal Created - " + appContext.toString());
            sAppGlobal = new AppGlobal(appContext);
        }

        return sAppGlobal;
    }

    public static AppGlobal getInstance() {
        return sAppGlobal;
    }

    private ConfigCache mConfigCache;
    private ApiCallClient mUiApiCallClient;
    private ApiCallClient mBgApiCallClient;
    private SharedPreferences mSharedPreferences;
    private final Set<Handler> mMsgHandlers = new HashSet();


    public AppGlobal(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mConfigCache = new ConfigCache(context);
        mUiApiCallClient = new ApiCallClient(this);
        mBgApiCallClient = new ApiCallClient(this);
    }

    public ConfigCache getConfigCache() {
        return mConfigCache;
    }

    public ApiCallClient getUiApiCallClient() {
        return mUiApiCallClient;
    }

    public ApiCallClient getBgApiCallClient() {
        return mBgApiCallClient;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public void onServerInfoChanged(Context context) {
        sendMessage(R.id.onServerInfoChanged);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void showLogin(Context context) {
        showLogin(context, false);
    }

    public void showLogin(Context context, boolean clearAll) {
        Intent intent = new Intent(context, LoginActivity.class);
        if(clearAll) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("popup", true);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public void registerMsgHandler(Handler handler) {
        synchronized (mMsgHandlers) {
            mMsgHandlers.add(handler);
        }
    }

    public void unregisterMsgHandler(Handler handler) {
        synchronized (mMsgHandlers) {
            mMsgHandlers.remove(handler);
        }
    }

    public void sendMessage(int msgId) {
        synchronized (mMsgHandlers) {
            for(Handler handler : mMsgHandlers)
                handler.sendEmptyMessage(msgId);
        }
    }
}
