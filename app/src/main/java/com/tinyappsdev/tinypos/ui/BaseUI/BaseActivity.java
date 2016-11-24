package com.tinyappsdev.tinypos.ui.BaseUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.TinyApplication;
import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.LoginActivity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseActivity extends AppCompatActivity implements ActivityInterface {
    public final static String TAG = BaseActivity.class.getSimpleName();

    protected Set<Handler> mMsgHandlers;
    protected ConfigCache mConfigCache;
    protected SharedPreferences mSharedPreferences;
    protected Tracker mTracker;
    protected ApiCallClient.Result mResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMsgHandlers = new HashSet<Handler>();
        mConfigCache = AppGlobal.getInstance().getConfigCache();
        mSharedPreferences = AppGlobal.getInstance().getSharedPreferences();
        mTracker = ((TinyApplication)getApplication()).getDefaultTracker();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName(this.getClass().getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(!(this instanceof LoginActivity)) {
            String serverAuth = mSharedPreferences.getString("serverAuth", "");
            int employeeCode = mSharedPreferences.getInt("employeeCode", 0);

            if(serverAuth == null || serverAuth.isEmpty() || employeeCode == 0)
                AppGlobal.getInstance().showLogin(this, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMsgHandlers.clear();
        if(mResult != null) mResult.cancel();
    }

    @Override
    public void registerMsgHandler(Handler handler) {
        Log.d(TAG, String.format("%s.registerMsgHandler(%s)", this.toString(), handler.toString()));
        mMsgHandlers.add(handler);
    }

    @Override
    public void unregisterMsgHandler(Handler handler) {
        Log.d(TAG, String.format("%s.unregisterMsgHandler(%s)", this.toString(), handler.toString()));
        mMsgHandlers.remove(handler);
    }

    @Override
    public ConfigCache getConfigCache() {
        return mConfigCache;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public void sendMessage(int msgId) {
        for(Handler handler : mMsgHandlers)
            handler.sendEmptyMessage(msgId);
    }

    public void sendMessage(int msgId, int arg1, int arg2, Bundle data) {
        for(Handler handler : mMsgHandlers) {
            Message msg = handler.obtainMessage(msgId, arg1, arg2);
            if(data != null) msg.setData(data);
            handler.sendMessage(msg);
        }
    }

}
