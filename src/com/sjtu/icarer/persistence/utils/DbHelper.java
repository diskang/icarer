package com.sjtu.icarer.persistence.utils;

import javax.inject.Inject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * db helper
 * 
 * @author kang shiyong
 */
public class DbHelper extends SQLiteOpenHelper {
	
	 //TODO move to config file
	 /**
     * Version constant to increment when the database should be rebuilt
     */
    private static final int VERSION = 1;

    /**
     * Name of database file
     */
    private static final String NAME = "icarer.db";

    /**
     * @param context
     */
    @Inject
    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
        	db.execSQL("CREATE TABLE elder ("
        			+ "id INTEGER PRIMARY KEY,"
        			+ "name TEXT, "
        			+ "username TEXT,"
        			+ "photo_url TEXT,"
        			+ "area_id INTEGER,"
        			+ "care_level INTEGER);");
        	
            db.execSQL("CREATE TABLE elder_carer ("
            		+ "id INTEGER ,"
            		+ "elder_id INTEGER,"
            		+ "name TEXT,"
            		+ "username TEXT,"
            		+ "photo_url TEXT,"
            		+ "work_date TEXT,"
            		+ "PRIMARY KEY(id, elder_id));");
            
            db.execSQL("CREATE TABLE area_carer ("
            		+ "id INTEGER ,"
            		+ "area_id INTEGER,"
            		+ "name TEXT,"
            		+ "username TEXT,"
            		+ "photo_url TEXT,"
            		+ "work_date TEXT,"
            		+ "PRIMARY KEY(id, area_id));");
            
            db.execSQL("CREATE TABLE area_item ("
            		+ "id INTEGER PRIMARY KEY,"
            		+ "icon TEXT,"
            		+ "gero_id INTEGER,"
            		+ "item_name TEXT,"
            		+ "frequency INTEGER,"
            		+ "period INTEGER,"
            		//+ "last_date TEXT,"
            		//+ "next_date TEXT"
            		+ "notes TEXT);");
            db.execSQL("CREATE TABLE elder_item ("
            		+ "id INTEGER PRIMARY KEY,"
            		+ "icon TEXT,"
            		+ "item_id INTEGER,"
            		+ "item_name TEXT,"
            		+ "start_time TEXT,"//start time in a single day
            		+ "end_time TEXT,"  //expire time in a single day
            		+ "last_date TEXT,"  //last record date
            		+ "next_date TEXT,"  //next plan date base on last_date & period
            		+ "period INTEGER,"
            		+ "notes TEXT);");
        
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.execSQL("DROP TABLE IF EXISTS elder");
        db.execSQL("DROP TABLE IF EXISTS carer");
        db.execSQL("DROP TABLE IF EXISTS elder_item");
        db.execSQL("DROP TABLE IF EXISTS area_item");
        onCreate(db);
    }
}
