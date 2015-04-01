package com.sjtu.icarer.persistence;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.persistence.utils.PersistableResource;
import com.sjtu.icarer.service.IcarerService;

public class ElderItemData implements PersistableResource<ElderItem>{
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
        builder.appendWhere("elder_item.next_date is null OR elder_item.next_date is DATE(\"now\")");
        return builder
              .query(readableDatabase, new String[] { "elder_item.id",
                      "elder_item.icon", "elder_item.item_id","elder_item.item_name", "elder_item.period",
                      "elder_item.start_time","elder_item.end_time", "elder_item.last_date","elder_item.next_date" },
                      null, null, null, null, "elder_item.start_time");
	}

	@Override
	public ElderItem loadFrom(Cursor cursor) {
		ElderItem elderItem = new ElderItem();
		elderItem.setCareItemId(cursor.getInt(0));
		elderItem.setIcon(cursor.getString(1));
		elderItem.setCareItemId(cursor.getInt(2));
		elderItem.setCareItemName(cursor.getString(3));
		elderItem.setPeriod(cursor.getInt(4));
		elderItem.setStartTime(Time.valueOf(cursor.getString(5)));
		elderItem.setEndTime(Time.valueOf(cursor.getString(6)));
		elderItem.setLastDate(Date.valueOf(cursor.getString(7)));
		elderItem.setNextDate(Date.valueOf(cursor.getString(8)));
		return elderItem;
	}

	@Override
	public void store(SQLiteDatabase db, List<ElderItem> items) {
		if (items.isEmpty())
            return;
		
        ContentValues values = new ContentValues(10);
        for (ElderItem item : items) {
            values.clear();
            
            values.put("id", item.getId());
            values.put("icon", item.getIcon());
            values.put("item_id", item.getCareItemId());
            values.put("item_name", item.getCareItemName());
            values.put("period", item.getPeriod());
            values.put("notes", item.getNotes()); 
            values.put("start_time", item.getStartTime().toString());
            values.put("end_time", item.getEndTime().toString());
            values.put("last_date", item.getLastDate().toString());
            values.put("next_date", item.getNextDate().toString());        
            
            db.replace("elder_item", null, values);
        }
	}

	@Override
	public List<ElderItem> request() throws IOException {
		List<ElderItem> items = icarerService.getElderItems(elderId);
		return items;
	}
    
}
