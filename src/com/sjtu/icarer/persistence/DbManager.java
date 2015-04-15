package com.sjtu.icarer.persistence;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
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
    @SuppressWarnings({"unchecked","unused"})
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
    @SuppressWarnings("unused")
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
        List<Carer> todayCarers = dbCache.loadOrRequest(carerData);
        if(todayCarers==null||todayCarers.isEmpty()){
            List<Carer> historyCarers =dbCache.load(carerData, "elder_carer", 
            		new String[]{"id","name","username","photo_url","max(work_date)"},
            		"elder_id="+elder.getElderId(),"id order by work_date desc");
        	   return historyCarers;
        }else{
            return todayCarers;
        }
    }
	/*
	 * select 
	 *     id,elder_id,name,username,photo_url,max(work_date) 
	 * from 
	 *     xxxx_carer 
	 * where 
	 *     xxxx_id=?
	 * group by 
	 *     id 
	 * order by 
	 *     work_date 
	 * desc;
	 * 
	 * 
	 * get most recent carers by elder or area,
	 * return common part in between TABLE elder_carer & area_carer
	 * */
    public List<Carer> getCarerByArea( boolean forceReload) throws IOException {
        int areaId = preferenceManager.getAreaId();
        CarerData carerData = new CarerData(icarerService, areaId);
        List<Carer> todayCarers = dbCache.loadOrRequest(carerData);
        if(todayCarers==null||todayCarers.isEmpty()){
            List<Carer> historyCarers =dbCache.load(carerData, "area_carer", 
            		new String[]{"id","name","username","photo_url","max(work_date)"},
            		"area_id="+areaId,"id order by work_date desc");
        	   return historyCarers;
        }else{
            return todayCarers;
        }
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
     * get elder's items with a submission times record
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
    	ElderItemData elderItemdata = new ElderItemData(icarerService,elder);
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
