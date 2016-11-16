package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class Food {


	long _id;
	String foodName;
	int taxable;
	double price;
	int dbRev;
	List<FoodAttrGroup> attrGroup;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setFoodName(String pFoodName) {
		this.foodName = pFoodName;
	}

	public String getFoodName() {
		return this.foodName;
	}

	public void setTaxable(int pTaxable) {
		this.taxable = pTaxable;
	}

	public int getTaxable() {
		return this.taxable;
	}

	public void setPrice(double pPrice) {
		this.price = pPrice;
	}

	public double getPrice() {
		return this.price;
	}

	public void setDbRev(int pDbRev) {
		this.dbRev = pDbRev;
	}

	public int getDbRev() {
		return this.dbRev;
	}

	public void setAttrGroup(List<FoodAttrGroup> pAttrGroup) {
		this.attrGroup = pAttrGroup;
	}

	public List<FoodAttrGroup> getAttrGroup() {
		return this.attrGroup;
	}

	public static class Schema {
		public final static String TABLE_NAME = "Food";

		public final static String COL_ID = "_id";
		public final static String COL_FOODNAME = "foodName";
		public final static String COL_TAXABLE = "taxable";
		public final static String COL_PRICE = "price";
		public final static String COL_DBREV = "dbRev";
		public final static String COL_ATTRGROUP = "attrGroup";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Food (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"foodName TEXT," +
			"taxable INTEGER," +
			"price REAL," +
			"dbRev INTEGER," +
			"attrGroup TEXT" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS Food");
		}

		public static String[] getColNames() {
			return new String[] {
				"Food._id AS Food__id",
				"Food.foodName AS Food_foodName",
				"Food.taxable AS Food_taxable",
				"Food.price AS Food_price",
				"Food.dbRev AS Food_dbRev",
				"Food.attrGroup AS Food_attrGroup"
			};
		}

	}

}
