package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class TicketFoodAttr {


	String name;
	String value;


	public void setName(String pName) {
		this.name = pName;
	}

	public String getName() {
		return this.name;
	}

	public void setValue(String pValue) {
		this.value = pValue;
	}

	public String getValue() {
		return this.value;
	}

	public static class Schema {
		public final static String TABLE_NAME = "TicketFoodAttr";

		public final static String COL_NAME = "name";
		public final static String COL_VALUE = "value";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TicketFoodAttr (" + 
			"name TEXT," +
			"value TEXT" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS TicketFoodAttr");
		}

		public static String[] getColNames() {
			return new String[] {
				"TicketFoodAttr.name AS TicketFoodAttr_name",
				"TicketFoodAttr.value AS TicketFoodAttr_value"
			};
		}

	}

}
