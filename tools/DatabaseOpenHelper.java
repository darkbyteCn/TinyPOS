package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tinyappsdev.tinypos.AppGlobal;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public final static String TAG = DatabaseOpenHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = ${this.version};
    public static final String DATABASE_NAME = "tinypos.db";
    private static DatabaseOpenHelper sInstance;

    private Context mContext;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
    }

    public static DatabaseOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DatabaseOpenHelper.class) {
                if (sInstance == null)
                    sInstance = new DatabaseOpenHelper(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
%for(var schema in this.schemas) {
        ${capitalize(schema)}.Schema.CreateTable(db);
%}

        Log.i(TAG, String.format("Created Database Version %s %s", DATABASE_NAME, DATABASE_VERSION));
        AppGlobal.getInstance().getSharedPreferences().edit()
                .putLong("syncRequestTs", System.currentTimeMillis())
                .putBoolean("resyncDatabase", true)
                .commit();
        mContext.getContentResolver().notifyChange(ContentProviderEx.BuildUri(Config.Schema.TABLE_NAME), null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
%for(var schema in this.schemas) {
%   if(schema == 'Config') continue;
        ${capitalize(schema)}.Schema.DropTable(db);
%}

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}