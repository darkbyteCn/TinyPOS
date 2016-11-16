package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class TicketFood {


	long _id;
	long ticketId;
	int itemId;
	String foodName;
	int quantity;
	int fulfilled;
	double price;
	double exPrice;
	double taxRate;
	List<TicketFoodAttr> attr;
	long createdTime;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setTicketId(long pTicketId) {
		this.ticketId = pTicketId;
	}

	public long getTicketId() {
		return this.ticketId;
	}

	public void setItemId(int pItemId) {
		this.itemId = pItemId;
	}

	public int getItemId() {
		return this.itemId;
	}

	public void setFoodName(String pFoodName) {
		this.foodName = pFoodName;
	}

	public String getFoodName() {
		return this.foodName;
	}

	public void setQuantity(int pQuantity) {
		this.quantity = pQuantity;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setFulfilled(int pFulfilled) {
		this.fulfilled = pFulfilled;
	}

	public int getFulfilled() {
		return this.fulfilled;
	}

	public void setPrice(double pPrice) {
		this.price = pPrice;
	}

	public double getPrice() {
		return this.price;
	}

	public void setExPrice(double pExPrice) {
		this.exPrice = pExPrice;
	}

	public double getExPrice() {
		return this.exPrice;
	}

	public void setTaxRate(double pTaxRate) {
		this.taxRate = pTaxRate;
	}

	public double getTaxRate() {
		return this.taxRate;
	}

	public void setAttr(List<TicketFoodAttr> pAttr) {
		this.attr = pAttr;
	}

	public List<TicketFoodAttr> getAttr() {
		return this.attr;
	}

	public void setCreatedTime(long pCreatedTime) {
		this.createdTime = pCreatedTime;
	}

	public long getCreatedTime() {
		return this.createdTime;
	}

	public static class Schema {
		public final static String TABLE_NAME = "TicketFood";

		public final static String COL_ID = "_id";
		public final static String COL_TICKETID = "ticketId";
		public final static String COL_ITEMID = "itemId";
		public final static String COL_FOODNAME = "foodName";
		public final static String COL_QUANTITY = "quantity";
		public final static String COL_FULFILLED = "fulfilled";
		public final static String COL_PRICE = "price";
		public final static String COL_EXPRICE = "exPrice";
		public final static String COL_TAXRATE = "taxRate";
		public final static String COL_ATTR = "attr";
		public final static String COL_CREATEDTIME = "createdTime";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TicketFood (" + 
			"_id INTEGER," +
			"ticketId INTEGER," +
			"itemId INTEGER," +
			"foodName TEXT," +
			"quantity INTEGER," +
			"fulfilled INTEGER," +
			"price REAL," +
			"exPrice REAL," +
			"taxRate REAL," +
			"attr TEXT," +
			"createdTime INTEGER" +
			")";

		public final static String SQL_INDEX_TICKETID = "CREATE INDEX IF NOT EXISTS TICKETFOOD_TICKETID on TicketFood(ticketId)";
		public final static String SQL_INDEX_CREATEDTIME = "CREATE INDEX IF NOT EXISTS TICKETFOOD_CREATEDTIME on TicketFood(createdTime)";

		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(SQL_INDEX_TICKETID);
			db.execSQL(SQL_INDEX_CREATEDTIME);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP INDEX IF EXISTS TICKETFOOD_TICKETID");
			db.execSQL("DROP INDEX IF EXISTS TICKETFOOD_CREATEDTIME");
			db.execSQL("DROP TABLE IF EXISTS TicketFood");
		}

		public static String[] getColNames() {
			return new String[] {
				"TicketFood._id AS TicketFood__id",
				"TicketFood.ticketId AS TicketFood_ticketId",
				"TicketFood.itemId AS TicketFood_itemId",
				"TicketFood.foodName AS TicketFood_foodName",
				"TicketFood.quantity AS TicketFood_quantity",
				"TicketFood.fulfilled AS TicketFood_fulfilled",
				"TicketFood.price AS TicketFood_price",
				"TicketFood.exPrice AS TicketFood_exPrice",
				"TicketFood.taxRate AS TicketFood_taxRate",
				"TicketFood.attr AS TicketFood_attr",
				"TicketFood.createdTime AS TicketFood_createdTime"
			};
		}

	}

}
