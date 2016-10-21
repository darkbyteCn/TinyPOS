package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class Menu {

	long _id;
	long categoryId;
	long foodId;
	String menuName;
	int dbRev;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setCategoryId(long pCategoryId) {
		this.categoryId = pCategoryId;
	}

	public long getCategoryId() {
		return this.categoryId;
	}

	public void setFoodId(long pFoodId) {
		this.foodId = pFoodId;
	}

	public long getFoodId() {
		return this.foodId;
	}

	public void setMenuName(String pMenuName) {
		this.menuName = pMenuName;
	}

	public String getMenuName() {
		return this.menuName;
	}

	public void setDbRev(int pDbRev) {
		this.dbRev = pDbRev;
	}

	public int getDbRev() {
		return this.dbRev;
	}

	public static class Schema {
		public static String TABLE_NAME = "Menu";

		public static String COL_ID = "_id";
		public static String COL_CATEGORYID = "categoryId";
		public static String COL_FOODID = "foodId";
		public static String COL_MENUNAME = "menuName";
		public static String COL_DBREV = "dbRev";

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Menu (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"categoryId INTEGER," +
			"foodId INTEGER," +
			"menuName TEXT," +
			"dbRev INTEGER" +
			")";

		public static String SQL_INDEX_CATEGORYID = "CREATE INDEX IF NOT EXISTS MENU_CATEGORYID on Menu(categoryId)";
		public static String SQL_INDEX_FOODID = "CREATE INDEX IF NOT EXISTS MENU_FOODID on Menu(foodId)";

		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(SQL_INDEX_CATEGORYID);
			db.execSQL(SQL_INDEX_FOODID);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP INDEX IF EXISTS MENU_CATEGORYID");
			db.execSQL("DROP INDEX IF EXISTS MENU_FOODID");
			db.execSQL("DROP TABLE IF EXISTS Menu");
		}

		public static String[] getColNames() {
			return new String[] {
				"Menu._id AS Menu__id",
				"Menu.categoryId AS Menu_categoryId",
				"Menu.foodId AS Menu_foodId",
				"Menu.menuName AS Menu_menuName",
				"Menu.dbRev AS Menu_dbRev"
			};
		}

	}

}
