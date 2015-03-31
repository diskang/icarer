package com.sjtu.icarer.persistence;

import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.persistence.utils.PersistableResource;
import com.sjtu.icarer.service.IcarerService;

public class AreaItemData implements PersistableResource<AreaItem>{
	private final IcarerService icarerService;
	
	public AreaItemData(IcarerService icarerService) {
	    this.icarerService = icarerService;
	}
	
	@Override
	public Cursor getCursor(SQLiteDatabase readableDatabase) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("area_item");
        return builder
              .query(readableDatabase, new String[] { "area_item.id",
                      "area_item.icon", "area_item.gero_id","area_item.item_name", "area_item.frequency", "area_item.period" },
                      null, null, null, null, null);
	}

	@Override
	public AreaItem loadFrom(Cursor cursor) {
	    AreaItem areaItem = new AreaItem();
	    areaItem.setId(cursor.getInt(0));
	    areaItem.setIcon(cursor.getString(1));
	    areaItem.setGeroId(cursor.getInt(2));
	    areaItem.setName(cursor.getString(3));
	    areaItem.setFrequency(cursor.getInt(4));
	    areaItem.setPeriod(cursor.getInt(5));
	    return areaItem;
	}

	@Override
	public void store(SQLiteDatabase db, List<AreaItem> items) {
		if (items.isEmpty())
            return;
		db.delete("area_item", null, null);
		
        ContentValues values = new ContentValues(7);
        for (AreaItem item : items) {
            values.clear();
            
            values.put("id", item.getId());
            values.put("icon", item.getIcon());
            values.put("greo_id", item.getGeroId());
            values.put("item_name", item.getName());
            values.put("frequency", item.getFrequency());
            values.put("period", item.getPeriod());
            values.put("notes", item.getNotes()); 
            
            db.replace("area_item", null, values);
        }
	}

	@Override
	public List<AreaItem> request() throws IOException {
		List<AreaItem> areaItems = icarerService.getAreaItems();
		return areaItems;
	}

}
