package com.tinyappsdev.tinypos.service;

//Auto-Generated, See Tools

import android.content.ContentResolver;
import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.rest.ApiCall;
import com.tinyappsdev.tinypos.data.ContentProviderEx;

import java.util.ArrayList;
import java.util.List;


public class SyncHelper {

	public static void syncAll(ApiCall apiCall, ContentResolver contentResolver) throws JSONException {
		syncTable(apiCall, contentResolver, "Ticket", null);
		syncTable(apiCall, contentResolver, "Food", null);
		syncTable(apiCall, contentResolver, "Menu", null);
		syncTable(apiCall, contentResolver, "DineTable", null);
		syncTable(apiCall, contentResolver, "Config", "_id>=0");
	}

	public static void syncTable(ApiCall apiCall, ContentResolver contentResolver, String tableName, String delSelection) throws JSONException {
		//delete all
		contentResolver.delete(ContentProviderEx.BuildUri(tableName), delSelection, null);

		long fromId = 0;
		while(true) {
			JSONObject resObject = apiCall.callApiSync(
				String.format("%s/getSyncDocs", tableName),
				new Object[][] {
					new Object[] {"pageSize", 100},
					new Object[] {"fromId", fromId},
				}
			);

			JSONArray docs = resObject.getJSONArray("docs");
			if(docs.length() <= 0) break;

			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();
			for(int i = 0; i < docs.length(); i++) {
				JSONObject doc = docs.getJSONObject(i);
				if(doc.optInt("dbDeleted") > 0) continue;
				contentValuesArray.add(ModelHelper.GetContentValuesFromJson(tableName, doc));
			}
			contentResolver.bulkInsert(
					ContentProviderEx.BuildUri(tableName),
					contentValuesArray.toArray(new ContentValues[contentValuesArray.size()])
			);
			
			if(docs.length() != 100) break;
			fromId = contentValuesArray.get(contentValuesArray.size() - 1).getAsLong("_id");
		}

	}

}
