package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class Customer {


	long _id;
	String name;
	String address;
	String address2;
	String city;
	String state;
	String zipCode;
	String phone;
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

	public void setAddress(String pAddress) {
		this.address = pAddress;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress2(String pAddress2) {
		this.address2 = pAddress2;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setCity(String pCity) {
		this.city = pCity;
	}

	public String getCity() {
		return this.city;
	}

	public void setState(String pState) {
		this.state = pState;
	}

	public String getState() {
		return this.state;
	}

	public void setZipCode(String pZipCode) {
		this.zipCode = pZipCode;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setPhone(String pPhone) {
		this.phone = pPhone;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setDbRev(int pDbRev) {
		this.dbRev = pDbRev;
	}

	public int getDbRev() {
		return this.dbRev;
	}

	public static class Schema {
		public final static String TABLE_NAME = "Customer";

		public final static String COL_ID = "_id";
		public final static String COL_NAME = "name";
		public final static String COL_ADDRESS = "address";
		public final static String COL_ADDRESS2 = "address2";
		public final static String COL_CITY = "city";
		public final static String COL_STATE = "state";
		public final static String COL_ZIPCODE = "zipCode";
		public final static String COL_PHONE = "phone";
		public final static String COL_DBREV = "dbRev";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Customer (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"name TEXT," +
			"address TEXT," +
			"address2 TEXT," +
			"city TEXT," +
			"state TEXT," +
			"zipCode TEXT," +
			"phone TEXT," +
			"dbRev INTEGER" +
			")";


		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS Customer");
		}

		public static String[] getColNames() {
			return new String[] {
				"Customer._id AS Customer__id",
				"Customer.name AS Customer_name",
				"Customer.address AS Customer_address",
				"Customer.address2 AS Customer_address2",
				"Customer.city AS Customer_city",
				"Customer.state AS Customer_state",
				"Customer.zipCode AS Customer_zipCode",
				"Customer.phone AS Customer_phone",
				"Customer.dbRev AS Customer_dbRev"
			};
		}

	}

}
