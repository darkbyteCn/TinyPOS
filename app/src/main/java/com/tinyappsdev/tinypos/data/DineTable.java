package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class DineTable {

	long _id;
	String name;
	long ticketId;
	int maxGuest;
	int dbRev;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setName(String pName) {
		this.name = pName;
	}

	public String getName() {
		return this.name;
	}

	public void setTicketId(long pTicketId) {
		this.ticketId = pTicketId;
	}

	public long getTicketId() {
		return this.ticketId;
	}

	public void setMaxGuest(int pMaxGuest) {
		this.maxGuest = pMaxGuest;
	}

	public int getMaxGuest() {
		return this.maxGuest;
	}

	public void setDbRev(int pDbRev) {
		this.dbRev = pDbRev;
	}

	public int getDbRev() {
		return this.dbRev;
	}

	public static class Schema {
		public static String TABLE_NAME = "DineTable";

		public static String COL_ID = "_id";
		public static String COL_NAME = "name";
		public static String COL_TICKETID = "ticketId";
		public static String COL_MAXGUEST = "maxGuest";
		public static String COL_DBREV = "dbRev";

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS DineTable (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"name TEXT," +
			"ticketId INTEGER," +
			"maxGuest INTEGER," +
			"dbRev INTEGER" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS DineTable");
		}

		public static String[] getColNames() {
			return new String[] {
				"DineTable._id AS DineTable__id",
				"DineTable.name AS DineTable_name",
				"DineTable.ticketId AS DineTable_ticketId",
				"DineTable.maxGuest AS DineTable_maxGuest",
				"DineTable.dbRev AS DineTable_dbRev"
			};
		}

	}

}
