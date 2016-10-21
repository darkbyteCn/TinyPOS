package com.tinyappsdev.tinypos.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.tinyappsdev.tinypos.R;

public class SyncService extends Service {
    public final static String TAG = SyncService.class.getSimpleName();

    public final static int SYNC_INTERVAL = 60 * 60;
    private static SyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        //Log.d(TAG, String.format("SyncService -> onCreate [%s]", this.toString()));
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    public static void ConfigurePeriodicSync(Context context, Account newAccount, int syncInterval) {
        ContentResolver.addPeriodicSync(
                newAccount,
                context.getString(R.string.sync_authority),
                new Bundle(),
                syncInterval
        );
    }

    public static Account GetSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        if(accountManager.getPassword(newAccount) == null) {
            Log.d(TAG, "GetSyncAccount -> addAccountExplicitly");
            if(!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            OnAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void OnAccountCreated(Account newAccount, Context context) {
        ConfigurePeriodicSync(context, newAccount, SYNC_INTERVAL);
        ContentResolver.setSyncAutomatically(
                newAccount,
                context.getString(R.string.sync_authority),
                true);
    }

    public static void Initialize(Context context) {
        GetSyncAccount(context);
    }

}
