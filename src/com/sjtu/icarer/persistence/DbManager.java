package com.sjtu.icarer.persistence;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.persistence.utils.RequestReader;
import com.sjtu.icarer.persistence.utils.RequestWriter;
import com.sjtu.icarer.service.IcarerService;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DbManager {
	 private static final String TAG = "DbManager";
//	 private int areaId;
	 
	 @Inject protected DbCache dbCache;
	 @Inject protected PreferenceManager preferenceManager;
	 @Inject @Named("Auth")protected IcarerService icarerService;
	
	 /**
	     * Format version to bump if serialization format changes and cache should
	     * be ignored
	     */
	 private static final int FORMAT_VERSION = 1;
//	 private static final Executor EXECUTOR = Executors.newFixedThreadPool(10);
	 
//	 @Inject
//	 public DbManager() {
//		 
//	 }
	/**
     * Read data from file
     *
     * @param file
     * @return data
     */
    @SuppressWarnings("unchecked")
    private <V> V read(final File file) {
        long start = System.currentTimeMillis();
        long length = file.length();
        Object data = new RequestReader(file, FORMAT_VERSION).read();
        if (data != null)
            Log.d(TAG, MessageFormat.format(
                    "Cache hit to {0}, {1} ms to load {2} bytes",
                    file.getName(), (System.currentTimeMillis() - start),
                    length));
        return (V) data;
    }

    /**
     * Write data to file
     *
     * @param file
     * @param data
     * @return this manager
     */
    private DbManager write(File file, Object data) {
        new RequestWriter(file, FORMAT_VERSION).write(data);
        return this;
    }

    /**
     * Query tables for columns
     *
     * @param helper
     * @param tables
     * @param columns
     * @return cursor
     */
    protected Cursor query(SQLiteOpenHelper helper, String tables,
            String[] columns) {
        return query(helper, tables, columns, null, null);
    }

    /**
     * Query tables for columns
     *
     * @param helper
     * @param tables
     * @param columns
     * @param selection
     * @param selectionArgs
     * @return cursor
     */
    protected Cursor query(SQLiteOpenHelper helper, String tables,
            String[] columns, String selection, String[] selectionArgs) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tables);
        return builder.query(helper.getReadableDatabase(), columns, selection,
                selectionArgs, null, null, null);
    }
    
    /**
     * Get elders
     * <p/>
     * This method may perform file and/or network I/O and should never be
     * called on the UI-thread
     *
     * @param forceReload
     * @return list of elders
     * @throws IOException
     */
    public List<Elder> getElders(boolean forceReload) throws IOException {
    	int areaId = preferenceManager.getAreaId();
	    ElderData elderData = new ElderData(icarerService, areaId);
        return forceReload ? dbCache.requestAndStore(elderData)
                : dbCache.loadOrRequest(elderData);
    }
    /**
     * delete  an elder in DB 
     * if elder is null , delete all elders 
     * */
    public void deleteElder(Elder elder) throws IOException{
    	int areaId = preferenceManager.getAreaId();
    	ElderData elderData = new ElderData(icarerService, areaId);
    	String selection = elder==null?null:"elder.elder_id="+elder.getElderId();
    	dbCache.delete(elderData, "elder", selection);
    }
    /**
     * Get carer
     * <p/>
     * This method may perform file and/or network I/O and should never be
     * called on the UI-thread
     *
     * @param forceReload
     * @return list of carers
     * @throws IOException
     */
    public List<Carer> getCarerByElder(Elder elder, boolean forceReload) throws IOException {
	    CarerData carerData = new CarerData(icarerService, elder);
        return forceReload ? dbCache.requestAndStore(carerData)
                : dbCache.loadOrRequest(carerData);
    }
    
    public List<Carer> getCarerByArea( boolean forceReload) throws IOException {
    	int areaId = preferenceManager.getAreaId();
	    CarerData carerData = new CarerData(icarerService, areaId);
        return forceReload ? dbCache.requestAndStore(carerData)
                : dbCache.loadOrRequest(carerData);
    }
    
    /**
     * Get area_items
     * <p/>
     * This method may perform file and/or network I/O and should never be
     * called on the UI-thread
     *
     * @param forceReload
     * @return list of area items
     * @throws IOException
     */
    public List<AreaItem> getAreaItems( boolean forceReload) throws IOException {
	    AreaItemData areaItemData = new AreaItemData(icarerService);
        return forceReload ? dbCache.requestAndStore(areaItemData)
                : dbCache.loadOrRequest(areaItemData);
    }
    
    /**
     * 
     * SQL:
select 
    elder_item.*
from 
    elder_item
where
    elder_item.elder_id =???
    AND
    elder_item.id not in (
        select 
            distinct temp_table.item_id
        from
            (
            select 
                elder_item_record.item_id as item_id, elder_item_record.finish_time as finish_time,  elder_item.period as period
            from
                elder_item_record inner join elder_item on elder_item.id = elder_item_record.item_id
            ) as temp_table
        group by
            temp_table.item_id
        having
            datetime(max(temp_table.finish_time), '+' || temp_table.period || ' day') > datetime('now')
    );
    
     * get elder_items that needs to be done now
     * <p/>
     * This method may perform file and should never be
     * called on the UI-thread
 
     * @return list of elder items
     * @throws IOException
     */
    public List<ElderItem> getCurrentElderItems(Elder elder)throws IOException{
    	ElderItemData elderItemData = new ElderItemData(icarerService, elder);
    	String whereClause = 
    			"elder_item.elder_id = "+elder.getElderId()
    			+  " AND elder_item.id not in ( "
    			+  " select "
    			+     " distinct temp_table.item_id "
    			+  " from "
    			+     " (select "
    			+         " elder_item_record.item_id as item_id, elder_item_record.finish_time as finish_time,  elder_item.period as period "
    			+     " from "
    			+         " elder_item_record inner join elder_item on elder_item.id = elder_item_record.item_id "
    			+     " ) as temp_table "
    			+  " group by "
    			+     " temp_table.item_id "
    			+  " having "
    			+     " datetime(max(temp_table.finish_time), '+' || temp_table.period || ' day') > datetime('now') "
    			+  " ) ";
    	List<ElderItem> items =dbCache.load(elderItemData, "elder_item", whereClause);
    	if(items.isEmpty()){
    		List<ElderItem> items_check_if_exist =dbCache.load(elderItemData, "elder_item", null);
    		if(items_check_if_exist.isEmpty()){// whereClause is null,data not even exist in db
    			return dbCache.requestAndStore(elderItemData);// fetch from web
    		}else{
    			return new ArrayList<ElderItem>();// not empty in db, just get empty result under the query conditions
    		}
    	}else{
    		return items;
    	}
    }
    
    /**
     * get elder's items in all
     * <p/>
     * This method may perform file and/or network I/O and should never be
     * called on the UI-thread
     *
     * @param items 
     * @return list of elder items
     * @throws IOException
     */
    
    public List<ElderItem> getAllElderItems(Elder elder, boolean forceReload) throws IOException{
    	ElderItemData elderItemData = new ElderItemData(icarerService, elder);
    	return forceReload ? dbCache.requestAndStore(elderItemData)
                : dbCache.loadOrRequest(elderItemData);
    }
    /**
     * delete elder's elder_items
     * if elder is null , delete all elder items 
     * */
    public void deleteElderItems(Elder elder) throws IOException{
    	ElderItemData elderItemdata = new ElderItemData();
    	String selection = elder==null?null:"elder_item.elder_id="+elder.getElderId();
    	dbCache.delete(elderItemdata, "elder_item", selection);
    }
    
    /*
     * store a record for elderItem ,
     *   is_submit inside elderRecord must be set True explicitly
     *   after successfully uploading the record
     *    or it'll be set to false 
     * */
    public void storeElderRecord(ElderRecord elderRecord) throws IOException{
    	ElderItemRecordData elderItemRecordData = new ElderItemRecordData();
    	dbCache.insert(elderItemRecordData, elderRecord);
    	
    }
    /**
     * delete elder's elder_item records
     * if elder is null , delete all records 
     * */
    public void deleteElderRecords(Elder elder) throws IOException{
    	ElderItemRecordData elderItemRecordData = new ElderItemRecordData();
    	String selection = elder==null?null:"elder_item_record.elder_id="+elder.getElderId();
    	dbCache.delete(elderItemRecordData, "elder_item_record", selection);
    }
    
    /*
     * change 'is_submit' s value in TABLE-<elder_item_record> from false to true
     *  one-way change
     *  update once a time by carer_id, because a record should belong to a carer
     * */
    public void setElderRecordsSubmitted(int carerId) throws IOException{
    	ElderItemRecordData elderItemRecordData = new ElderItemRecordData();
    	ContentValues values = new ContentValues(1);
    	values.put("is_submit", true);
    	String selection = "carer_id="+carerId+" AND is_submit=0";
    	dbCache.update(elderItemRecordData, "elder_item_record",values,selection);
    }
    
}
