package com.tinyappsdev.tinypos.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.tinyappsdev.tinypos.service.MessageService;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseActivity;

/**
 * Created by pk on 10/15/2016.
 */
public class SyncableActivity extends BaseActivity {

    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {}
        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }
}
