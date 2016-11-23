package com.tinyappsdev.tinypos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;

public class HomeActivity extends SyncableActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: {
                mSharedPreferences.edit().remove("employeeCode").apply();
                AppGlobal.getInstance().showLogin(this);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }

    public void clickTicket(View view) {
        Intent intent = new Intent(this, TicketActivity.class);
        startActivity(intent);
    }

}
