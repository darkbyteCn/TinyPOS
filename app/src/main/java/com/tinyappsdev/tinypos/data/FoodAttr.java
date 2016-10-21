package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class FoodAttr {

	String name;
	double priceDiff;

	public void setName(String pName) {
		this.name = pName;
	}

	public String getName() {
		return this.name;
	}

	public void setPriceDiff(double pPriceDiff) {
		this.priceDiff = pPriceDiff;
	}

	public double getPriceDiff() {
		return this.priceDiff;
	}

	public static class Schema {
		public static String TABLE_NAME = "FoodAttr";

		public static String COL_NAME = "name";
		public static String COL_PRICEDIFF = "priceDiff";

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS FoodAttr (" + 
			"name TEXT," +
			"priceDiff REAL" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS FoodAttr");
		}

		public static String[] getColNames() {
			return new String[] {
				"FoodAttr.name AS FoodAttr_name",
				"FoodAttr.priceDiff AS FoodAttr_priceDiff"
			};
		}

	}

}
