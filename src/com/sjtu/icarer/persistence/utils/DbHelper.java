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
        			+ "id INTEGER PRIMARY KEY,"//user_id
        			+ "elder_id INTEGER,"      //elder id
        			+ "name TEXT, "            //elder_name
        			+ "username TEXT,"         //elder username
        			+ "photo_url TEXT,"        
        			+ "area_id INTEGER,"
        			+ "care_level INTEGER);");
        	
            db.execSQL("CREATE TABLE elder_carer ("
            		+ "id INTEGER ,"          //staff_id
            		+ "elder_id INTEGER,"     //elder_id
            		+ "name TEXT,"
            		+ "username TEXT,"
            		+ "photo_url TEXT,"
            		+ "work_date DATE,"
            		+ "PRIMARY KEY (id,elder_id));");
            
            db.execSQL("CREATE TABLE area_carer ("
            		+ "id INTEGER ,"         //staff_id
            		+ "area_id INTEGER," 
            		+ "name TEXT,"
            		+ "username TEXT,"
            		+ "photo_url TEXT,"
            		+ "work_date DATE,"
            		+ "PRIMARY KEY (id,area_id));");
            
            db.execSQL("CREATE TABLE area_item ("
            		+ "id INTEGER PRIMARY KEY," // area item id, TABLE-T_AREA_ITEM
            		+ "icon TEXT,"
            		+ "gero_id INTEGER,"
            		+ "item_name TEXT,"         //area item name
            		+ "frequency INTEGER,"
            		+ "period INTEGER,"
            		+ "notes TEXT);");
            db.execSQL("CREATE TABLE elder_item ("
            		+ "id INTEGER PRIMARY KEY,"  //elder item id, TABLE-T_ELDER_ITEM
            		+ "icon TEXT,"
            		+ "item_id INTEGER,"         //care item id , TABLE-T_CARE_ITEM
            		+ "item_name TEXT,"          //care item name
            		+ "elder_id INTEGER,"        //elder id
            		+ "period INTEGER,"
            		+ "notes TEXT,"
            		+ "start_time TIME,"//start time in a single day
            		+ "end_time TIME);");  //expire time in a single day
            
            db.execSQL("CREATE TABLE area_item_record ("
            		+ "carer_id INTEGER,"
            		+ "area_id INTEGER,"
            		+ "item_id INTEGER,"  //area_item_id
            		+ "item_name TEXT,"   //area_item_name
            		+ "finish_time DATE," // item's finish time
            		+ "is_submit TEXT);");// a boolean value 
            
            db.execSQL("CREATE TABLE elder_item_record ("
            		+ "carer_id INTEGER,"
            		+ "elder_id INTEGER,"
            		+ "item_id INTEGER,"  //elder_item_id
            		+ "item_name TEXT,"   //elder_item_name
            		+ "finish_time DATE," // item's finish time
            		+ "is_submit TEXT);");// a boolean value  
            
            
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.execSQL("DROP TABLE IF EXISTS elder");
        db.execSQL("DROP TABLE IF EXISTS elder_carer");
        db.execSQL("DROP TABLE IF EXISTS area_carer");
        db.execSQL("DROP TABLE IF EXISTS elder_item");
        db.execSQL("DROP TABLE IF EXISTS area_item");
        db.execSQL("DROP TABLE IF EXISTS elder_item_record");
        onCreate(db);
    }
}
