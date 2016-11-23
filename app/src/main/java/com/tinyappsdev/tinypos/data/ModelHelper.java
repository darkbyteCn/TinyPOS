package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import org.json.JSONException;
import org.json.JSONObject;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyappsdev.tinypos.helper.TinyMap;


public class ModelHelper {

	public final static Set<String> SYNCABLE_TABLES = 
			new HashSet(Arrays.asList(new String[] {"Ticket","Food","Menu","DineTable","Config"}));
	public final static String SYNCABLE_TABLES_QUERY = "Ticket,Food,Menu,DineTable,Config";

	public final static TypeReference CUSTOMER_TYPEREF = new TypeReference<Customer>(){};
	public final static TypeReference LIST_TICKETFOOD_TYPEREF = new TypeReference<List<TicketFood>>(){};
	public final static TypeReference LIST_TICKETPAYMENT_TYPEREF = new TypeReference<List<TicketPayment>>(){};
	public final static TypeReference LIST_TICKETFOODATTR_TYPEREF = new TypeReference<List<TicketFoodAttr>>(){};
	public final static TypeReference LIST_FOODATTR_TYPEREF = new TypeReference<List<FoodAttr>>(){};
	public final static TypeReference LIST_FOODATTRGROUP_TYPEREF = new TypeReference<List<FoodAttrGroup>>(){};

	final static ObjectMapper sObjectMapper = new ObjectMapper();
    static {
        sObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

	public static ContentProviderOperation BuildOperationForDelete(String collection, long id) {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
			ContentProviderEx.BuildUri(collection, id + "")
		);
		return builder.build();
	}

	public static ContentProviderOperation BuildOperationForInsert(String collection, TinyMap doc) {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
			ContentProviderEx.BuildUri(
				new String[] {collection, doc.getLong("_id") + ""},
				new Object[][] { new Object[] {"replace", 1} }
				)
			);
		return builder.withValues(GetContentValuesFromJsonMap(collection, doc)).build();
	}

	public static ContentProviderOperation BuildOperationForInsert(String collection, ContentValues doc) {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
			ContentProviderEx.BuildUri(
				new String[] {collection, doc.getAsLong("_id") + ""},
				new Object[][] { new Object[] {"replace", 1} }
				)
			);
		return builder.withValues(doc).build();
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

	public static void clearAllTables(ContentResolver contentResolver) {
		contentResolver.delete(ContentProviderEx.BuildUri("Ticket"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("TicketPayment"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("TicketFoodAttr"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("TicketFood"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("FoodAttr"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("FoodAttrGroup"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("Food"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("Menu"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("DineTable"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("Config"), null, null);
		contentResolver.delete(ContentProviderEx.BuildUri("Customer"), null, null);
	}

	public static ContentValues GetContentValuesFromJsonMap(String collection, TinyMap map) {
		if(collection.equals("Ticket"))
			return TicketContentValuesFromJsonMap(map);
		if(collection.equals("Food"))
			return FoodContentValuesFromJsonMap(map);
		if(collection.equals("Menu"))
			return MenuContentValuesFromJsonMap(map);
		if(collection.equals("DineTable"))
			return DineTableContentValuesFromJsonMap(map);
		if(collection.equals("Config"))
			return ConfigContentValuesFromJsonMap(map);
		return null;
	}

	public static ObjectMapper getObjectMapper() { return sObjectMapper; }

	public static String toJson(Object obj) {
		try {
			return obj == null ? null : sObjectMapper.writeValueAsString(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <V> V fromJson(String jsonStr, Class<V> type) {
		try {
			return jsonStr == null ? null : sObjectMapper.readValue(jsonStr, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object fromJson(String jsonStr, TypeReference typeReference) {
		try {
			return jsonStr == null ? null : sObjectMapper.readValue(jsonStr, typeReference);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Ticket TicketFromMap(Map map) {
		Ticket obj = new Ticket();
		obj.setId((long)map.get("_id"));
		obj.setState((int)map.get("state"));
		obj.setTableId((long)map.get("tableId"));
		obj.setTableName((String)map.get("tableName"));
		obj.setEmployeeId((long)map.get("employeeId"));
		obj.setEmployeeName((String)map.get("employeeName"));
		obj.setCustomer((Customer)map.get("customer"));
		obj.setNumFoodFullfilled((int)map.get("numFoodFullfilled"));
		obj.setNumFood((int)map.get("numFood"));
		obj.setNumGuest((int)map.get("numGuest"));
		obj.setCurItemId((int)map.get("curItemId"));
		obj.setFoodItems((List<TicketFood>)map.get("foodItems"));
		obj.setSubtotal((double)map.get("subtotal"));
		obj.setTips((double)map.get("tips"));
		obj.setFee((double)map.get("fee"));
		obj.setTax((double)map.get("tax"));
		obj.setTotal((double)map.get("total"));
		obj.setBalance((double)map.get("balance"));
		obj.setPayments((List<TicketPayment>)map.get("payments"));
		obj.setCreatedTime((long)map.get("createdTime"));
		obj.setNotes((String)map.get("notes"));
		obj.setDbRev((int)map.get("dbRev"));
		obj.setDbCreatedTime((long)map.get("dbCreatedTime"));
		obj.setDbModifiedTime((long)map.get("dbModifiedTime"));

		return obj;
    }

    public static Map TicketToMap(Ticket obj) {
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("state", obj.getState());
		map.put("tableId", obj.getTableId());
		map.put("tableName", obj.getTableName());
		map.put("employeeId", obj.getEmployeeId());
		map.put("employeeName", obj.getEmployeeName());
		map.put("customer", obj.getCustomer());
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
		map.put("balance", obj.getBalance());
		map.put("payments", obj.getPayments());
		map.put("createdTime", obj.getCreatedTime());
		map.put("notes", obj.getNotes());
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
		m.setCustomer((Customer)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "customer")),
			CUSTOMER_TYPEREF
		));
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
		m.setBalance(cursor.getDouble(cursor.getColumnIndex(prefix + "balance")));
		m.setPayments((List<TicketPayment>)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "payments")),
			LIST_TICKETPAYMENT_TYPEREF
		));
		m.setCreatedTime(cursor.getLong(cursor.getColumnIndex(prefix + "createdTime")));
		m.setNotes(cursor.getString(cursor.getColumnIndex(prefix + "notes")));
		m.setDbRev(cursor.getInt(cursor.getColumnIndex(prefix + "dbRev")));
		m.setDbCreatedTime(cursor.getLong(cursor.getColumnIndex(prefix + "dbCreatedTime")));
		m.setDbModifiedTime(cursor.getLong(cursor.getColumnIndex(prefix + "dbModifiedTime")));

		return m;
    }

    public static ContentValues TicketContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("state", map.getInt("state"));
		m.put("tableId", map.getLong("tableId"));
		m.put("tableName", map.getString("tableName"));
		m.put("employeeId", map.getLong("employeeId"));
		m.put("employeeName", map.getString("employeeName"));
		m.put("customer", toJson(map.get("customer")));
		m.put("numFoodFullfilled", map.getInt("numFoodFullfilled"));
		m.put("numFood", map.getInt("numFood"));
		m.put("numGuest", map.getInt("numGuest"));
		m.put("curItemId", map.getInt("curItemId"));
		m.put("foodItems", toJson(map.get("foodItems")));
		m.put("subtotal", map.getDouble("subtotal"));
		m.put("tips", map.getDouble("tips"));
		m.put("fee", map.getDouble("fee"));
		m.put("tax", map.getDouble("tax"));
		m.put("total", map.getDouble("total"));
		m.put("balance", map.getDouble("balance"));
		m.put("payments", toJson(map.get("payments")));
		m.put("createdTime", map.getLong("createdTime"));
		m.put("notes", map.getString("notes"));
		m.put("dbRev", map.getInt("dbRev"));
		m.put("dbCreatedTime", map.getLong("dbCreatedTime"));
		m.put("dbModifiedTime", map.getLong("dbModifiedTime"));

		return m;
    }

	public static TicketPayment TicketPaymentFromMap(Map map) {
		TicketPayment obj = new TicketPayment();
		obj.setId((long)map.get("_id"));
		obj.setType((int)map.get("type"));
		obj.setAmount((double)map.get("amount"));
		obj.setTender((double)map.get("tender"));
		obj.setCreatedTime((long)map.get("createdTime"));

		return obj;
    }

    public static Map TicketPaymentToMap(TicketPayment obj) {
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("type", obj.getType());
		map.put("amount", obj.getAmount());
		map.put("tender", obj.getTender());
		map.put("createdTime", obj.getCreatedTime());

		return map;
    }

    public static TicketPayment TicketPaymentFromCursor(Cursor cursor) {
    	return TicketPaymentFromCursor(cursor, "");
    }

    public static TicketPayment TicketPaymentFromCursor(Cursor cursor, String prefix) {
		TicketPayment m = new TicketPayment();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setType(cursor.getInt(cursor.getColumnIndex(prefix + "type")));
		m.setAmount(cursor.getDouble(cursor.getColumnIndex(prefix + "amount")));
		m.setTender(cursor.getDouble(cursor.getColumnIndex(prefix + "tender")));
		m.setCreatedTime(cursor.getLong(cursor.getColumnIndex(prefix + "createdTime")));

		return m;
    }

    public static ContentValues TicketPaymentContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("type", map.getInt("type"));
		m.put("amount", map.getDouble("amount"));
		m.put("tender", map.getDouble("tender"));
		m.put("createdTime", map.getLong("createdTime"));

		return m;
    }

	public static TicketFoodAttr TicketFoodAttrFromMap(Map map) {
		TicketFoodAttr obj = new TicketFoodAttr();
		obj.setName((String)map.get("name"));
		obj.setValue((String)map.get("value"));

		return obj;
    }

    public static Map TicketFoodAttrToMap(TicketFoodAttr obj) {
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

    public static ContentValues TicketFoodAttrContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("name", map.getString("name"));
		m.put("value", map.getString("value"));

		return m;
    }

	public static TicketFood TicketFoodFromMap(Map map) {
		TicketFood obj = new TicketFood();
		obj.setId((long)map.get("_id"));
		obj.setTicketId((long)map.get("ticketId"));
		obj.setItemId((int)map.get("itemId"));
		obj.setFoodName((String)map.get("foodName"));
		obj.setQuantity((int)map.get("quantity"));
		obj.setFulfilled((int)map.get("fulfilled"));
		obj.setPrice((double)map.get("price"));
		obj.setExPrice((double)map.get("exPrice"));
		obj.setTaxRate((double)map.get("taxRate"));
		obj.setAttr((List<TicketFoodAttr>)map.get("attr"));
		obj.setCreatedTime((long)map.get("createdTime"));

		return obj;
    }

    public static Map TicketFoodToMap(TicketFood obj) {
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("ticketId", obj.getTicketId());
		map.put("itemId", obj.getItemId());
		map.put("foodName", obj.getFoodName());
		map.put("quantity", obj.getQuantity());
		map.put("fulfilled", obj.getFulfilled());
		map.put("price", obj.getPrice());
		map.put("exPrice", obj.getExPrice());
		map.put("taxRate", obj.getTaxRate());
		map.put("attr", obj.getAttr());
		map.put("createdTime", obj.getCreatedTime());

		return map;
    }

    public static TicketFood TicketFoodFromCursor(Cursor cursor) {
    	return TicketFoodFromCursor(cursor, "");
    }

    public static TicketFood TicketFoodFromCursor(Cursor cursor, String prefix) {
		TicketFood m = new TicketFood();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setTicketId(cursor.getLong(cursor.getColumnIndex(prefix + "ticketId")));
		m.setItemId(cursor.getInt(cursor.getColumnIndex(prefix + "itemId")));
		m.setFoodName(cursor.getString(cursor.getColumnIndex(prefix + "foodName")));
		m.setQuantity(cursor.getInt(cursor.getColumnIndex(prefix + "quantity")));
		m.setFulfilled(cursor.getInt(cursor.getColumnIndex(prefix + "fulfilled")));
		m.setPrice(cursor.getDouble(cursor.getColumnIndex(prefix + "price")));
		m.setExPrice(cursor.getDouble(cursor.getColumnIndex(prefix + "exPrice")));
		m.setTaxRate(cursor.getDouble(cursor.getColumnIndex(prefix + "taxRate")));
		m.setAttr((List<TicketFoodAttr>)fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "attr")),
			LIST_TICKETFOODATTR_TYPEREF
		));
		m.setCreatedTime(cursor.getLong(cursor.getColumnIndex(prefix + "createdTime")));

		return m;
    }

    public static ContentValues TicketFoodContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("ticketId", map.getLong("ticketId"));
		m.put("itemId", map.getInt("itemId"));
		m.put("foodName", map.getString("foodName"));
		m.put("quantity", map.getInt("quantity"));
		m.put("fulfilled", map.getInt("fulfilled"));
		m.put("price", map.getDouble("price"));
		m.put("exPrice", map.getDouble("exPrice"));
		m.put("taxRate", map.getDouble("taxRate"));
		m.put("attr", toJson(map.get("attr")));
		m.put("createdTime", map.getLong("createdTime"));

		return m;
    }

	public static FoodAttr FoodAttrFromMap(Map map) {
		FoodAttr obj = new FoodAttr();
		obj.setName((String)map.get("name"));
		obj.setPriceDiff((double)map.get("priceDiff"));

		return obj;
    }

    public static Map FoodAttrToMap(FoodAttr obj) {
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

    public static ContentValues FoodAttrContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("name", map.getString("name"));
		m.put("priceDiff", map.getDouble("priceDiff"));

		return m;
    }

	public static FoodAttrGroup FoodAttrGroupFromMap(Map map) {
		FoodAttrGroup obj = new FoodAttrGroup();
		obj.setName((String)map.get("name"));
		obj.setAttr((List<FoodAttr>)map.get("attr"));

		return obj;
    }

    public static Map FoodAttrGroupToMap(FoodAttrGroup obj) {
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

    public static ContentValues FoodAttrGroupContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("name", map.getString("name"));
		m.put("attr", toJson(map.get("attr")));

		return m;
    }

	public static Food FoodFromMap(Map map) {
		Food obj = new Food();
		obj.setId((long)map.get("_id"));
		obj.setFoodName((String)map.get("foodName"));
		obj.setTaxable((int)map.get("taxable"));
		obj.setPrice((double)map.get("price"));
		obj.setDbRev((int)map.get("dbRev"));
		obj.setAttrGroup((List<FoodAttrGroup>)map.get("attrGroup"));

		return obj;
    }

    public static Map FoodToMap(Food obj) {
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

    public static ContentValues FoodContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("foodName", map.getString("foodName"));
		m.put("taxable", map.getInt("taxable"));
		m.put("price", map.getDouble("price"));
		m.put("dbRev", map.getInt("dbRev"));
		m.put("attrGroup", toJson(map.get("attrGroup")));

		return m;
    }

	public static Menu MenuFromMap(Map map) {
		Menu obj = new Menu();
		obj.setId((long)map.get("_id"));
		obj.setCategoryId((long)map.get("categoryId"));
		obj.setFoodId((long)map.get("foodId"));
		obj.setMenuName((String)map.get("menuName"));
		obj.setDbRev((int)map.get("dbRev"));

		return obj;
    }

    public static Map MenuToMap(Menu obj) {
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

    public static ContentValues MenuContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("categoryId", map.getLong("categoryId"));
		m.put("foodId", map.getLong("foodId"));
		m.put("menuName", map.getString("menuName"));
		m.put("dbRev", map.getInt("dbRev"));

		return m;
    }

	public static DineTable DineTableFromMap(Map map) {
		DineTable obj = new DineTable();
		obj.setId((long)map.get("_id"));
		obj.setName((String)map.get("name"));
		obj.setTicketId((long)map.get("ticketId"));
		obj.setMaxGuest((int)map.get("maxGuest"));
		obj.setDbRev((int)map.get("dbRev"));

		return obj;
    }

    public static Map DineTableToMap(DineTable obj) {
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

    public static ContentValues DineTableContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("name", map.getString("name"));
		m.put("ticketId", map.getLong("ticketId"));
		m.put("maxGuest", map.getInt("maxGuest"));
		m.put("dbRev", map.getInt("dbRev"));

		return m;
    }

	public static Config ConfigFromMap(Map map) {
		Config obj = new Config();
		obj.setId((long)map.get("_id"));
		obj.setKey((String)map.get("key"));
		obj.setVal((String)map.get("val"));

		return obj;
    }

    public static Map ConfigToMap(Config obj) {
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

    public static ContentValues ConfigContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("key", map.getString("key"));
		m.put("val", map.getString("val"));

		return m;
    }

	public static Customer CustomerFromMap(Map map) {
		Customer obj = new Customer();
		obj.setId((long)map.get("_id"));
		obj.setName((String)map.get("name"));
		obj.setAddress((String)map.get("address"));
		obj.setAddress2((String)map.get("address2"));
		obj.setCity((String)map.get("city"));
		obj.setState((String)map.get("state"));
		obj.setZipCode((String)map.get("zipCode"));
		obj.setPhone((String)map.get("phone"));
		obj.setDbRev((int)map.get("dbRev"));

		return obj;
    }

    public static Map CustomerToMap(Customer obj) {
		Map map = new HashMap();
		map.put("_id", obj.getId());
		map.put("name", obj.getName());
		map.put("address", obj.getAddress());
		map.put("address2", obj.getAddress2());
		map.put("city", obj.getCity());
		map.put("state", obj.getState());
		map.put("zipCode", obj.getZipCode());
		map.put("phone", obj.getPhone());
		map.put("dbRev", obj.getDbRev());

		return map;
    }

    public static Customer CustomerFromCursor(Cursor cursor) {
    	return CustomerFromCursor(cursor, "");
    }

    public static Customer CustomerFromCursor(Cursor cursor, String prefix) {
		Customer m = new Customer();

		m.setId(cursor.getLong(cursor.getColumnIndex(prefix + "_id")));
		m.setName(cursor.getString(cursor.getColumnIndex(prefix + "name")));
		m.setAddress(cursor.getString(cursor.getColumnIndex(prefix + "address")));
		m.setAddress2(cursor.getString(cursor.getColumnIndex(prefix + "address2")));
		m.setCity(cursor.getString(cursor.getColumnIndex(prefix + "city")));
		m.setState(cursor.getString(cursor.getColumnIndex(prefix + "state")));
		m.setZipCode(cursor.getString(cursor.getColumnIndex(prefix + "zipCode")));
		m.setPhone(cursor.getString(cursor.getColumnIndex(prefix + "phone")));
		m.setDbRev(cursor.getInt(cursor.getColumnIndex(prefix + "dbRev")));

		return m;
    }

    public static ContentValues CustomerContentValuesFromJsonMap(TinyMap map) {
    	ContentValues m = new ContentValues();

		m.put("_id", map.getLong("_id"));
		m.put("name", map.getString("name"));
		m.put("address", map.getString("address"));
		m.put("address2", map.getString("address2"));
		m.put("city", map.getString("city"));
		m.put("state", map.getString("state"));
		m.put("zipCode", map.getString("zipCode"));
		m.put("phone", map.getString("phone"));
		m.put("dbRev", map.getInt("dbRev"));

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
		public Customer getCustomer() {
			return (Customer)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "customer")),
				CUSTOMER_TYPEREF
			);
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
		public double getBalance() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "balance"));
		}
		public List<TicketPayment> getPayments() {
			return (List<TicketPayment>)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "payments")),
				LIST_TICKETPAYMENT_TYPEREF
			);
		}
		public long getCreatedTime() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "createdTime"));
		}
		public String getNotes() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "notes"));
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

	public static class TicketPaymentCursor {
		private Cursor mCursor;
		private String mPrefix;
		public TicketPaymentCursor(Cursor cursor) { this(cursor, ""); }
		public TicketPaymentCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public int getType() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "type"));
		}
		public double getAmount() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "amount"));
		}
		public double getTender() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "tender"));
		}
		public long getCreatedTime() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "createdTime"));
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
		public long getTicketId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "ticketId"));
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
		public int getFulfilled() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "fulfilled"));
		}
		public double getPrice() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "price"));
		}
		public double getExPrice() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "exPrice"));
		}
		public double getTaxRate() {
			return mCursor.getDouble(mCursor.getColumnIndex(mPrefix + "taxRate"));
		}
		public List<TicketFoodAttr> getAttr() {
			return (List<TicketFoodAttr>)fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "attr")),
				LIST_TICKETFOODATTR_TYPEREF
			);
		}
		public long getCreatedTime() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "createdTime"));
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

	public static class CustomerCursor {
		private Cursor mCursor;
		private String mPrefix;
		public CustomerCursor(Cursor cursor) { this(cursor, ""); }
		public CustomerCursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

		public long getId() {
			return mCursor.getLong(mCursor.getColumnIndex(mPrefix + "_id"));
		}
		public String getName() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "name"));
		}
		public String getAddress() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "address"));
		}
		public String getAddress2() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "address2"));
		}
		public String getCity() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "city"));
		}
		public String getState() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "state"));
		}
		public String getZipCode() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "zipCode"));
		}
		public String getPhone() {
			return mCursor.getString(mCursor.getColumnIndex(mPrefix + "phone"));
		}
		public int getDbRev() {
			return mCursor.getInt(mCursor.getColumnIndex(mPrefix + "dbRev"));
		}

	}


}
