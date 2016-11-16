package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class TicketPayment {


	long _id;
	int type;
	double amount;
	double tender;
	long createdTime;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setType(int pType) {
		this.type = pType;
	}

	public int getType() {
		return this.type;
	}

	public void setAmount(double pAmount) {
		this.amount = pAmount;
	}

	public double getAmount() {
		return this.amount;
	}

	public void setTender(double pTender) {
		this.tender = pTender;
	}

	public double getTender() {
		return this.tender;
	}

	public void setCreatedTime(long pCreatedTime) {
		this.createdTime = pCreatedTime;
	}

	public long getCreatedTime() {
		return this.createdTime;
	}

	public static class Schema {
		public final static String TABLE_NAME = "TicketPayment";

		public final static String COL_ID = "_id";
		public final static String COL_TYPE = "type";
		public final static String COL_AMOUNT = "amount";
		public final static String COL_TENDER = "tender";
		public final static String COL_CREATEDTIME = "createdTime";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TicketPayment (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"type INTEGER," +
			"amount REAL," +
			"tender REAL," +
			"createdTime INTEGER" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS TicketPayment");
		}

		public static String[] getColNames() {
			return new String[] {
				"TicketPayment._id AS TicketPayment__id",
				"TicketPayment.type AS TicketPayment_type",
				"TicketPayment.amount AS TicketPayment_amount",
				"TicketPayment.tender AS TicketPayment_tender",
				"TicketPayment.createdTime AS TicketPayment_createdTime"
			};
		}

	}

}
