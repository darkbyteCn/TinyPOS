package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import org.json.JSONException;
import org.json.JSONObject;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ModelHelper {
	public final static TypeReference LIST_TICKETFOOD_TYPEREF = new TypeReference<List<TicketFood>>(){};
	public final static TypeReference LIST_TICKETFOODATTR_TYPEREF = new TypeReference<List<TicketFoodAttr>>(){};
	public final static TypeReference LIST_FOODATTR_TYPEREF = new TypeReference<List<FoodAttr>>(){};
	public final static TypeReference LIST_FOODATTRGROUP_TYPEREF = new TypeReference<List<FoodAttrGroup>>(){};

	public static ContentProviderOperation BuildOperationForDelete(String collection, long id) throws JSONException {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
			ContentProviderEx.BuildUri(collection, id + "")
		);
		return builder.build();
	}

	public static ContentProviderOperation BuildOperationForInsert(String collection, JSONObject doc) throws JSONException {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
			ContentProviderEx.BuildUri(
				new String[] {collection, doc.getLong("_id") + ""},
				new Object[][] { new Object[] {"replace", 1} }
				)
			);
		return builder.withValues(GetContentValuesFromJson(collection, doc)).build();
	}

	public static void ConfigSetValue(SQLiteDatabase db, String key, Object value) {
		String val = String.valueOf(value);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.Schema.COL_VAL, val);
        int count = db.update(Config.Schema.TABLE_NAME, contentValues,
                String.format("%s < 0 and %s=?", Config.Schema.COL_ID, Config.Schema.COL_KEY),
                new String[]{key}
        );

        if (count <= 0) {
            db.execSQL(
                    String.format("insert into %s select min(_id) - 1, ?, ? from %s",
                            Config.Schema.TABLE_NAME,
                            Config.Schema.TABLE_NAME
                    ),
                    new String[]{key, val}
            );
        }
	}

	public static ContentValues GetContentValuesFromJson(String collection, JSONObject jsonObject) throws JSONException {
		if(collection.equals("Ticket"))
			return TicketContentValuesFromJson(jsonObject);
		if(collection.equals("Food"))
			return FoodContentValuesFromJson(jsonObject);
		if(collection.equals("Menu"))
			return MenuContentValuesFromJson(jsonObject);
		if(collection.equals("DineTable"))
			return DineTableContentValuesFromJson(jsonObject);
		if(collection.equals("Config"))
			return ConfigContentValuesFromJson(jsonObject);
		return null;
	}

	public static Object fromJson(String jsonStr, TypeReference typeReference) {
		if(jsonStr == null) return null;
		try {
			return (new ObjectMapper()).readValue(jsonStr, typeReference);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Ticket TicketFromMap(Map map){
		Ticket obj = new Ticket();
		obj.setId(((Number)map.get("_id")).longValue());
		obj.setState(((Number)map.get("state")).intValue());
		obj.setTableId(((Number)map.get("tableId")).longValue());
		obj.setTableName((String)map.get("tableName"));
		obj.setEmployeeId(((Number)map.get("employeeId")).longValue());
		obj.setEmployeeName((String)map.get("employeeName"));
		obj.setCustomerId(((Number)map.get("customerId")).longValue());
		obj.setCustomerName((String)map.get("customerName"));
		obj.setNumFoodFullfilled(((Number)map.get("numFoodFullfilled")).intValue());
		obj.setNumFood(((Number)map.get("numFood")).intValue());
		obj.setNumGuest(((Number)map.get("numGuest")).intValue());
		obj.setCurItemId(((Number)map.get("curItemId")).intValue());
		obj.setFoodItems((List<TicketFood>)map.get("foodItems"));
		obj.setSubtotal(((Number)map.get("subtotal")).doubleValue());
		obj.setTips(((Number)map.get("tips")).doubleValue());
		obj.setFee(((Number)map.get("fee")).doubleValue());
		obj.setTax(((Number)map.get("tax")).doubleValue());
		obj.setTotal(((Number)map.get("total")).doubleValue());
		obj.setCreatedTime(((Number)map.get("createdTime")).longValue());
		obj.setDbRev(((Number)map.get("dbRev")).intValue());
		obj.setDbCreatedTime(((Number)map.get("dbCreatedTime")).longValue());
		obj.setDbModifiedTime(((Number)map.get("dbModifiedTime")).longValue());

		return obj;
    }

    public static Map TicketToMap(Ticket obj){
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("state", obj.getState());
		map.put("tableId", obj.getTableId());
		map.put("tableName", obj.getTableName());
		map.put("employeeId", obj.getEmployeeId());
		map.put("employeeName", obj.getEmployeeName());
		map.put("customerId", obj.getCustomerId());
		map.put("customerName", obj.getCustomerName());
		map.put("numFoodFullfilled", obj.getNumFoodFullfilled());
		map.put("numFood", obj.getNumFood());
		map.put("numGuest", obj.getNumGuest());
		map.put("curItemId", obj.getCurItemId());
		map.put("foodItems", obj.getFoodItems());
		map.put("subtotal", obj.getSubtotal());
		map.put("tips", obj.getTips());
		map.put("fee", obj.getFee());
		map.put("tax", obj.getTax());
		map.put("total", obj.getTotal());
		map.put("createdTime", obj.getCreatedTime());
		map.put("dbRev", obj.getDbRev());
		map.put("dbCreatedTime", obj.getDbCreatedTime());
		map.put("dbModifiedTime", obj.getDbModifiedTime());

		return map;
    }

    public static Ticket TicketFromCursor(Cursor cursor) {
    	return TicketFromCursor(cursor, "");
    }

    public static Ticket TicketFromCursor(Cursor cursor, String prefix) {
		Ticket m = new Ticket();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setState(cursor.getInt(cursor.getColumnIndex(prefix + "state")));
		m.setTableId(cursor.getLong(cursor.getColumnIndex(prefix + "tableId")));
		m.setTableName(cursor.getString(cursor.getColumnIndex(prefix + "tableName")));
		m.setEmployeeId(cursor.getLong(cursor.getColumnIndex(prefix + "employeeId")));
		m.setEmployeeName(cursor.getString(cursor.getColumnIndex(prefix + "employeeName")));
		m.setCustomerId(cursor.getLong(cursor.getColumnIndex(prefix + "customerId")));
		m.setCustomerName(cursor.getString(cursor.getColumnIndex(prefix + "customerName")));
		m.setNumFoodFullfilled(cursor.getInt(cursor.getColumnIndex(prefix + "numFoodFullfilled")));
		m.setNumFood(cursor.getInt(cursor.getColumnIndex(prefix + "numFood")));
		m.setNumGuest(cursor.getInt(cursor.getColumnIndex(prefix + "numGuest")));
		m.setCurItemId(cursor.getInt(cursor.getColumnIndex(prefix + "curItemId")));
		m.setFoodItems((List<TicketFood>)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "foodItems")),
			LIST_TICKETFOOD_TYPEREF
		));
		m.setSubtotal(cursor.getDouble(cursor.getColumnIndex(prefix + "subtotal")));
		m.setTips(cursor.getDouble(cursor.getColumnIndex(prefix + "tips")));
		m.setFee(cursor.getDouble(cursor.getColumnIndex(prefix + "fee")));
		m.setTax(cursor.getDouble(cursor.getColumnIndex(prefix + "tax")));
		m.setTotal(cursor.getDouble(cursor.getColumnIndex(prefix + "total")));
		m.setCreatedTime(cursor.getLong(cursor.getColumnIndex(prefix + "createdTime")));
		m.setDbRev(cursor.getInt(cursor.getColumnIndex(prefix + "dbRev")));
		m.setDbCreatedTime(cursor.getLong(cursor.getColumnIndex(prefix + "dbCreatedTime")));
		m.setDbModifiedTime(cursor.getLong(cursor.getColumnIndex(prefix + "dbModifiedTime")));

		return m;
    }

    public static ContentValues TicketContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("_id", jsonObject.optLong("_id", 0));
		m.put("state", jsonObject.optInt("state", 0));
		m.put("tableId", jsonObject.optLong("tableId", 0));
		m.put("tableName", jsonObject.optString("tableName", null));
		m.put("employeeId", jsonObject.optLong("employeeId", 0));
		m.put("employeeName", jsonObject.optString("employeeName", null));
		m.put("customerId", jsonObject.optLong("customerId", 0));
		m.put("customerName", jsonObject.optString("customerName", null));
		m.put("numFoodFullfilled", jsonObject.optInt("numFoodFullfilled", 0));
		m.put("numFood", jsonObject.optInt("numFood", 0));
		m.put("numGuest", jsonObject.optInt("numGuest", 0));
		m.put("curItemId", jsonObject.optInt("curItemId", 0));
		m.put("foodItems", jsonObject.optString("foodItems", null));
		m.put("subtotal", jsonObject.optDouble("subtotal", 0));
		m.put("tips", jsonObject.optDouble("tips", 0));
		m.put("fee", jsonObject.optDouble("fee", 0));
		m.put("tax", jsonObject.optDouble("tax", 0));
		m.put("total", jsonObject.optDouble("total", 0));
		m.put("createdTime", jsonObject.optLong("createdTime", 0));
		m.put("dbRev", jsonObject.optInt("dbRev", 0));
		m.put("dbCreatedTime", jsonObject.optLong("dbCreatedTime", 0));
		m.put("dbModifiedTime", jsonObject.optLong("dbModifiedTime", 0));

		return m;
    }

	public static TicketFoodAttr TicketFoodAttrFromMap(Map map){
		TicketFoodAttr obj = new TicketFoodAttr();
		obj.setName((String)map.get("name"));
		obj.setValue((String)map.get("value"));

		return obj;
    }

    public static Map TicketFoodAttrToMap(TicketFoodAttr obj){
		Map map = new HashMap();
		map.put("name", obj.getName());
		map.put("value", obj.getValue());

		return map;
    }

    public static TicketFoodAttr TicketFoodAttrFromCursor(Cursor cursor) {
    	return TicketFoodAttrFromCursor(cursor, "");
    }

    public static TicketFoodAttr TicketFoodAttrFromCursor(Cursor cursor, String prefix) {
		TicketFoodAttr m = new TicketFoodAttr();

		m.setName(cursor.getString(cursor.getColumnIndex(prefix + "name")));
		m.setValue(cursor.getString(cursor.getColumnIndex(prefix + "value")));

		return m;
    }

    public static ContentValues TicketFoodAttrContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("name", jsonObject.optString("name", null));
		m.put("value", jsonObject.optString("value", null));

		return m;
    }

	public static TicketFood TicketFoodFromMap(Map map){
		TicketFood obj = new TicketFood();
		obj.setId(((Number)map.get("_id")).longValue());
		obj.setItemId(((Number)map.get("itemId")).intValue());
		obj.setFoodName((String)map.get("foodName"));
		obj.setQuantity(((Number)map.get("quantity")).intValue());
		obj.setPrice(((Number)map.get("price")).doubleValue());
		obj.setExPrice(((Number)map.get("exPrice")).doubleValue());
		obj.setAttr((List<TicketFoodAttr>)map.get("attr"));
		obj.setTaxable(((Number)map.get("taxable")).intValue());

		return obj;
    }

    public static Map TicketFoodToMap(TicketFood obj){
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("itemId", obj.getItemId());
		map.put("foodName", obj.getFoodName());
		map.put("quantity", obj.getQuantity());
		map.put("price", obj.getPrice());
		map.put("exPrice", obj.getExPrice());
		map.put("attr", obj.getAttr());
		map.put("taxable", obj.getTaxable());

		return map;
    }

    public static TicketFood TicketFoodFromCursor(Cursor cursor) {
    	return TicketFoodFromCursor(cursor, "");
    }

    public static TicketFood TicketFoodFromCursor(Cursor cursor, String prefix) {
		TicketFood m = new TicketFood();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setItemId(cursor.getInt(cursor.getColumnIndex(prefix + "itemId")));
		m.setFoodName(cursor.getString(cursor.getColumnIndex(prefix + "foodName")));
		m.setQuantity(cursor.getInt(cursor.getColumnIndex(prefix + "quantity")));
		m.setPrice(cursor.getDouble(cursor.getColumnIndex(prefix + "price")));
		m.setExPrice(cursor.getDouble(cursor.getColumnIndex(prefix + "exPrice")));
		m.setAttr((List<TicketFoodAttr>)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "attr")),
			LIST_TICKETFOODATTR_TYPEREF
		));
		m.setTaxable(cursor.getInt(cursor.getColumnIndex(prefix + "taxable")));

		return m;
    }

    public static ContentValues TicketFoodContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("_id", jsonObject.optLong("_id", 0));
		m.put("itemId", jsonObject.optInt("itemId", 0));
		m.put("foodName", jsonObject.optString("foodName", null));
		m.put("quantity", jsonObject.optInt("quantity", 0));
		m.put("price", jsonObject.optDouble("price", 0));
		m.put("exPrice", jsonObject.optDouble("exPrice", 0));
		m.put("attr", jsonObject.optString("attr", null));
		m.put("taxable", jsonObject.optInt("taxable", 0));

		return m;
    }

	public static FoodAttr FoodAttrFromMap(Map map){
		FoodAttr obj = new FoodAttr();
		obj.setName((String)map.get("name"));
		obj.setPriceDiff(((Number)map.get("priceDiff")).doubleValue());

		return obj;
    }

    public static Map FoodAttrToMap(FoodAttr obj){
		Map map = new HashMap();
		map.put("name", obj.getName());
		map.put("priceDiff", obj.getPriceDiff());

		return map;
    }

    public static FoodAttr FoodAttrFromCursor(Cursor cursor) {
    	return FoodAttrFromCursor(cursor, "");
    }

    public static FoodAttr FoodAttrFromCursor(Cursor cursor, String prefix) {
		FoodAttr m = new FoodAttr();

		m.setName(cursor.getString(cursor.getColumnIndex(prefix + "name")));
		m.setPriceDiff(cursor.getDouble(cursor.getColumnIndex(prefix + "priceDiff")));

		return m;
    }

    public static ContentValues FoodAttrContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("name", jsonObject.optString("name", null));
		m.put("priceDiff", jsonObject.optDouble("priceDiff", 0));

		return m;
    }

	public static FoodAttrGroup FoodAttrGroupFromMap(Map map){
		FoodAttrGroup obj = new FoodAttrGroup();
		obj.setName((String)map.get("name"));
		obj.setAttr((List<FoodAttr>)map.get("attr"));

		return obj;
    }

    public static Map FoodAttrGroupToMap(FoodAttrGroup obj){
		Map map = new HashMap();
		map.put("name", obj.getName());
		map.put("attr", obj.getAttr());

		return map;
    }

    public static FoodAttrGroup FoodAttrGroupFromCursor(Cursor cursor) {
    	return FoodAttrGroupFromCursor(cursor, "");
    }

    public static FoodAttrGroup FoodAttrGroupFromCursor(Cursor cursor, String prefix) {
		FoodAttrGroup m = new FoodAttrGroup();

		m.setName(cursor.getString(cursor.getColumnIndex(prefix + "name")));
		m.setAttr((List<FoodAttr>)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "attr")),
			LIST_FOODATTR_TYPEREF
		));

		return m;
    }

    public static ContentValues FoodAttrGroupContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("name", jsonObject.optString("name", null));
		m.put("attr", jsonObject.optString("attr", null));

		return m;
    }

	public static Food FoodFromMap(Map map){
		Food obj = new Food();
		obj.setId(((Number)map.get("_id")).longValue());
		obj.setFoodName((String)map.get("foodName"));
		obj.setTaxable(((Number)map.get("taxable")).intValue());
		obj.setPrice(((Number)map.get("price")).doubleValue());
		obj.setDbRev(((Number)map.get("dbRev")).intValue());
		obj.setAttrGroup((List<FoodAttrGroup>)map.get("attrGroup"));

		return obj;
    }

    public static Map FoodToMap(Food obj){
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("foodName", obj.getFoodName());
		map.put("taxable", obj.getTaxable());
		map.put("price", obj.getPrice());
		map.put("dbRev", obj.getDbRev());
		map.put("attrGroup", obj.getAttrGroup());

		return map;
    }

    public static Food FoodFromCursor(Cursor cursor) {
    	return FoodFromCursor(cursor, "");
    }

    public static Food FoodFromCursor(Cursor cursor, String prefix) {
		Food m = new Food();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setFoodName(cursor.getString(cursor.getColumnIndex(prefix + "foodName")));
		m.setTaxable(cursor.getInt(cursor.getColumnIndex(prefix + "taxable")));
		m.setPrice(cursor.getDouble(cursor.getColumnIndex(prefix + "price")));
		m.setDbRev(cursor.getInt(cursor.getColumnIndex(prefix + "dbRev")));
		m.setAttrGroup((List<FoodAttrGroup>)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "attrGroup")),
			LIST_FOODATTRGROUP_TYPEREF
		));

		return m;
    }

    public static ContentValues FoodContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("_id", jsonObject.optLong("_id", 0));
		m.put("foodName", jsonObject.optString("foodName", null));
		m.put("taxable", jsonObject.optInt("taxable", 0));
		m.put("price", jsonObject.optDouble("price", 0));
		m.put("dbRev", jsonObject.optInt("dbRev", 0));
		m.put("attrGroup", jsonObject.optString("attrGroup", null));

		return m;
    }

	public static Menu MenuFromMap(Map map){
		Menu obj = new Menu();
		obj.setId(((Number)map.get("_id")).longValue());
		obj.setCategoryId(((Number)map.get("categoryId")).longValue());
		obj.setFoodId(((Number)map.get("foodId")).longValue());
		obj.setMenuName((String)map.get("menuName"));
		obj.setDbRev(((Number)map.get("dbRev")).intValue());

		return obj;
    }

    public static Map MenuToMap(Menu obj){
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("categoryId", obj.getCategoryId());
		map.put("foodId", obj.getFoodId());
		map.put("menuName", obj.getMenuName());
		map.put("dbRev", obj.getDbRev());

		return map;
    }

    public static Menu MenuFromCursor(Cursor cursor) {
    	return MenuFromCursor(cursor, "");
    }

    public static Menu MenuFromCursor(Cursor cursor, String prefix) {
		Menu m = new Menu();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setCategoryId(cursor.getLong(cursor.getColumnIndex(prefix + "categoryId")));
		m.setFoodId(cursor.getLong(cursor.getColumnIndex(prefix + "foodId")));
		m.setMenuName(cursor.getString(cursor.getColumnIndex(prefix + "menuName")));
		m.setDbRev(cursor.getInt(cursor.getColumnIndex(prefix + "dbRev")));

		return m;
    }

    public static ContentValues MenuContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("_id", jsonObject.optLong("_id", 0));
		m.put("categoryId", jsonObject.optLong("categoryId", 0));
		m.put("foodId", jsonObject.optLong("foodId", 0));
		m.put("menuName", jsonObject.optString("menuName", null));
		m.put("dbRev", jsonObject.optInt("dbRev", 0));

		return m;
    }

	public static DineTable DineTableFromMap(Map map){
		DineTable obj = new DineTable();
		obj.setId(((Number)map.get("_id")).longValue());
		obj.setName((String)map.get("name"));
		obj.setTicketId(((Number)map.get("ticketId")).longValue());
		obj.setMaxGuest(((Number)map.get("maxGuest")).intValue());
		obj.setDbRev(((Number)map.get("dbRev")).intValue());

		return obj;
    }

    public static Map DineTableToMap(DineTable obj){
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("name", obj.getName());
		map.put("ticketId", obj.getTicketId());
		map.put("maxGuest", obj.getMaxGuest());
		map.put("dbRev", obj.getDbRev());

		return map;
    }

    public static DineTable DineTableFromCursor(Cursor cursor) {
    	return DineTableFromCursor(cursor, "");
    }

    public static DineTable DineTableFromCursor(Cursor cursor, String prefix) {
		DineTable m = new DineTable();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setName(cursor.getString(cursor.getColumnIndex(prefix + "name")));
		m.setTicketId(cursor.getLong(cursor.getColumnIndex(prefix + "ticketId")));
		m.setMaxGuest(cursor.getInt(cursor.getColumnIndex(prefix + "maxGuest")));
		m.setDbRev(cursor.getInt(cursor.getColumnIndex(prefix + "dbRev")));

		return m;
    }

    public static ContentValues DineTableContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("_id", jsonObject.optLong("_id", 0));
		m.put("name", jsonObject.optString("name", null));
		m.put("ticketId", jsonObject.optLong("ticketId", 0));
		m.put("maxGuest", jsonObject.optInt("maxGuest", 0));
		m.put("dbRev", jsonObject.optInt("dbRev", 0));

		return m;
    }

	public static Config ConfigFromMap(Map map){
		Config obj = new Config();
		obj.setId(((Number)map.get("_id")).longValue());
		obj.setKey((String)map.get("key"));
		obj.setVal((String)map.get("val"));

		return obj;
    }

    public static Map ConfigToMap(Config obj){
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("key", obj.getKey());
		map.put("val", obj.getVal());

		return map;
    }

    public static Config ConfigFromCursor(Cursor cursor) {
    	return ConfigFromCursor(cursor, "");
    }

    public static Config ConfigFromCursor(Cursor cursor, String prefix) {
		Config m = new Config();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setKey(cursor.getString(cursor.getColumnIndex(prefix + "key")));
		m.setVal(cursor.getString(cursor.getColumnIndex(prefix + "val")));

		return m;
    }

    public static ContentValues ConfigContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

		m.put("_id", jsonObject.optLong("_id", 0));
		m.put("key", jsonObject.optString("key", null));
		m.put("val", jsonObject.optString("val", null));

		return m;
    }


	public static class TicketCursor {
		private Cursor mCursor;
		private String mPrefix;
		public TicketCursor(Cursor cursor) { this(cursor, ""); }
		public TicketCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public int getState() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "state"));
		}
		public long getTableId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "tableId"));
		}
		public String getTableName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "tableName"));
		}
		public long getEmployeeId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "employeeId"));
		}
		public String getEmployeeName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "employeeName"));
		}
		public long getCustomerId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "customerId"));
		}
		public String getCustomerName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "customerName"));
		}
		public int getNumFoodFullfilled() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "numFoodFullfilled"));
		}
		public int getNumFood() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "numFood"));
		}
		public int getNumGuest() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "numGuest"));
		}
		public int getCurItemId() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "curItemId"));
		}
		public List<TicketFood> getFoodItems() {
			return (List<TicketFood>)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "foodItems")),
				LIST_TICKETFOOD_TYPEREF
			);
		}
		public double getSubtotal() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "subtotal"));
		}
		public double getTips() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "tips"));
		}
		public double getFee() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "fee"));
		}
		public double getTax() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "tax"));
		}
		public double getTotal() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "total"));
		}
		public long getCreatedTime() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "createdTime"));
		}
		public int getDbRev() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "dbRev"));
		}
		public long getDbCreatedTime() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "dbCreatedTime"));
		}
		public long getDbModifiedTime() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "dbModifiedTime"));
		}

	}

	public static class TicketFoodAttrCursor {
		private Cursor mCursor;
		private String mPrefix;
		public TicketFoodAttrCursor(Cursor cursor) { this(cursor, ""); }
		public TicketFoodAttrCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public String getName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "name"));
		}
		public String getValue() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "value"));
		}

	}

	public static class TicketFoodCursor {
		private Cursor mCursor;
		private String mPrefix;
		public TicketFoodCursor(Cursor cursor) { this(cursor, ""); }
		public TicketFoodCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public int getItemId() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "itemId"));
		}
		public String getFoodName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "foodName"));
		}
		public int getQuantity() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "quantity"));
		}
		public double getPrice() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "price"));
		}
		public double getExPrice() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "exPrice"));
		}
		public List<TicketFoodAttr> getAttr() {
			return (List<TicketFoodAttr>)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "attr")),
				LIST_TICKETFOODATTR_TYPEREF
			);
		}
		public int getTaxable() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "taxable"));
		}

	}

	public static class FoodAttrCursor {
		private Cursor mCursor;
		private String mPrefix;
		public FoodAttrCursor(Cursor cursor) { this(cursor, ""); }
		public FoodAttrCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public String getName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "name"));
		}
		public double getPriceDiff() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "priceDiff"));
		}

	}

	public static class FoodAttrGroupCursor {
		private Cursor mCursor;
		private String mPrefix;
		public FoodAttrGroupCursor(Cursor cursor) { this(cursor, ""); }
		public FoodAttrGroupCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public String getName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "name"));
		}
		public List<FoodAttr> getAttr() {
			return (List<FoodAttr>)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "attr")),
				LIST_FOODATTR_TYPEREF
			);
		}

	}

	public static class FoodCursor {
		private Cursor mCursor;
		private String mPrefix;
		public FoodCursor(Cursor cursor) { this(cursor, ""); }
		public FoodCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public String getFoodName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "foodName"));
		}
		public int getTaxable() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "taxable"));
		}
		public double getPrice() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "price"));
		}
		public int getDbRev() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "dbRev"));
		}
		public List<FoodAttrGroup> getAttrGroup() {
			return (List<FoodAttrGroup>)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "attrGroup")),
				LIST_FOODATTRGROUP_TYPEREF
			);
		}

	}

	public static class MenuCursor {
		private Cursor mCursor;
		private String mPrefix;
		public MenuCursor(Cursor cursor) { this(cursor, ""); }
		public MenuCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public long getCategoryId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "categoryId"));
		}
		public long getFoodId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "foodId"));
		}
		public String getMenuName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "menuName"));
		}
		public int getDbRev() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "dbRev"));
		}

	}

	public static class DineTableCursor {
		private Cursor mCursor;
		private String mPrefix;
		public DineTableCursor(Cursor cursor) { this(cursor, ""); }
		public DineTableCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public String getName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "name"));
		}
		public long getTicketId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "ticketId"));
		}
		public int getMaxGuest() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "maxGuest"));
		}
		public int getDbRev() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "dbRev"));
		}

	}

	public static class ConfigCursor {
		private Cursor mCursor;
		private String mPrefix;
		public ConfigCursor(Cursor cursor) { this(cursor, ""); }
		public ConfigCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public String getKey() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "key"));
		}
		public String getVal() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "val"));
		}

	}


}
