package com.sjtu.icarer.persistence;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.sjtu.icarer.common.utils.TimeUtils;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.utils.PersistableResource;
import com.sjtu.icarer.service.IcarerService;

import static com.sjtu.icarer.common.utils.TimeUtils.DATE_FORMAT_DATE;
public class CarerData implements PersistableResource<Carer>{

	private final IcarerService icarerService;
	private List<Elder> elders;
	private Elder elder;
	private int areaId=0;
	private String tableName;
	private List<Carer> carers=new ArrayList<Carer>();
	
	public CarerData(IcarerService icarerService, List<Elder> elders){//not support now
		this.icarerService = icarerService;
		this.elders = elders;
		this.tableName = "elder_carer";
	}
	public CarerData(IcarerService icarerService, Elder elder) {
		this.icarerService =icarerService;
	    this.elder = elder;
	    this.tableName="elder_carer";
	}
	
	public CarerData(IcarerService icarerService,int areaId){
		this.icarerService = icarerService;
		this.areaId = areaId;
		this.tableName = "area_carer";
	}
	
	@Override
	public Cursor getCursor(SQLiteDatabase readableDatabase) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        String workToday = "work_date ="+TimeUtils.getCurrentTimeInString(DATE_FORMAT_DATE);
        builder.appendWhere(workToday);
        if(elder!=null){
        	builder.appendWhere(" AND elder_id="+elder.getElderId());
        }else if(areaId!=0){
        	builder.appendWhere(" AND area_id="+areaId);
        }
        return builder
              .query(readableDatabase, new String[] { "id",
                      "name", "username","photo_url","work_date" },
                      null, null, null, null, null);
	}

	@Override
	public Carer loadFrom(Cursor cursor) {
		Carer carer = new Carer();
		carer.setId(cursor.getInt(0));
		carer.setName(cursor.getString(1));
		carer.setUsername(cursor.getString(2));
		carer.setPhotoUrl(cursor.getString(3));
		carer.setWorkDate(
				Date.valueOf(cursor.getString(4))
				);
		return carer;
	}

	@Override
	public void store(SQLiteDatabase db, List<Carer> carers) {
		//db.delete("carer", null, null);
        if (carers.isEmpty())
            return;
        ContentValues values = new ContentValues(6);
        for (Carer carer : carers) {
            values.clear();
            values.put("id", carer.getId());
            values.put("name", carer.getName());
            values.put("username", carer.getName());
            values.put("photo_url", carer.getPhotoUrl());
            values.put("work_date", TimeUtils.getCurrentTimeInString(DATE_FORMAT_DATE));
            if("area_carer".equals(tableName)){
            	values.put("area_id", areaId);
            }else{
            	values.put("elder_id", elder.getId());
            }
            db.replace(tableName, null, values);
        }
		
	}

	@Override
	public List<Carer> request() throws IOException {
		
		if(elder!=null){
			carers.addAll(icarerService.getCurrentElderCarers(elder.getElderId()));
		}else if(areaId!=0){
			carers.addAll(icarerService.getCurrentAreaCarers(areaId));
		}else if(elders!=null){//has problems, missing elder info in carers' list
			for(Elder elder:elders){
				carers.addAll(icarerService.getCurrentElderCarers(elder.getElderId()));
			}
		}
		return carers;
	}

}
