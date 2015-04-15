package com.sjtu.icarer.persistence.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public abstract class NormalResource<T> implements PersistableResource<T>{
	
	public Cursor getCursor(SQLiteDatabase readableDatabase,String table,String[]projectionIn,
			String selection, String groupBy) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(table);
		return builder.query(readableDatabase, projectionIn, selection, null, groupBy, null, null);
	}
	//MENTION: loadFrom method is defined in PersistableResource
	
	public void update(SQLiteDatabase writableDatabase, String table,ContentValues values, String selection) {
		writableDatabase.update(table, values, selection, null);
	}
	
	public void delete(SQLiteDatabase writableDatabase,String table, String selection) {
		writableDatabase.delete(table, selection, null);
	}
	/*
	 * insert a single item to the db
	 * */
	public abstract Boolean insert(SQLiteDatabase writableDatabase, T singleItem);
	
	
}
