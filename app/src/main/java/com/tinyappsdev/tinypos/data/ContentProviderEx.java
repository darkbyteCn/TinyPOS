package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ContentProviderEx extends ContentProvider {

	public final static String AUTHORITY = "com.tinyappsdev.tinypos";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private final static int URI_TICKET = 0;
    private final static int URI_TICKET_RID = 1;
    private final static int URI_TICKETPAYMENT = 2;
    private final static int URI_TICKETPAYMENT_RID = 3;
    private final static int URI_TICKETFOODATTR = 4;
    private final static int URI_TICKETFOODATTR_RID = 5;
    private final static int URI_TICKETFOOD = 6;
    private final static int URI_TICKETFOOD_RID = 7;
    private final static int URI_FOODATTR = 8;
    private final static int URI_FOODATTR_RID = 9;
    private final static int URI_FOODATTRGROUP = 10;
    private final static int URI_FOODATTRGROUP_RID = 11;
    private final static int URI_FOOD = 12;
    private final static int URI_FOOD_RID = 13;
    private final static int URI_MENU = 14;
    private final static int URI_MENU_RID = 15;
    private final static int URI_DINETABLE = 16;
    private final static int URI_DINETABLE_RID = 17;
    private final static int URI_CONFIG = 18;
    private final static int URI_CONFIG_RID = 19;
    private final static int URI_CUSTOMER = 20;
    private final static int URI_CUSTOMER_RID = 21;

    private final static int URI_DINETABLE_TICKET = 1000;
    private final static int URI_DINETABLE_TICKET_RID = 1001;
    private final static int URI_MENU_FOOD = 1002;
    private final static int URI_MENU_FOOD_RID = 1003;

    public final static UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    protected DatabaseOpenHelper mDatabaseOpenHelper;
    protected ContentResolver mContentResolver;

    static {
        MATCHER.addURI(AUTHORITY, "Ticket", URI_TICKET);
        MATCHER.addURI(AUTHORITY, "Ticket/#", URI_TICKET_RID);
        MATCHER.addURI(AUTHORITY, "TicketPayment", URI_TICKETPAYMENT);
        MATCHER.addURI(AUTHORITY, "TicketPayment/#", URI_TICKETPAYMENT_RID);
        MATCHER.addURI(AUTHORITY, "TicketFoodAttr", URI_TICKETFOODATTR);
        MATCHER.addURI(AUTHORITY, "TicketFoodAttr/#", URI_TICKETFOODATTR_RID);
        MATCHER.addURI(AUTHORITY, "TicketFood", URI_TICKETFOOD);
        MATCHER.addURI(AUTHORITY, "TicketFood/#", URI_TICKETFOOD_RID);
        MATCHER.addURI(AUTHORITY, "FoodAttr", URI_FOODATTR);
        MATCHER.addURI(AUTHORITY, "FoodAttr/#", URI_FOODATTR_RID);
        MATCHER.addURI(AUTHORITY, "FoodAttrGroup", URI_FOODATTRGROUP);
        MATCHER.addURI(AUTHORITY, "FoodAttrGroup/#", URI_FOODATTRGROUP_RID);
        MATCHER.addURI(AUTHORITY, "Food", URI_FOOD);
        MATCHER.addURI(AUTHORITY, "Food/#", URI_FOOD_RID);
        MATCHER.addURI(AUTHORITY, "Menu", URI_MENU);
        MATCHER.addURI(AUTHORITY, "Menu/#", URI_MENU_RID);
        MATCHER.addURI(AUTHORITY, "DineTable", URI_DINETABLE);
        MATCHER.addURI(AUTHORITY, "DineTable/#", URI_DINETABLE_RID);
        MATCHER.addURI(AUTHORITY, "Config", URI_CONFIG);
        MATCHER.addURI(AUTHORITY, "Config/#", URI_CONFIG_RID);
        MATCHER.addURI(AUTHORITY, "Customer", URI_CUSTOMER);
        MATCHER.addURI(AUTHORITY, "Customer/#", URI_CUSTOMER_RID);
        MATCHER.addURI(AUTHORITY, "DineTable_Ticket", URI_DINETABLE_TICKET);
        MATCHER.addURI(AUTHORITY, "DineTable_Ticket/#", URI_DINETABLE_TICKET_RID);
        MATCHER.addURI(AUTHORITY, "Menu_Food", URI_MENU_FOOD);
        MATCHER.addURI(AUTHORITY, "Menu_Food/#", URI_MENU_FOOD_RID);
    }

    static class JoinedTable {
        String tableName;
        boolean isMajor;
        JoinedTable(String tableName, boolean isMajor) {
            this.tableName = tableName;
            this.isMajor = isMajor;
        }
    }
    final static Map<String, JoinedTable[]> JoinedTableMap = new HashMap<String, JoinedTable[]>();
    static {
        JoinedTableMap.put("DineTable", new JoinedTable[] {
            new JoinedTable("DineTable_Ticket", true)
        });
        JoinedTableMap.put("Ticket", new JoinedTable[] {
            new JoinedTable("DineTable_Ticket", false)
        });
        JoinedTableMap.put("Menu", new JoinedTable[] {
            new JoinedTable("Menu_Food", true)
        });
        JoinedTableMap.put("Food", new JoinedTable[] {
            new JoinedTable("Menu_Food", false)
        });
    }

    @Override
    public boolean onCreate() {
        mContentResolver = getContext().getContentResolver();
        mDatabaseOpenHelper = DatabaseOpenHelper.getInstance(getContext());
        return true;
    }

    public static Uri BuildUri(String... paths)
    {
        if(paths == null || paths.length == 0) return BASE_CONTENT_URI;

        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for(String path: paths)
            builder.appendPath(path);

        return builder.build();
    }

    public static Uri BuildUri(String[] paths, Object[][] params)
    {
        if(paths == null && params == null) return BASE_CONTENT_URI;

        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for(String path: paths)
            builder.appendPath(path);

        for(Object[] param: params)
            builder.appendQueryParameter(String.valueOf(param[0]),String.valueOf(param[1]));

        return builder.build();
    }

    @Override
    public String getType(Uri uri) {
        switch(MATCHER.match(uri)) {
            case URI_TICKET: {
                return "vnd.android.cursor.dir/Ticket";
            }
            case URI_TICKET_RID: {
                return "vnd.android.cursor.item/Ticket";
            }
            case URI_TICKETPAYMENT: {
                return "vnd.android.cursor.dir/TicketPayment";
            }
            case URI_TICKETPAYMENT_RID: {
                return "vnd.android.cursor.item/TicketPayment";
            }
            case URI_TICKETFOODATTR: {
                return "vnd.android.cursor.dir/TicketFoodAttr";
            }
            case URI_TICKETFOODATTR_RID: {
                return "vnd.android.cursor.item/TicketFoodAttr";
            }
            case URI_TICKETFOOD: {
                return "vnd.android.cursor.dir/TicketFood";
            }
            case URI_TICKETFOOD_RID: {
                return "vnd.android.cursor.item/TicketFood";
            }
            case URI_FOODATTR: {
                return "vnd.android.cursor.dir/FoodAttr";
            }
            case URI_FOODATTR_RID: {
                return "vnd.android.cursor.item/FoodAttr";
            }
            case URI_FOODATTRGROUP: {
                return "vnd.android.cursor.dir/FoodAttrGroup";
            }
            case URI_FOODATTRGROUP_RID: {
                return "vnd.android.cursor.item/FoodAttrGroup";
            }
            case URI_FOOD: {
                return "vnd.android.cursor.dir/Food";
            }
            case URI_FOOD_RID: {
                return "vnd.android.cursor.item/Food";
            }
            case URI_MENU: {
                return "vnd.android.cursor.dir/Menu";
            }
            case URI_MENU_RID: {
                return "vnd.android.cursor.item/Menu";
            }
            case URI_DINETABLE: {
                return "vnd.android.cursor.dir/DineTable";
            }
            case URI_DINETABLE_RID: {
                return "vnd.android.cursor.item/DineTable";
            }
            case URI_CONFIG: {
                return "vnd.android.cursor.dir/Config";
            }
            case URI_CONFIG_RID: {
                return "vnd.android.cursor.item/Config";
            }
            case URI_CUSTOMER: {
                return "vnd.android.cursor.dir/Customer";
            }
            case URI_CUSTOMER_RID: {
                return "vnd.android.cursor.item/Customer";
            }
            case URI_DINETABLE_TICKET: {
                return "vnd.android.cursor.dir/DineTable_Ticket";
            }
            case URI_DINETABLE_TICKET_RID: {
                return "vnd.android.cursor.item/DineTable_Ticket";
            }
            case URI_MENU_FOOD: {
                return "vnd.android.cursor.dir/Menu_Food";
            }
            case URI_MENU_FOOD_RID: {
                return "vnd.android.cursor.item/Menu_Food";
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    protected Object[] prepareSelection(Uri uri, String selection, String[] selectionArgs) {
        return prepareSelection(uri, selection, selectionArgs, "_id");
    }
    
    protected Object[] prepareSelection(Uri uri, String selection, String[] selectionArgs, String colId) {
        if(selection != null) {
            selection = String.format("%s=? AND (%s)", colId, selection);
            int argsLen = selectionArgs == null ? 0 : selectionArgs.length;
            String[] new_selectionArgs = new String[1 + argsLen];
            new_selectionArgs[0] = uri.getPathSegments().get(1);
            for(int i = 0; i < argsLen; i++)
                new_selectionArgs[1 + i] = selectionArgs[i];
            selectionArgs = new_selectionArgs;
        } else {
            selection = String.format("_id=?", colId);
            selectionArgs = new String[] {uri.getPathSegments().get(1)};
        }

        return new Object[] {selection, selectionArgs};
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = MATCHER.match(uri);
        if(code == -1 || code >= 1000)
            throw new IllegalArgumentException("Unknown URI " + uri);

        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        int count = 0;

        String tableName = uri.getPathSegments().get(0);
        if(code % 2 == 0) {
            count = db.update(tableName, values, selection, selectionArgs);
            if(count > 0) notifyChange(uri, code);

        } else {
            Object[] sel = prepareSelection(uri, selection, selectionArgs);
            selection = (String)sel[0];
            selectionArgs = (String[])sel[1];

            count = db.update(tableName, values, selection, selectionArgs);
            if(count > 0) notifyChange(uri, code);

        }

        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int code = MATCHER.match(uri);
        if(code == -1 || code >= 1000)
            throw new IllegalArgumentException("Unknown URI " + uri);

        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        Uri ret = null;

        String tableName = uri.getPathSegments().get(0);
        long id;
        if(uri.getBooleanQueryParameter("replace", false))
            id = db.replace(tableName, null, contentValues);
        else
            id = db.insert(tableName, null, contentValues);

        if(id >= 0) {
            notifyChange(uri, code);
            ret = BuildUri(tableName, "" + id);
        }

        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = MATCHER.match(uri);
        if(code == -1 || code >= 1000)
            throw new IllegalArgumentException("Unknown URI " + uri);

        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        int count = 0;

        String tableName = uri.getPathSegments().get(0);
        if(code % 2 == 0) {
            count = db.delete(tableName, selection, selectionArgs);
            if(count > 0) notifyChange(uri, code);

        } else {
            Object[] sel = prepareSelection(uri, selection, selectionArgs);
            selection = (String)sel[0];
            selectionArgs = (String[])sel[1];

            count = db.delete(tableName, selection, selectionArgs);
            if(count > 0) notifyChange(uri, code);

        }

        return count;
    }

    protected String[] getTableJoinInfo(int code) {
        switch(code) {
            case URI_DINETABLE_TICKET:
            case URI_DINETABLE_TICKET_RID: {
                return new String[] {
                    "DineTable left join Ticket ON (DineTable.ticketId=Ticket._id)",
                    "DineTable._id"
                };
            }
            case URI_MENU_FOOD:
            case URI_MENU_FOOD_RID: {
                return new String[] {
                    "Menu left join Food ON (Menu.foodId=Food._id)",
                    "Menu._id"
                };
            }
        }

        return null;
    }

    protected void notifyChange(Uri uri, int code) {
        mContentResolver.notifyChange(uri, null);

        String tableName = uri.getPathSegments().get(0);
        JoinedTable[] joinedTableArray = JoinedTableMap.get(tableName);
        if(joinedTableArray == null) return;
        
        for(JoinedTable joinedTable : joinedTableArray) {
            if(code % 2 == 0 || !joinedTable.isMajor)
                mContentResolver.notifyChange(BuildUri(joinedTable.tableName), null);
            else
                mContentResolver.notifyChange(BuildUri(joinedTable.tableName, uri.getPathSegments().get(1)), null);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = MATCHER.match(uri);
        if(code == -1)
            throw new IllegalArgumentException("Unknown URI " + uri);

        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = null;

        String tableName;
        String colId;
        if(code >= 1000) {
            String[] tableJoinInfo = getTableJoinInfo(code);
            tableName = tableJoinInfo[0];
            colId = tableJoinInfo[1];
        } else {
            tableName = uri.getPathSegments().get(0);
            colId = "_id";
        }

        if(code % 2 == 0) {
            cursor = db.query(tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
            );
            cursor.setNotificationUri(mContentResolver, uri);

        } else {
            Object[] sel = prepareSelection(uri, selection, selectionArgs, colId);
            selection = (String)sel[0];
            selectionArgs = (String[])sel[1];

            cursor = db.query(tableName,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        null
            );
            cursor.setNotificationUri(mContentResolver, uri);

        }

        return cursor;
    }

}
