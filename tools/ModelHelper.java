package com.tinyappsdev.tinypos.data;

//Auto-Generated, See Tools

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import org.json.JSONException;
import org.json.JSONObject;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

<%

function getContentValueType(type) {
	if(type == 'byte[]')
		return 'object';
	else if(type == 'float')
		return 'double';
	else
		return type;
}

function isNumber(type) {
	if(type == 'float' || type == 'double' || type == 'short' || type == 'int' || type == 'long')
		return true;
	return false;
}

function optValue(type) {
	if(type == 'float' || type == 'double' || type == 'short' || type == 'int' || type == 'long')
		return 0;
	else
		return null;
}

function typeName(s) {
	return s.replace(/[^a-z0-9]+/gi, '_').replace(/[^a-z0-9]+$/gi, '').toUpperCase() + '_TYPEREF';
}

var models = {};
for(var name in this.schemas) {
	var schema = this.schemas[name];
	var model = models[name] = {};

	for(var col in schema) {
		var colMeta = schema[col];
		model[col] = {
			isJson: colMeta.isJson,
			type: colMeta.type,
			contentValueType: getContentValueType(colMeta.type)
		}
	}
}

%>

public class ModelHelper {
%for(var name in models) {
%	for(var col in models[name]) {
%		if(!models[name][col].isJson) continue;
	public final static TypeReference ${typeName(models[name][col].type)} = new TypeReference<${models[name][col].type}>(){};
%	}
%}

	public static ContentProviderOperation BuildOperationForDelete(String collection, long id) throws JSONException {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(
			ContentProviderEx.BuildUri(collection, id + "")
		);
		return builder.build();
	}

	public static ContentProviderOperation BuildOperationForInsert(String collection, JSONObject doc) throws JSONException {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
			ContentProviderEx.BuildUri(
				new String[] {collection, doc.getLong("_id") + ""},
				new Object[][] { new Object[] {"replace", 1} }
				)
			);
		return builder.withValues(GetContentValuesFromJson(collection, doc)).build();
	}

	public static void ConfigSetValue(SQLiteDatabase db, String key, Object value) {
		String val = String.valueOf(value);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.Schema.COL_VAL, val);
        int count = db.update(Config.Schema.TABLE_NAME, contentValues,
                String.format("%s < 0 and %s=?", Config.Schema.COL_ID, Config.Schema.COL_KEY),
                new String[]{key}
        );

        if (count <= 0) {
            db.execSQL(
                    String.format("insert into %s select min(_id) - 1, ?, ? from %s",
                            Config.Schema.TABLE_NAME,
                            Config.Schema.TABLE_NAME
                    ),
                    new String[]{key, val}
            );
        }
	}

	public static ContentValues GetContentValuesFromJson(String collection, JSONObject jsonObject) throws JSONException {
%for(var name in models) {
%	if(!(name in this.noSyncTables)) {
		if(collection.equals("${name}"))
			return ${name}ContentValuesFromJson(jsonObject);
%	}
%}
		return null;
	}

	public static Object fromJson(String jsonStr, TypeReference typeReference) {
		if(jsonStr == null) return null;
		try {
			return (new ObjectMapper()).readValue(jsonStr, typeReference);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

<%
for(var name in models) {
	var model = models[name];
%>
	public static ${name} ${name}FromMap(Map map){
		${name} obj = new ${name}();
%		for(var col in model) {
%			if(isNumber(model[col].type)) {
		obj.set${capitalize(col)}(((Number)map.get("${col}")).${model[col].type}Value());
%			} else {
		obj.set${capitalize(col)}((${model[col].type})map.get("${col}"));
%			}
%		}

		return obj;
    }

    public static Map ${name}ToMap(${name} obj){
		Map map = new HashMap();
%		for(var col in model) {
		map.put("${col}", obj.get${capitalize(col)}());
%		}

		return map;
    }

    public static ${name} ${name}FromCursor(Cursor cursor) {
    	return ${name}FromCursor(cursor, "");
    }

    public static ${name} ${name}FromCursor(Cursor cursor, String prefix) {
		${name} m = new ${name}();

<%
		for(var col in model) {
			var colMeta = model[col];
			var cast = colMeta.contentValueType != colMeta.type ? '(' + colMeta.type + ')' : ''
%>
%			if(colMeta.isJson) {
		m.set${capitalize(col)}((${colMeta.type})fromJson(
			cursor.getString(cursor.getColumnIndex(prefix + "${col}")),
			${typeName(colMeta.type)}
		));
%			} else {
		m.set${capitalize(col)}(${cast}cursor.get${capitalize(colMeta.contentValueType)}(cursor.getColumnIndex(prefix + "${col}")));
%			}
%		}

		return m;
    }

    public static ContentValues ${name}ContentValuesFromJson(JSONObject jsonObject) throws JSONException {
    	ContentValues m = new ContentValues();

<%
		for(var col in model) {
			var colMeta = model[col];
			var cast = colMeta.contentValueType != colMeta.type ? '(' + colMeta.type + ')' : ''
%>
%			if(colMeta.isJson) {
		m.put("${col}", jsonObject.optString("${col}", null));
%			} else {
		m.put("${col}", ${cast}jsonObject.opt${capitalize(colMeta.contentValueType)}("${col}", ${optValue(colMeta.type)}));
%			}
%		}

		return m;
    }

%}

<%
for(var name in models) {
	var model = models[name];
%>
	public static class ${name}Cursor {
		private Cursor mCursor;
		private String mPrefix;
		public ${name}Cursor(Cursor cursor) { this(cursor, ""); }
		public ${name}Cursor(Cursor cursor, String prefix) {
			mCursor = cursor;
			mPrefix = prefix;
		}

<%
	for(var col in model) {
		var colMeta = model[col];
		var cast = colMeta.contentValueType != colMeta.type ? '(' + colMeta.type + ')' : ''
%>
		public ${colMeta.type} get${capitalize(col)}() {
%			if(colMeta.isJson) {
			return (${colMeta.type})fromJson(
				mCursor.getString(mCursor.getColumnIndex(mPrefix + "${col}")),
				${typeName(colMeta.type)}
			);
%			} else {
			return ${cast}mCursor.get${capitalize(colMeta.contentValueType)}(mCursor.getColumnIndex(mPrefix + "${col}"));
%			}
		}
%	}

	}

%}

}
