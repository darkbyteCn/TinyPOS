package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class FoodAttrGroup {


	String name;
	List<FoodAttr> attr;


	public void setName(String pName) {
		this.name = pName;
	}

	public String getName() {
		return this.name;
	}

	public void setAttr(List<FoodAttr> pAttr) {
		this.attr = pAttr;
	}

	public List<FoodAttr> getAttr() {
		return this.attr;
	}

	public static class Schema {
		public final static String TABLE_NAME = "FoodAttrGroup";

		public final static String COL_NAME = "name";
		public final static String COL_ATTR = "attr";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS FoodAttrGroup (" + 
			"name TEXT," +
			"attr TEXT" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS FoodAttrGroup");
		}

		public static String[] getColNames() {
			return new String[] {
				"FoodAttrGroup.name AS FoodAttrGroup_name",
				"FoodAttrGroup.attr AS FoodAttrGroup_attr"
			};
		}

	}

}
