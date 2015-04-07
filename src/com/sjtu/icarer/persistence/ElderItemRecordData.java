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
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.persistence.utils.NormalResource;
import com.sjtu.icarer.service.IcarerService;

public class ElderItemRecordData extends NormalResource<ElderRecord>{
//    private final IcarerService icarerService;
//    public ElderItemRecordData(IcarerService icarerService){
//         this.icarerService = icarerService;
//    }
    @Inject @Named("Auth")IcarerService icarerService;
    @Inject DbCache dbCache;
	@Override
	public Cursor getCursor(SQLiteDatabase readableDatabase) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables("elder_item_record");
        return builder
                .query(readableDatabase, new String[] { "carer_id","elder_id",
                        "item_id", "item_name","finish_time", "is_submit" },
                        null, null, null, null, null);
	}
    @Override
    public ElderRecord loadFrom(Cursor cursor) {
        ElderRecord elderRecord = new ElderRecord();
        elderRecord.setStaffId(cursor.getInt(0));
        elderRecord.setElderId(cursor.getInt(1));
        elderRecord.addElderItem(cursor.getInt(2),cursor.getString(3));
        try{
		      elderRecord.setFinishTime(TimeUtils.parse(cursor.getString(4), DEFAULT_DATE_FORMAT));
        }catch(Exception e){
        //TODO
        }
		elderRecord.setSubmit(cursor.getInt(5)==1?true:false);
		return elderRecord;
	}
    
    @Override
    public Boolean insert(SQLiteDatabase db,ElderRecord elderRecord) {
	     if (elderRecord.isEmpty())
            return false;
	     int carerId = elderRecord.getStaffId();
	     int elderId = elderRecord.getElderId();
	     Date finishTime = elderRecord.getFinishTime();
	     Set<ElderRecord.item> elderItems = elderRecord.getElderItem();
	     Boolean isSubmit = elderRecord.isSubmit();
        ContentValues values = new ContentValues(5);
        for (ElderRecord.item item : elderItems) {
            values.clear();
            values.put("carer_id", carerId);
            values.put("elder_id", elderId);
            values.put("item_id", item.getId());
            values.put("item_name", item.getName());
            values.put("finish_time", TimeUtils.getTime(finishTime.getTime(), DEFAULT_DATE_FORMAT));
            values.put("is_submit", isSubmit);
            long result = db.insert("elder_item_record", null, values);
            if(result==-1){
            	return false;
            }else{
            	continue;
            }
         }
        return true;
	 }

	@Override
	public void store(SQLiteDatabase writableDatabase, List<ElderRecord> items) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<ElderRecord> request() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}
