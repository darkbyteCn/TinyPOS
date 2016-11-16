package com.tinyappsdev.tinypos.ui.BaseUI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tinyappsdev.tinypos.helper.ConfigCache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseActivity extends AppCompatActivity implements ActivityInterface {
    public final static String TAG = BaseActivity.class.getSimpleName();

    protected Set<Handler> mMsgHandlers;
    protected ConfigCache mConfigCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMsgHandlers = new HashSet<Handler>();
        mConfigCache = ConfigCache.getInstance(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMsgHandlers.clear();
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
