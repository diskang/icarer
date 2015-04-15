/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sjtu.icarer.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.sjtu.icarer.persistence.utils.DbHelper;
import com.sjtu.icarer.persistence.utils.NormalResource;
import com.sjtu.icarer.persistence.utils.PersistableResource;

/**
 * Given a PersistableResource, this class will take support loading/storing
 * it's data or requesting fresh data, as appropriate.
 */
public class DbCache {

    private static final String TAG = "DatabaseCache";

    @Inject
    protected Provider<DbHelper> helperProvider;

    /**
     * Get writable database
     *
     * @param helper
     * @return writable database or null if it failed to create/open
     */
    public SQLiteDatabase getWritable(SQLiteOpenHelper helper) {
        try {
            return helper.getWritableDatabase();
        } catch (SQLiteException e1) {
            // Make second attempt
            try {
                return helper.getWritableDatabase();
            } catch (SQLiteException e2) {
                return null;
            }
        }
    }

    /**
     * Get readable database
     *
     * @param helper
     * @return readable database or null if it failed to create/open
     */
    public SQLiteDatabase getReadable(SQLiteOpenHelper helper) {
        try {
            return helper.getReadableDatabase();
        } catch (SQLiteException e1) {
            // Make second attempt
            try {
                return helper.getReadableDatabase();
            } catch (SQLiteException e2) {
                return null;
            }
        }
    }
    
    /**
     * Load or request given resources
     *
     * @param persistableResource
     * @return resource
     * @throws IOException
     */
    public <E> List<E> loadOrRequest(PersistableResource<E> persistableResource)
            throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            List<E> items = loadFromDB(helper, persistableResource);
            if (items != null) {
                Log.d(TAG, "CACHE HIT: Found " + items.size() + " items for "
                        + persistableResource);
                return items;
            }
            return requestAndStore(helper, persistableResource);
        } finally {
            helper.close();
        }
    }

    /**
     * Request and store given resources
     *
     * @param persistableResource
     * @return resources
     * @throws IOException
     */
    public <E> List<E> requestAndStore(
            PersistableResource<E> persistableResource) throws IOException {
        SQLiteOpenHelper helper = helperProvider.get();
        try {
            return requestAndStore(helper, persistableResource);
        } finally {
            helper.close();
        }
    }
    /**
     * load data by given parameters
     *  provide more adaptability
     * */
    public <E> List<E> load(final NormalResource<E> normalResource,
    		String table, String[] projectionIn, String selection, String groupBy)throws IOException {
    	SQLiteOpenHelper helper = helperProvider.get();
        try {
            List<E> items = loadFromDB(helper, normalResource,table, projectionIn, selection,groupBy);
            if (items != null) {
                Log.d(TAG, "CACHE HIT: Found " + items.size() + " items for " + normalResource);
            }else{
            	items= new ArrayList<E>();
            	Log.d(TAG,"CACHE : NOT FOUND" + " items for " + normalResource);
            }
            return items;
        } finally {
            helper.close();
        }
    }
    /*
     *  insert a single item
     * */
    public <E>void insert(final NormalResource<E> normalResource,E item){
    	SQLiteOpenHelper helper = helperProvider.get();
    	final SQLiteDatabase db = getWritable(helper);
        if (db == null)
            return ;
       
        db.beginTransaction();
        try {
        	   normalResource.insert(db, item);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        helper.close();
    }
    
    /* TODO insert a list of items*/
    
    
    /**
     * update given items in db's resources
     * @param <E>
     *
     * @param persistableResource
     * @param items
     * @return updated resource
     * @throws IOException
     */
    public <E> void update(NormalResource<E> normalResource,String table, 
    		ContentValues values, String selection) throws IOException {
    	SQLiteOpenHelper helper = helperProvider.get();
    	final SQLiteDatabase db = getWritable(helper);
        if (db == null)
            return;

        db.beginTransaction();
        try {
        	   normalResource.update(db, table, values, selection);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        helper.close();
    }
    
    public <E> void delete(NormalResource<E> normalResource,
    		String table,String selection) throws IOException{
    	SQLiteOpenHelper helper = helperProvider.get();
    	final SQLiteDatabase db = getWritable(helper);
        if (db == null)
            return;
        db.beginTransaction();
        try {
        	normalResource.delete(db, table, selection);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        helper.close();
    }
    
    private <E> List<E> requestAndStore(final SQLiteOpenHelper helper,
            final PersistableResource<E> persistableResource)
            throws IOException {
        final List<E> items = persistableResource.request();

        final SQLiteDatabase db = getWritable(helper);
        if (db == null)
            return items;

        db.beginTransaction();
        try {
            persistableResource.store(db, items);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return items;
    }

    
    private <E> List<E> loadFromDB(final SQLiteOpenHelper helper,
            final PersistableResource<E> persistableResource) {
        final SQLiteDatabase db = getReadable(helper);
        if (db == null)
            return null;

        Cursor cursor = persistableResource.getCursor(db);
        try {
            if (!cursor.moveToFirst())
                return null;

            List<E> cached = new ArrayList<E>();
            do
                cached.add(persistableResource.loadFrom(cursor));
            while (cursor.moveToNext());
            return cached;
        } finally {
            cursor.close();
        }
    }
    
    private <E> List<E> loadFromDB(final SQLiteOpenHelper helper,
            final NormalResource<E> normalResource,String table, String[] projectionIn,
            String selection, String groupBy) {
    	
        final SQLiteDatabase db = getReadable(helper);
        if (db == null)
            return null;

        Cursor cursor = normalResource.getCursor(db, table, projectionIn, selection,groupBy);
        try {
            if (!cursor.moveToFirst())
                return null;
            List<E> cached = new ArrayList<E>();
            do
                cached.add(normalResource.loadFrom(cursor));
            while (cursor.moveToNext());
            return cached;
        } finally {
            cursor.close();
        }
    }
}
