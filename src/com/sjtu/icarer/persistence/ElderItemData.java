package com.sjtu.icarer.persistence;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.persistence.utils.NormalResource;
import com.sjtu.icarer.service.IcarerService;

public class ElderItemData extends NormalResource<ElderItem>{
	private IcarerService icarerService;
	private int elderId=0;
	
	/*initialize to update item's state*/
	public ElderItemData(){
	}
	
	//initialize to get an elder's items
	public ElderItemData(IcarerService icarerService, Elder elder) {
		this.icarerService = icarerService;
		elderId = elder.getId();
	}
	
	@Override
	public Cursor getCursor(SQLiteDatabase readableDatabase) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("elder_item");
        builder.appendWhere("elder_item.elder_id="+elderId);
        return builder
              .query(readableDatabase, new String[] { "elder_item.id",
                      "elder_item.icon", "elder_item.item_id","elder_item.item_name",
                      "elder_item.period","elder_item.start_time","elder_item.end_time"},
                      null, null, null, null, null);
	}

	@Override
	public ElderItem loadFrom(Cursor cursor) {
		ElderItem elderItem = new ElderItem();
		elderItem.setId(cursor.getInt(0));
		elderItem.setIcon(cursor.getString(1));
		elderItem.setCareItemId(cursor.getInt(2));
		elderItem.setCareItemName(cursor.getString(3));
		elderItem.setElderId(cursor.getInt(4));
		elderItem.setPeriod(cursor.getInt(5));
		try{
			String startTime = cursor.getString(7);
			String endTime = cursor.getString(8);
		   if(startTime!=null&&!startTime.isEmpty()) elderItem.setStartTime(Time.valueOf(startTime));
		   if(endTime!=null&&!endTime.isEmpty()) elderItem.setEndTime(Time.valueOf(endTime));
		    LogUtils.d("starttime="+startTime);
		}catch (Exception e){
			e.printStackTrace();
			LogUtils.d("time value parse error");
		}
		return elderItem;
	}

	@Override
	public void store(SQLiteDatabase db, List<ElderItem> items) {
		if (items.isEmpty())
            return;
		 //db.delete("elder_item", null, null);
        ContentValues values = new ContentValues(9);
        for (ElderItem item : items) {
            values.clear();
            values.put("id", item.getId());
            values.put("icon", item.getIcon());
            values.put("item_id", item.getCareItemId());
            values.put("item_name", item.getCareItemName());
            values.put("elder_id", elderId);
            values.put("period", item.getPeriod());
            values.put("notes", item.getNotes()); 
            String starttime = item.getStartTime()==null?null:item.getStartTime().toString();
            String endtime = item.getEndTime()==null?null:item.getEndTime().toString();
            values.put("start_time", starttime);
            values.put("end_time", endtime);       
            
            db.replace("elder_item", null, values);
        }
	}

	@Override
	public List<ElderItem> request() throws IOException {
		List<ElderItem> items = icarerService.getElderItems(elderId);
		return items;
	}

	@Override
	public Boolean insert(SQLiteDatabase writableDatabase, ElderItem singleItem) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
