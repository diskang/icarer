package com.sjtu.icarer.persistence;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import com.sjtu.icarer.core.app.PreferenceManager;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.utils.RequestReader;
import com.sjtu.icarer.persistence.utils.RequestWriter;
import com.sjtu.icarer.service.IcarerService;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.TabHost;

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
    
}
