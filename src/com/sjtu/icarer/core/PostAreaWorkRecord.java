package com.sjtu.icarer.core;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerService;

public class PostAreaWorkRecord extends ProgressDialogTask<Boolean>{
	private IcarerService icarerService;
	private DbManager dbManager;
	private AreaRecord areaRecords;
	
	@Inject
	protected PostAreaWorkRecord(Activity activity,
			IcarerService icarerService, DbManager dbManager, 
			PreferenceManager preferenceManager,AreaRecord areaRecords,Carer carer) {
		super(activity);
		int areaId = preferenceManager.getAreaId();
		int carerId = carer.getId();
		this.areaRecords =  areaRecords;
		this.areaRecords.setAreaId(areaId);
		this.areaRecords.setStaffId(carerId);
		this.icarerService = icarerService;
		this.dbManager = dbManager;
	}
	
	public PostAreaWorkRecord start(){
		showIndeterminate(getString(R.string.message_post_areaitem_data));
		execute();
		return this;
	}
	
	@Override
	public Boolean call() throws Exception {
		this.areaRecords.setFinishTime(new Date());
		Boolean result = icarerService.insertAreawork(areaRecords);
		return result;
	}
	
    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);
        try{
            storeIntoDb(false);
        }catch(IOException err){
        	   throw new RuntimeException(err);
        }
    }
    
    @Override
    protected void onSuccess(Boolean result) throws IOException{
    	//if(!result){//store anyway
    		storeIntoDb(result);
    	//}
    }
    
    private void storeIntoDb(Boolean result) throws IOException{
    	areaRecords.setSubmit(result);
    	dbManager.storeAreaRecord(areaRecords);
    }
}
