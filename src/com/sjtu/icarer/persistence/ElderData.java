package com.sjtu.icarer.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.utils.NormalResource;
import com.sjtu.icarer.service.IcarerService;

public class ElderData extends NormalResource<Elder>{
    private final IcarerService icarerService;
    private final int areaId;
    
    public ElderData(IcarerService icarerService, int areaId) {
		this.icarerService = icarerService;
		this.areaId = areaId;
	}
    
	@Override
	public Cursor getCursor(SQLiteDatabase readableDatabase) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
//        builder.setTables("orgs JOIN users ON (orgs.id = users.id)");
        builder.setTables("elder");
        return builder
                .query(readableDatabase, new String[] { "elder.id","elder.elder_id",
                        "elder.name", "elder.username","elder.photo_url", "elder.area_id", "elder.care_level" },
                        null, null, null, null, null);
	}
	
	@Override
	public Elder loadFrom(Cursor cursor) {
		Elder elder = new Elder();
		elder.setId(cursor.getInt(0));
		elder.setElderId(cursor.getInt(1));
		elder.setName(cursor.getString(2));
		elder.setUsername(cursor.getString(3));
		elder.setPhotoUrl(cursor.getString(4));
		elder.setAreaId(cursor.getInt(5));
		elder.setCareLevel(cursor.getInt(6));
		return elder;
	}
	
	@Override
	public void store(SQLiteDatabase db, List<Elder> elders) {
		if (elders.isEmpty())
            return;
        db.delete("elder", null, null);

        ContentValues values = new ContentValues(7);
        for (Elder elder : elders) {
            values.clear();

            values.put("id", elder.getId());
            values.put("elder_id", elder.getElderId());
            values.put("name", elder.getName());
            values.put("username", elder.getUsername());
            values.put("photo_url", elder.getPhotoUrl());
            values.put("area_id", elder.getAreaId());
            values.put("care_level", elder.getCareLevel());
            db.replace("elder", null, values);
        }
		
	}
	
	@Override
	public List<Elder> request() throws IOException {
		List<Elder> elders = icarerService.getElderByArea(areaId);
        return new ArrayList<Elder>(elders);
	}

	@Override
	public Boolean insert(SQLiteDatabase writableDatabase, Elder singleItem) {
		// TODO Auto-generated method stub
		return null;
	}

}
