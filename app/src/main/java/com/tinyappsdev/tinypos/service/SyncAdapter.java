package com.tinyappsdev.tinypos.service;

import android.accounts.Account;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.data.Config;
import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.DineTable;
import com.tinyappsdev.tinypos.data.Food;
import com.tinyappsdev.tinypos.data.Menu;
import com.tinyappsdev.tinypos.data.Ticket;
import com.tinyappsdev.tinypos.data.TicketFood;
import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.helper.TinyMap;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.Widget.OrderStatusWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/*
The main purpose of this class is to synchronize all changes in the main server to local database.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final static String TAG = SyncAdapter.class.getSimpleName();

    private ContentResolver mContentResolver;
    private AppGlobal mAppGlobal;
    private long mLastDocEventId = -1;


    public SyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mAppGlobal = AppGlobal.getInstance();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "SyncAdapter -> onPerformSync -> Start++");
        try {
            if (mAppGlobal.getSharedPreferences().getBoolean("resyncDatabase", false))
                syncAll();
            else
                syncChangesOnly(bundle.getLong("lastDocEventId"));

        } catch(SyncAdapterException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "SyncAdapter -> onPerformSync -> Done--");
    }

    public static class SyncAdapterException extends Exception {
        SyncAdapterException(String msg) {
            super(msg);
        }
    }

    public void sendWidgetUpdateRequest() {
        Context context = getContext();
        Intent intent = new Intent(OrderStatusWidget.ACTION_APPWIDGET_UPDATE)
                .setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    protected void syncAll() throws SyncAdapterException {
        long syncRequestTs = mAppGlobal.getSharedPreferences().getLong("syncRequestTs", 0);
        mLastDocEventId = 0;
        ApiCallClient.Result<Map> result = mAppGlobal.getBgApiCallClient().getDocEventLastId(null);
        if(result.error != null || result.data == null)
            throw new SyncAdapterException("syncAll -> Error " + result.error);

        TinyMap map = TinyMap.AsTinyMap(result.data);
        if(!map.hasKey("lastId"))
            throw new SyncAdapterException("syncAll -> Invalid Data");

        long lastId = map.getLong("lastId");
        syncAllTables(mContentResolver);

        if(mAppGlobal.getSharedPreferences().getLong("syncRequestTs", 0) != syncRequestTs) {
            Log.i(TAG, String.format("SyncAll Cancelled"));
            return;
        }

        mAppGlobal.getSharedPreferences().edit()
                .putLong("lastDocEventId", lastId)
                .remove("resyncDatabase")
                .apply();
        mLastDocEventId = lastId;
        Log.i(TAG, String.format("SyncAll Done -> SEQ(%s)", lastId));

        sendWidgetUpdateRequest();
    }

    protected void syncChangesOnly(long lastDocEventId) throws SyncAdapterException {
        if(mLastDocEventId < 0)
            mLastDocEventId = mAppGlobal.getSharedPreferences().getLong("lastDocEventId", 0);
        if(lastDocEventId > 0 && lastDocEventId <= mLastDocEventId) return;

        long lastId = lastDocEventId;
        if(lastId <= 0) {
            ApiCallClient.Result<Map> result = mAppGlobal.getBgApiCallClient().getDocEventLastId(null);
            if(result.error != null || result.data == null)
                throw new SyncAdapterException("syncChangesOnly -> Error " + result.error);

            TinyMap map = TinyMap.AsTinyMap(result.data);
            if(!map.hasKey("lastId"))
                throw new SyncAdapterException("syncChangesOnly -> Invalid Data");

            lastId = map.getLong("lastId");
        }

        if(lastId - mLastDocEventId > 2000) {
            mAppGlobal.getSharedPreferences().edit().putBoolean("resyncDatabase", true).apply();
            return;
        }

        int updatedCount = 0;
        long fromId = mLastDocEventId;
        while(lastId < 0 || fromId < lastId) {
            ApiCallClient.Result<Map> result = mAppGlobal.getBgApiCallClient().getDocEventDocs(fromId, null);
            if(result.error != null || result.data == null)
                throw new SyncAdapterException("syncChangesOnly -> Error " + result.error);

            TinyMap map = TinyMap.AsTinyMap(result.data);
            if(!map.hasKey("lastId"))
                throw new SyncAdapterException("syncChangesOnly -> Invalid Data");

            try {
                ArrayList arrayList = buildDbOperations(map);
                updatedCount += arrayList.size();
                mContentResolver.applyBatch(ContentProviderEx.AUTHORITY, arrayList);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                throw new SyncAdapterException("syncChangesOnly -> " + e.getMessage());
            }

            fromId = map.getLong("toId");
            lastId = map.getLong("lastId");
            Log.i(TAG, String.format("syncChangesOnly Done -> SEQ(%s)", fromId));
            mAppGlobal.getSharedPreferences().edit().putLong("lastDocEventId", fromId).apply();
            mLastDocEventId = fromId;
        }

        if(updatedCount > 0) sendWidgetUpdateRequest();
    }

    protected ArrayList<ContentProviderOperation> buildDbOperations(TinyMap map) throws SyncAdapterException {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        TinyMap docsByColl = map.getTinyMap("docsByColl");
        if(docsByColl == null) return operations;

        for(Map.Entry<String, Object> entry : ((Map<String, Object>)docsByColl.map()).entrySet()) {
            String collection = entry.getKey();
            if(!ModelHelper.SYNCABLE_TABLES.contains(collection)) {
                mContentResolver.notifyChange(ContentProviderEx.BuildUri(collection), null);
                continue;
            }

            boolean isTicket = collection.equals(Ticket.Schema.TABLE_NAME);

            TinyMap docsByEvent = TinyMap.AsTinyMap((Map)entry.getValue());
            if(docsByEvent == null) continue;

            TinyMap.TinyList updatedDocs = docsByEvent.getTinyList("updated");
            if(updatedDocs != null) {
                for (int i = 0; i < updatedDocs.list().size(); i++) {
                    TinyMap doc = updatedDocs.getTinyMap(i);
                    int state = doc.getInt(Ticket.Schema.COL_STATE);
                    if (doc.getInt("dbDeleted") > 0 || isTicket && (state & Ticket.STATE_COMPLETED) != 0)
                        operations.add(ModelHelper.BuildOperationForDelete(collection, doc.getLong("_id")));
                    else
                        operations.add(ModelHelper.BuildOperationForInsert(collection, doc));
                }
            }

            TinyMap.TinyList deletedDocIds = docsByEvent.getTinyList("deleted");
            if(deletedDocIds != null) {
                for (int i = 0; i < deletedDocIds.list().size(); i++)
                    operations.add(
                            ModelHelper.BuildOperationForDelete(collection, deletedDocIds.getLong(i))
                    );
            }

            if(isTicket)
                buildTicketPendingFood(updatedDocs, deletedDocIds, operations);
        }

        return operations;
    }

    protected void syncAllTables(ContentResolver contentResolver) throws SyncAdapterException {
        ModelHelper.clearAllTables(contentResolver);

        syncTable(contentResolver, Ticket.Schema.TABLE_NAME, null);
        syncTable(contentResolver, Food.Schema.TABLE_NAME, null);
        syncTable(contentResolver, Menu.Schema.TABLE_NAME, null);
        syncTable(contentResolver, DineTable.Schema.TABLE_NAME, null);
        syncTable(contentResolver, Config.Schema.TABLE_NAME, null);
    }

    protected void syncTable(ContentResolver contentResolver, String tableName, String delSelection) throws SyncAdapterException {
        boolean isTicket = tableName.equals(Ticket.Schema.TABLE_NAME);
        if(isTicket)
            contentResolver.delete(ContentProviderEx.BuildUri(TicketFood.Schema.TABLE_NAME), null, null);

        long fromId = 0;
        while(true) {
            ApiCallClient.Result<Map> result = mAppGlobal.getBgApiCallClient().makeCall(
                    String.format("/%s/getSyncDocs?limit=100&fromId=%d", tableName, fromId),
                    null,
                    Map.class
            );
            if(result.error != null || result.data == null)
                throw new SyncAdapterException("syncTable -> Error");

            if(!result.data.containsKey("docs"))
                throw new SyncAdapterException("syncTable -> Invaild Data");

            List docs = (List)result.data.get("docs");
            if(docs == null || docs.size() <= 0) break;

            List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();
            for(int i = 0; i < docs.size(); i++) {
                TinyMap doc = TinyMap.AsTinyMap((Map)docs.get(i));
                int state = doc.getInt(Ticket.Schema.COL_STATE);
                if(doc.getInt("dbDeleted") > 0 || isTicket && (state & Ticket.STATE_COMPLETED) != 0)
                    continue;
                contentValuesArray.add(ModelHelper.GetContentValuesFromJsonMap(tableName, doc));
            }
            contentResolver.bulkInsert(
                    ContentProviderEx.BuildUri(tableName),
                    contentValuesArray.toArray(new ContentValues[contentValuesArray.size()])
            );
            Log.i(TAG, String.format(
                    "syncTable -> %s -> %d doc(s)", tableName, contentValuesArray.size()
            ));

            if(isTicket) {
                ContentValues[] allPendingFoodList = getPendingFoodListFromTickets(docs);
                if(allPendingFoodList.length > 0)
                    contentResolver.bulkInsert(
                            ContentProviderEx.BuildUri(TicketFood.Schema.TABLE_NAME),
                            allPendingFoodList
                    );
            }

            if(docs.size() != 100) break;
            fromId = contentValuesArray.get(contentValuesArray.size() - 1).getAsLong("_id");
        }

    }

    protected ContentValues[] getPendingFoodListFromTickets(List docs) throws SyncAdapterException {
        List<ContentValues> allPendingFoodList = new ArrayList<ContentValues>();
        for(int i = 0; i < docs.size(); i++) {
            TinyMap doc = TinyMap.AsTinyMap((Map)docs.get(i));
            List<ContentValues> pendingFoodList = getPendingFoodList(doc);
            if(pendingFoodList != null) allPendingFoodList.addAll(pendingFoodList);
        }
        return allPendingFoodList.toArray(new ContentValues[allPendingFoodList.size()]);
    }

    protected List<ContentValues> getPendingFoodList(TinyMap doc) throws SyncAdapterException {
        if(doc.getInt("dbDeleted") > 0) return null;

        int state = doc.getInt(Ticket.Schema.COL_STATE);
        if((state & Ticket.STATE_COMPLETED) != 0) return null;

        TinyMap.TinyList ticketFoodList = doc.getTinyList(Ticket.Schema.COL_FOODITEMS);
        if(ticketFoodList == null) return null;

        long ticketId = doc.getLong("_id");
        long timeStamp = doc.getLong(Ticket.Schema.COL_CREATEDTIME);

        List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();
        for(int j = 0; j < ticketFoodList.list().size(); j++) {
            TinyMap ticketFood = TinyMap.AsTinyMap((Map)ticketFoodList.get(j));
            final int fulfilled = ticketFood.getInt(TicketFood.Schema.COL_FULFILLED);
            final int quantity = ticketFood.getInt(TicketFood.Schema.COL_QUANTITY);
            if(fulfilled >= quantity) continue;

            ticketFood.map().put(TicketFood.Schema.COL_TICKETID, ticketId);
            if(ticketFood.getLong(TicketFood.Schema.COL_CREATEDTIME) == 0)
                ticketFood.map().put(TicketFood.Schema.COL_CREATEDTIME, timeStamp);

            contentValuesArray.add(ModelHelper.TicketFoodContentValuesFromJsonMap(ticketFood));
        }

        return contentValuesArray;
    }

    protected void buildTicketPendingFood(TinyMap.TinyList updatedDocs,
                                          TinyMap.TinyList deletedDocIds,
                                          ArrayList<ContentProviderOperation> operations) throws SyncAdapterException {
        List<ContentValues> allPendingFoodList = new ArrayList<ContentValues>();
        ArrayList<Long> ids = new ArrayList<Long>();

        if(updatedDocs != null) {
            for (int i = 0; i < updatedDocs.list().size(); i++) {
                TinyMap doc = updatedDocs.getTinyMap(i);
                ids.add(doc.getLong("_id"));
                List<ContentValues> pendingFoodList = getPendingFoodList(doc);
                if (pendingFoodList != null) allPendingFoodList.addAll(pendingFoodList);
            }
        }

        if(deletedDocIds != null) {
            for (int i = 0; i < deletedDocIds.list().size(); i++)
                ids.add(deletedDocIds.getLong(i));
        }

        if(!ids.isEmpty()) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
                    ContentProviderEx.BuildUri(TicketFood.Schema.TABLE_NAME)
            );
            operations.add(
                    builder.withSelection(
                        String.format("%s in (%s)", TicketFood.Schema.COL_TICKETID, TextUtils.join(",", ids)),
                        null
                    ).build()
            );
        }

        for(ContentValues pendingFood : allPendingFoodList)
            operations.add(
                    ModelHelper.BuildOperationForInsert(TicketFood.Schema.TABLE_NAME, pendingFood)
            );
    }

}
