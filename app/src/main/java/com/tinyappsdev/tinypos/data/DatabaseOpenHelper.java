package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 11;
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
        Ticket.Schema.CreateTable(db);
        TicketFoodAttr.Schema.CreateTable(db);
        TicketFood.Schema.CreateTable(db);
        FoodAttr.Schema.CreateTable(db);
        FoodAttrGroup.Schema.CreateTable(db);
        Food.Schema.CreateTable(db);
        Menu.Schema.CreateTable(db);
        DineTable.Schema.CreateTable(db);
        Config.Schema.CreateTable(db);

        ModelHelper.ConfigSetValue(db, "syncAll", 1);
        mContext.getContentResolver().notifyChange(ContentProviderEx.BuildUri(Config.Schema.TABLE_NAME), null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Ticket.Schema.DropTable(db);
        TicketFoodAttr.Schema.DropTable(db);
        TicketFood.Schema.DropTable(db);
        FoodAttr.Schema.DropTable(db);
        FoodAttrGroup.Schema.DropTable(db);
        Food.Schema.DropTable(db);
        Menu.Schema.DropTable(db);
        DineTable.Schema.DropTable(db);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}