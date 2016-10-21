package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


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
		public static String TABLE_NAME = "FoodAttrGroup";

		public static String COL_NAME = "name";
		public static String COL_ATTR = "attr";

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS FoodAttrGroup (" + 
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
