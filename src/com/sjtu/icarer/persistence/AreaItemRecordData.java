package com.sjtu.icarer.persistence;

import static com.sjtu.icarer.common.utils.TimeUtils.DEFAULT_DATE_FORMAT;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.sjtu.icarer.common.utils.TimeUtils;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.persistence.utils.NormalResource;
import com.sjtu.icarer.service.IcarerService;

public class AreaItemRecordData extends NormalResource<AreaRecord>{
	@Inject @Named("Auth")IcarerService icarerService;
	@Inject DbCache dbCache;
	@Override
	public Cursor getCursor(SQLiteDatabase readableDatabase) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables("area_item_record");
        return builder
                .query(readableDatabase, new String[] { "carer_id","area_id",
                        "item_id", "item_name","finish_time", "is_submit" },
                        null, null, null, null, null);
	}

	@Override
	public AreaRecord loadFrom(Cursor cursor) {
		AreaRecord areaRecord = new AreaRecord();
		areaRecord.setStaffId(cursor.getInt(0));
		areaRecord.setAreaId(cursor.getInt(1));
		areaRecord.addAreaItem(cursor.getInt(2),cursor.getString(3));
		try{
			areaRecord.setFinishTime(TimeUtils.parse(cursor.getString(4), DEFAULT_DATE_FORMAT));
		}catch(Exception e){
        //TODO
		}
		areaRecord.setSubmit(cursor.getInt(5)==1?true:false);
		return areaRecord;
	}

	@Override
	public void store(SQLiteDatabase writableDatabase, List<AreaRecord> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AreaRecord> request() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean insert(SQLiteDatabase db, AreaRecord areaRecord) {
		if (areaRecord.isEmpty())
            return false;
	     int carerId = areaRecord.getStaffId();
	     int areaId = areaRecord.getAreaId();
	     Date finishTime = areaRecord.getFinishTime();
	     Set<AreaRecord.item> areaItems = areaRecord.getAreaItem();
	     Boolean isSubmit = areaRecord.isSubmit();
        ContentValues values = new ContentValues(5);
        for (AreaRecord.item item : areaItems) {
            values.clear();
            values.put("carer_id", carerId);
            values.put("area_id", areaId);
            values.put("item_id", item.getId());
            values.put("item_name", item.getName());
            values.put("finish_time", TimeUtils.getTime(finishTime.getTime(), DEFAULT_DATE_FORMAT));
            values.put("is_submit", isSubmit);
            long result = db.insert("area_item_record", null, values);
            if(result==-1){
            	return false;
            }else{
            	continue;
            }
         }
        return true;
	}

}
