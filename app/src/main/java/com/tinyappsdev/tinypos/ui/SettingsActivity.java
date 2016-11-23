package com.tinyappsdev.tinypos.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String serverAddress = AppGlobal.getInstance().getSharedPreferences()
                .getString("serverAddress", "");
        if(serverAddress != null && !serverAddress.isEmpty())
            getSupportActionBar().setTitle(String.format(
                    getString(R.string.format_title_with_server_address),
                    getString(R.string.title_activity_settings),
                    serverAddress
            ));

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if(count == 0) {
            finishSettings();
            return;
        }
        super.onBackPressed();
    }

    public void finishSettings() {
        finish();

        SharedPreferences sp = AppGlobal.getInstance().getSharedPreferences();

        if(sp.getBoolean("clearServerLogin", false)) {
            sp.edit()
                    .clear()
                    .putLong("syncRequestTs", System.currentTimeMillis())
                    .putBoolean("resyncDatabase", true)
                    .apply();
            AppGlobal.getInstance().onServerInfoChanged(getApplicationContext());
            return;
        }

        if(sp.getBoolean("resyncDatabasePending", false)) {
            sp.edit()
                    .remove("resyncDatabasePending")
                    .putLong("syncRequestTs", System.currentTimeMillis())
                    .putBoolean("resyncDatabase", true)
                    .apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.confirm: {
                finishSettings();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }

}
