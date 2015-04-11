package com.sjtu.icarer.core;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerService;

public class PostElderWorkRecord extends ProgressDialogTask<Boolean>{
	private final IcarerService icarerService;
	private final DbManager dbManager;
	private ElderRecord elderRecords;
	@Inject
	protected PostElderWorkRecord(Activity activity,
			IcarerService icarerService, DbManager dbManager,
			ElderRecord elderRecords,Carer carer, int elderId) {
		super(activity);
		int carerId = carer.getId();
		this.elderRecords =  elderRecords;
		this.elderRecords.setElderId(elderId);
		this.elderRecords.setStaffId(carerId);
		this.icarerService = icarerService;
		this.dbManager = dbManager;
	}
	
	public PostElderWorkRecord start(){
		showIndeterminate(getString(R.string.message_post_careitem_data));
		execute();
		return this;
	}
	
	@Override
	public Boolean call() throws Exception {
		this.elderRecords.setFinishTime(new Date());
		Boolean result = icarerService.insertCarework(elderRecords);
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
    		storeIntoDb(result);
    }
    
    private void storeIntoDb(Boolean result) throws IOException{
    	elderRecords.setSubmit(result);
    	dbManager.storeElderRecord(elderRecords);
    }
}
