package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.database.sqlite.SQLiteDatabase;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

<%

function getJavaDataType(type) {
	return type;
}

function getDataBaseType(type) {
	if(type == 'int' || type == 'long')
		return 'INTEGER';
	else if(type == 'float' || type == 'double')
		return 'REAL';
	else if(type == 'byte[]')
		return 'BLOB';
	else
		return 'TEXT';
}

function getInlineIndex(index) {
	if(index == 'primary')
		return 'PRIMARY KEY ASC';
	else if(index == 'unique')
		return 'UNIQUE';
	else 
		return null;
}

var className = capitalize(this.name);
var cols = [];
var sql_cols = [];
for(var col in this.cols) {
	var meta = this.cols[col];

	cols.push({
		dbName: col,
		type: meta.type,
		dataType: getJavaDataType(meta.type),
		javaName: capitalize(col),
		dbIndex: meta.index,
		meta: meta
	});

	var index = getInlineIndex(meta.index);
	sql_cols.push(col + " " + getDataBaseType(meta.type) + (index ? " " + index : ''));
}
%>

public class ${className} {

%for(var col of cols) {
	${col.dataType} ${col.dbName};
%}

%for(var col of cols) {
	public void set${col.javaName}(${col.dataType} p${col.javaName}) {
		this.${col.dbName} = p${col.javaName};
	}

	public ${col.dataType} get${col.javaName}() {
		return this.${col.dbName};
	}

%}
	public static class Schema {
		public static String TABLE_NAME = "${this.name}";

%for(var col of cols) {
		public static String COL_${col.javaName.toUpperCase()} = "${col.dbName}";
%}

		public static String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ${this.name} (" + 
			"${sql_cols.join('," +\n			"')}" +
			")";

<%
for(var col of cols) {
	if(col.dbIndex == 'index') {
%>
		public static String SQL_INDEX_${col.dbName.toUpperCase()} = "CREATE INDEX IF NOT EXISTS ${this.name.toUpperCase()}_${col.dbName.toUpperCase()} on ${this.name}(${col.dbName})";
<%
	}
}
%>

		public static void CreateTable(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
<%
for(var col of cols) {
	if(col.dbIndex == 'index') {
%>
			db.execSQL(SQL_INDEX_${col.dbName.toUpperCase()});
<%
	}
}
%>
		}

		public static void DropTable(SQLiteDatabase db) {
<%
for(var col of cols) {
	if(col.dbIndex == 'index') {
%>
			db.execSQL("DROP INDEX IF EXISTS ${this.name.toUpperCase()}_${col.dbName.toUpperCase()}");
<%
	}
}
%>
			db.execSQL("DROP TABLE IF EXISTS ${this.name}");
		}

		public static String[] getColNames() {
			return new String[] {
%for(var i = 0; i < cols.length; i++) {
				"${className}.${cols[i].dbName} AS ${className}_${cols[i].dbName}"${i < cols.length - 1 ? ',' : ''}
%}
			};
		}

	}

}
