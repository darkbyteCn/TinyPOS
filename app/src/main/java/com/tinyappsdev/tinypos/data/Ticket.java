package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class Ticket {

	long _id;
	int state;
	long tableId;
	String tableName;
	long employeeId;
	String employeeName;
	long customerId;
	String customerName;
	int numFoodFullfilled;
	int numFood;
	int numGuest;
	int curItemId;
	List<TicketFood> foodItems;
	double subtotal;
	double tips;
	double fee;
	double tax;
	double total;
	long createdTime;
	int dbRev;
	long dbCreatedTime;
	long dbModifiedTime;

	public void setId(long pId) {
		this._id = pId;
	}

	public long getId() {
		return this._id;
	}

	public void setState(int pState) {
		this.state = pState;
	}

	public int getState() {
		return this.state;
	}

	public void setTableId(long pTableId) {
		this.tableId = pTableId;
	}

	public long getTableId() {
		return this.tableId;
	}

	public void setTableName(String pTableName) {
		this.tableName = pTableName;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setEmployeeId(long pEmployeeId) {
		this.employeeId = pEmployeeId;
	}

	public long getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeName(String pEmployeeName) {
		this.employeeName = pEmployeeName;
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public void setCustomerId(long pCustomerId) {
		this.customerId = pCustomerId;
	}

	public long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerName(String pCustomerName) {
		this.customerName = pCustomerName;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setNumFoodFullfilled(int pNumFoodFullfilled) {
		this.numFoodFullfilled = pNumFoodFullfilled;
	}

	public int getNumFoodFullfilled() {
		return this.numFoodFullfilled;
	}

	public void setNumFood(int pNumFood) {
		this.numFood = pNumFood;
	}

	public int getNumFood() {
		return this.numFood;
	}

	public void setNumGuest(int pNumGuest) {
		this.numGuest = pNumGuest;
	}

	public int getNumGuest() {
		return this.numGuest;
	}

	public void setCurItemId(int pCurItemId) {
		this.curItemId = pCurItemId;
	}

	public int getCurItemId() {
		return this.curItemId;
	}

	public void setFoodItems(List<TicketFood> pFoodItems) {
		this.foodItems = pFoodItems;
	}

	public List<TicketFood> getFoodItems() {
		return this.foodItems;
	}

	public void setSubtotal(double pSubtotal) {
		this.subtotal = pSubtotal;
	}

	public double getSubtotal() {
		return this.subtotal;
	}

	public void setTips(double pTips) {
		this.tips = pTips;
	}

	public double getTips() {
		return this.tips;
	}

	public void setFee(double pFee) {
		this.fee = pFee;
	}

	public double getFee() {
		return this.fee;
	}

	public void setTax(double pTax) {
		this.tax = pTax;
	}

	public double getTax() {
		return this.tax;
	}

	public void setTotal(double pTotal) {
		this.total = pTotal;
	}

	public double getTotal() {
		return this.total;
	}

	public void setCreatedTime(long pCreatedTime) {
		this.createdTime = pCreatedTime;
	}

	public long getCreatedTime() {
		return this.createdTime;
	}

	public void setDbRev(int pDbRev) {
		this.dbRev = pDbRev;
	}

	public int getDbRev() {
		return this.dbRev;
	}

	public void setDbCreatedTime(long pDbCreatedTime) {
		this.dbCreatedTime = pDbCreatedTime;
	}

	public long getDbCreatedTime() {
		return this.dbCreatedTime;
	}

	public void setDbModifiedTime(long pDbModifiedTime) {
		this.dbModifiedTime = pDbModifiedTime;
	}

	public long getDbModifiedTime() {
		return this.dbModifiedTime;
	}

	public static class Schema {
		public static String TABLE_NAME = "Ticket";

		public static String COL_ID = "_id";
		public static String COL_STATE = "state";
		public static String COL_TABLEID = "tableId";
		public static String COL_TABLENAME = "tableName";
		public static String COL_EMPLOYEEID = "employeeId";
		public static String COL_EMPLOYEENAME = "employeeName";
		public static String COL_CUSTOMERID = "customerId";
		public static String COL_CUSTOMERNAME = "customerName";
		public static String COL_NUMFOODFULLFILLED = "numFoodFullfilled";
		public static String COL_NUMFOOD = "numFood";
		public static String COL_NUMGUEST = "numGuest";
		public static String COL_CURITEMID = "curItemId";
		public static String COL_FOODITEMS = "foodItems";
		public static String COL_SUBTOTAL = "subtotal";
		public static String COL_TIPS = "tips";
		public static String COL_FEE = "fee";
		public static String COL_TAX = "tax";
		public static String COL_TOTAL = "total";
		public static String COL_CREATEDTIME = "createdTime";
		public static String COL_DBREV = "dbRev";
		public static String COL_DBCREATEDTIME = "dbCreatedTime";
		public static String COL_DBMODIFIEDTIME = "dbModifiedTime";

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Ticket (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"state INTEGER," +
			"tableId INTEGER," +
			"tableName TEXT," +
			"employeeId INTEGER," +
			"employeeName TEXT," +
			"customerId INTEGER," +
			"customerName TEXT," +
			"numFoodFullfilled INTEGER," +
			"numFood INTEGER," +
			"numGuest INTEGER," +
			"curItemId INTEGER," +
			"foodItems TEXT," +
			"subtotal REAL," +
			"tips REAL," +
			"fee REAL," +
			"tax REAL," +
			"total REAL," +
			"createdTime INTEGER," +
			"dbRev INTEGER," +
			"dbCreatedTime INTEGER," +
			"dbModifiedTime INTEGER" +
			")";

		public static String SQL_INDEX_TABLEID = "CREATE INDEX IF NOT EXISTS TICKET_TABLEID on Ticket(tableId)";

		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(SQL_INDEX_TABLEID);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP INDEX IF EXISTS TICKET_TABLEID");
			db.execSQL("DROP TABLE IF EXISTS Ticket");
		}

		public static String[] getColNames() {
			return new String[] {
				"Ticket._id AS Ticket__id",
				"Ticket.state AS Ticket_state",
				"Ticket.tableId AS Ticket_tableId",
				"Ticket.tableName AS Ticket_tableName",
				"Ticket.employeeId AS Ticket_employeeId",
				"Ticket.employeeName AS Ticket_employeeName",
				"Ticket.customerId AS Ticket_customerId",
				"Ticket.customerName AS Ticket_customerName",
				"Ticket.numFoodFullfilled AS Ticket_numFoodFullfilled",
				"Ticket.numFood AS Ticket_numFood",
				"Ticket.numGuest AS Ticket_numGuest",
				"Ticket.curItemId AS Ticket_curItemId",
				"Ticket.foodItems AS Ticket_foodItems",
				"Ticket.subtotal AS Ticket_subtotal",
				"Ticket.tips AS Ticket_tips",
				"Ticket.fee AS Ticket_fee",
				"Ticket.tax AS Ticket_tax",
				"Ticket.total AS Ticket_total",
				"Ticket.createdTime AS Ticket_createdTime",
				"Ticket.dbRev AS Ticket_dbRev",
				"Ticket.dbCreatedTime AS Ticket_dbCreatedTime",
				"Ticket.dbModifiedTime AS Ticket_dbModifiedTime"
			};
		}

	}

}
