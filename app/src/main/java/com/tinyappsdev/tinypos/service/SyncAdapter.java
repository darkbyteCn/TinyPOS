package com.tinyappsdev.tinypos.service;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.tinyappsdev.tinypos.data.ContentProviderEx;
import com.tinyappsdev.tinypos.helper.ConfigCache;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.rest.ApiCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


/*
The main purpose of this class is to synchronize all changes in the main server to local database.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final static String TAG = SyncAdapter.class.getSimpleName();

    private ContentResolver mContentResolver;

    private ApiCall mApiCall = new ApiCall();
    private ConfigCache mConfigCache;

    private long mLastDocEventId = -1;


    public SyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mConfigCache = ConfigCache.getInstance(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "SyncAdapter -> onPerformSync");

        if(bundle.getBoolean("syncAll") || mConfigCache.getInt("syncAll") != 0)
            syncAll();
        else
            syncChangesOnly(bundle.getLong("lastDocEventId"));
    }

    protected void syncAll() {
        mLastDocEventId = 0;
        JSONObject jsObject = mApiCall.callApiSync("DocEvent/getLastId", null);
        try {
            long lastId = jsObject.getLong("lastId");
            SyncHelper.syncAll(mApiCall, mContentResolver);

            mConfigCache.set("lastDocEventId", lastId);
            mConfigCache.set("syncAll", 0);
            mLastDocEventId = lastId;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void syncChangesOnly(long lastDocEventId) {
        if(mLastDocEventId < 0) mLastDocEventId = mConfigCache.getLong("lastDocEventId");
        if(lastDocEventId > 0 && lastDocEventId <= mLastDocEventId) return;

        long lastId = lastDocEventId;
        if(lastId <= 0) {
            JSONObject jsObject = mApiCall.callApiSync("DocEvent/getLastId", null);
            try {
                lastId = jsObject.getLong("lastId");
            } catch (JSONException e) {
                lastId = 0;
                e.printStackTrace();
            }
        }

        if(lastId - mLastDocEventId > 2000) {
            mConfigCache.set("syncAll", 1);
            Log.i(TAG, String.format("Sync All Required CUR(%s) LAST(%s)", mLastDocEventId, lastId));
            return;
        }

        Log.i("PKT", String.format(">>>>syncChangesOnly REQ(%s) CUR(%s)", lastDocEventId, mLastDocEventId));

        long fromId = mLastDocEventId;
        while(lastId < 0 || fromId < lastId) {
            JSONObject jsObject = mApiCall.callApiSync("DocEvent/getDocs",
                    new String[][]{
                            new String[]{"fromId", "" + fromId},
                    }
            );

            try {
                mContentResolver.applyBatch(ContentProviderEx.AUTHORITY, buildDbOperations(jsObject));

                fromId = jsObject.getLong("toId");
                lastId = jsObject.getLong("lastId");

                Log.i("PKT", String.format(">>>>syncChangesOnly UPDATED -> CUR(%s)", fromId));
                mConfigCache.set("lastDocEventId", fromId);
                mLastDocEventId = fromId;

            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                break;

            } catch (RemoteException e) {
                e.printStackTrace();
                break;

            } catch (OperationApplicationException e) {
                e.printStackTrace();
                break;

            }
        }
    }

    protected ArrayList<ContentProviderOperation> buildDbOperations(JSONObject jsObject) throws JSONException {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        JSONObject docsByColl = jsObject.getJSONObject("docsByColl");
        Iterator<String> keys = docsByColl.keys();
        while(keys.hasNext()) {
            String collection = keys.next();
            JSONObject docsByEvent = docsByColl.getJSONObject(collection);

            JSONArray updatedDocs = docsByEvent.getJSONArray("updated");
            for(int i = 0; i < updatedDocs.length(); i++) {
                JSONObject doc = updatedDocs.getJSONObject(i);
                if(doc.optInt("dbDeleted") > 0)
                    operations.add(ModelHelper.BuildOperationForDelete(collection, doc.getLong("_id")));
                else
                    operations.add(ModelHelper.BuildOperationForInsert(collection, doc));
            }

            JSONArray deletedDocIds = docsByEvent.getJSONArray("deleted");
            for(int i = 0; i < deletedDocIds.length(); i++)
                operations.add(
                        ModelHelper.BuildOperationForDelete(collection, deletedDocIds.getLong(i))
                );
        }

        return operations;
    }


}
