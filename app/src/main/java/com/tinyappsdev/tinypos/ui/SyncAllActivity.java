package com.tinyappsdev.tinypos.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tinyappsdev.tinypos.R;

public class SyncAllActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_all);

        Log.i("PKT", ">>>>SyncAllActivity");
        Bundle bundle = getIntent().getExtras();
        if(bundle == null || !bundle.getBoolean("syncAll")) {
            //finish();
            //return;
        }


    }

}
