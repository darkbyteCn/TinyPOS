package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class Config {


	long _id;
	String key;
	String val;

	public void set_id(long _id) { setId(_id); }
	public long get_id() { return getId(); }

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setKey(String pKey) {
		this.key = pKey;
	}

	public String getKey() {
		return this.key;
	}

	public void setVal(String pVal) {
		this.val = pVal;
	}

	public String getVal() {
		return this.val;
	}

	public static class Schema {
		public final static String TABLE_NAME = "Config";

		public final static String COL_ID = "_id";
		public final static String COL_KEY = "key";
		public final static String COL_VAL = "val";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Config (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"key TEXT," +
			"val TEXT" +
			")";

		public final static String SQL_INDEX_KEY = "CREATE INDEX IF NOT EXISTS CONFIG_KEY on Config(key)";

		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(SQL_INDEX_KEY);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP INDEX IF EXISTS CONFIG_KEY");
			db.execSQL("DROP TABLE IF EXISTS Config");
		}

		public static String[] getColNames() {
			return new String[] {
				"Config._id AS Config__id",
				"Config.key AS Config_key",
				"Config.val AS Config_val"
			};
		}

	}

}
