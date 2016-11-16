package com.tinyappsdev.tinypos.ui;

import android.accounts.Account;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.TinyApplication;
import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.service.MessageService;
import com.tinyappsdev.tinypos.service.SyncAdapter;
import com.tinyappsdev.tinypos.service.SyncService;

public class HomeActivity extends SyncableActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SyncService.Initialize(getApplicationContext());
        ConfigCache.getInstance(getApplicationContext());

    }


    @Override
    protected void onResume() {
        super.onResume();

        Tracker tracker = ((TinyApplication)getApplication()).getDefaultTracker();
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public void clickOrder(View view) {
        Intent intent = new Intent(this, OrderMainActivity.class);
        startActivity(intent);
    }

    public void clickKitchen(View view) {
        Intent intent = new Intent(this, KitchenActivity.class);
        startActivity(intent);
    }

    public void clickCustomer(View view) {
        Intent intent = new Intent(this, CustomerActivity.class);
        startActivity(intent);
    }

    public void clickBackOffice(View view) {
        Intent intent = new Intent(this, BackOfficeActivity.class);
        startActivity(intent);
    }

    public void clickReport(View view) {
        Intent intent = new Intent(this, BackOfficeActivity.class);
        startActivity(intent);
    }

    public void clickTicket(View view) {
        Intent intent = new Intent(this, TicketActivity.class);
        startActivity(intent);
    }
}
