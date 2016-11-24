package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;



public class Ticket {

	public final static int STATE_COMPLETED = 1 << 30;
	public final static int STATE_PAID = 1 << 3;
	public final static int STATE_FULFILLED = 1 << 2;

	long _id;
	int state;
	long tableId;
	String tableName;
	long employeeId;
	String employeeName;
	Customer customer;
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
	double balance;
	List<TicketPayment> payments;
	long createdTime;
	String notes;
	int dbRev;
	long dbCreatedTime;
	long dbModifiedTime;

	public void set_id(long _id) { setId(_id); }
	public long get_id() { return getId(); }

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

	public void setCustomer(Customer pCustomer) {
		this.customer = pCustomer;
	}

	public Customer getCustomer() {
		return this.customer;
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

	public void setBalance(double pBalance) {
		this.balance = pBalance;
	}

	public double getBalance() {
		return this.balance;
	}

	public void setPayments(List<TicketPayment> pPayments) {
		this.payments = pPayments;
	}

	public List<TicketPayment> getPayments() {
		return this.payments;
	}

	public void setCreatedTime(long pCreatedTime) {
		this.createdTime = pCreatedTime;
	}

	public long getCreatedTime() {
		return this.createdTime;
	}

	public void setNotes(String pNotes) {
		this.notes = pNotes;
	}

	public String getNotes() {
		return this.notes;
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
		public final static String TABLE_NAME = "Ticket";

		public final static String COL_ID = "_id";
		public final static String COL_STATE = "state";
		public final static String COL_TABLEID = "tableId";
		public final static String COL_TABLENAME = "tableName";
		public final static String COL_EMPLOYEEID = "employeeId";
		public final static String COL_EMPLOYEENAME = "employeeName";
		public final static String COL_CUSTOMER = "customer";
		public final static String COL_NUMFOODFULLFILLED = "numFoodFullfilled";
		public final static String COL_NUMFOOD = "numFood";
		public final static String COL_NUMGUEST = "numGuest";
		public final static String COL_CURITEMID = "curItemId";
		public final static String COL_FOODITEMS = "foodItems";
		public final static String COL_SUBTOTAL = "subtotal";
		public final static String COL_TIPS = "tips";
		public final static String COL_FEE = "fee";
		public final static String COL_TAX = "tax";
		public final static String COL_TOTAL = "total";
		public final static String COL_BALANCE = "balance";
		public final static String COL_PAYMENTS = "payments";
		public final static String COL_CREATEDTIME = "createdTime";
		public final static String COL_NOTES = "notes";
		public final static String COL_DBREV = "dbRev";
		public final static String COL_DBCREATEDTIME = "dbCreatedTime";
		public final static String COL_DBMODIFIEDTIME = "dbModifiedTime";

		public final static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Ticket (" + 
			"_id INTEGER PRIMARY KEY ASC," +
			"state INTEGER," +
			"tableId INTEGER," +
			"tableName TEXT," +
			"employeeId INTEGER," +
			"employeeName TEXT," +
			"customer TEXT," +
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
			"balance REAL," +
			"payments TEXT," +
			"createdTime INTEGER," +
			"notes TEXT," +
			"dbRev INTEGER," +
			"dbCreatedTime INTEGER," +
			"dbModifiedTime INTEGER" +
			")";

		public final static String SQL_INDEX_STATE = "CREATE INDEX IF NOT EXISTS TICKET_STATE on Ticket(state)";
		public final static String SQL_INDEX_TABLEID = "CREATE INDEX IF NOT EXISTS TICKET_TABLEID on Ticket(tableId)";

		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(SQL_INDEX_STATE);
			db.execSQL(SQL_INDEX_TABLEID);
		}

		public static void DropTable(SQLiteDatabase db) {
			db.execSQL("DROP INDEX IF EXISTS TICKET_STATE");
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
				"Ticket.customer AS Ticket_customer",
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
				"Ticket.balance AS Ticket_balance",
				"Ticket.payments AS Ticket_payments",
				"Ticket.createdTime AS Ticket_createdTime",
				"Ticket.notes AS Ticket_notes",
				"Ticket.dbRev AS Ticket_dbRev",
				"Ticket.dbCreatedTime AS Ticket_dbCreatedTime",
				"Ticket.dbModifiedTime AS Ticket_dbModifiedTime"
			};
		}

	}

}
