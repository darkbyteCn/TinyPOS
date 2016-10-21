package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class TicketFood {

	long _id;
	int itemId;
	String foodName;
	int quantity;
	double price;
	double exPrice;
	List<TicketFoodAttr> attr;
	int taxable;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
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

	public void setAttr(List<TicketFoodAttr> pAttr) {
		this.attr = pAttr;
	}

	public List<TicketFoodAttr> getAttr() {
		return this.attr;
	}

	public void setTaxable(int pTaxable) {
		this.taxable = pTaxable;
	}

	public int getTaxable() {
		return this.taxable;
	}

	public static class Schema {
		public static String TABLE_NAME = "TicketFood";

		public static String COL_ID = "_id";
		public static String COL_ITEMID = "itemId";
		public static String COL_FOODNAME = "foodName";
		public static String COL_QUANTITY = "quantity";
		public static String COL_PRICE = "price";
		public static String COL_EXPRICE = "exPrice";
		public static String COL_ATTR = "attr";
		public static String COL_TAXABLE = "taxable";

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TicketFood (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"itemId INTEGER," +
			"foodName TEXT," +
			"quantity INTEGER," +
			"price REAL," +
			"exPrice REAL," +
			"attr TEXT," +
			"taxable INTEGER" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS TicketFood");
		}

		public static String[] getColNames() {
			return new String[] {
				"TicketFood._id AS TicketFood__id",
				"TicketFood.itemId AS TicketFood_itemId",
				"TicketFood.foodName AS TicketFood_foodName",
				"TicketFood.quantity AS TicketFood_quantity",
				"TicketFood.price AS TicketFood_price",
				"TicketFood.exPrice AS TicketFood_exPrice",
				"TicketFood.attr AS TicketFood_attr",
				"TicketFood.taxable AS TicketFood_taxable"
			};
		}

	}

}
