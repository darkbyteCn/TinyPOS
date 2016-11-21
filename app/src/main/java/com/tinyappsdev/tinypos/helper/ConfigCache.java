package com.tinyappsdev.tinypos.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.tinyappsdev.tinypos.data.Config;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.DatabaseOpenHelper;
import com.tinyappsdev.tinypos.data.ModelHelper;

import java.util.HashMap;
import java.util.Map;

public class ConfigCache {

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private ContentResolver mContentResolver;
    private int mCacheVersion = 0;
    private Map<String, String> mCache;
    private ConfigCacheContentObserver mConfigCacheContentObserver = new ConfigCacheContentObserver();


    private class ConfigCacheContentObserver extends ContentObserver {
        public ConfigCacheContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            synchronized (mCache) {
                mCache.clear();
                mCacheVersion++;
            }
        }
    }

    public ConfigCache(Context context) {
        mDatabaseOpenHelper = DatabaseOpenHelper.getInstance(context);
        mCache = new HashMap<String , String>();
        mContentResolver = context.getContentResolver();
        mContentResolver.registerContentObserver(
                ContentProviderEx.BuildUri(Config.Schema.TABLE_NAME),
                true,
                mConfigCacheContentObserver
        );
    }

    protected String fetchMissedCache(String key) {
        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(
                Config.Schema.TABLE_NAME,
                new String[]{Config.Schema.COL_VAL},
                String.format("%s=?", Config.Schema.COL_KEY),
                new String[]{key},
                null,
                null,
                String.format("%s asc", Config.Schema.COL_ID),
                "1"
        );

        if (cursor == null || !cursor.moveToFirst()) return null;

        String value = cursor.getString(0);
        cursor.close();
        return value;
    }

    public String get(String key) {
        boolean found = true;
        String value = null;

        synchronized (mCache) {
            if(mCache.containsKey(key))
                value = mCache.get(key);
            else
                found = false;
        }

        if(!found) {
            int curVersion = mCacheVersion;
            value = fetchMissedCache(key);

            synchronized (mCache) {
                if(curVersion == mCacheVersion) {
                    if (mCache.containsKey(key))
                        value = mCache.get(key);
                    else
                        mCache.put(key, value);
                }
            }
        }

        return value;
    }

    public void set(String key, Object value) {
        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        ModelHelper.ConfigSetValue(db, key, value);

        synchronized (mCache) {
            mCache.remove(key);
            mCacheVersion++;
        }
    }

    public void delete(String key) {
        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();

        db.delete(Config.Schema.TABLE_NAME,
                String.format("%s < 0 and %s=?", Config.Schema.COL_ID, Config.Schema.COL_KEY),
                new String[]{key}
        );

        synchronized (mCache) {
            mCache.remove(key);
            mCacheVersion++;
        }
    }

    public int getInt(String key) {
        String value = get(key);
        if(value == null) return 0;

        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    public long getLong(String key) {
        String value = get(key);
        if(value == null) return 0;

        try {
            return Long.parseLong(value);
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    public double getDouble(String key) {
        String value = get(key);
        if(value == null) return 0.0;

        try {
            return Double.parseDouble(value);
        } catch(NumberFormatException e) {
            return 0.0;
        }
    }

}
