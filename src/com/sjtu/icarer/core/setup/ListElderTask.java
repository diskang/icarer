package com.sjtu.icarer.core.setup;

import java.util.List;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.service.IcarerServiceProvider;

public class ListElderTask extends ProgressDialogTask<List<Elder>>{
	private int areaId;
	protected ListElderTask(Activity activity,int areaId,IcarerServiceProvider icarerServiceProvider) {
		super(activity,icarerServiceProvider);
		this.areaId = areaId;
	}
	
	public ListElderTask start(){
		showIndeterminate(getString(R.string.message_fetch_data));
		execute();
		return this;
	}
	@Override
	public List<Elder> call() throws Exception {
		super.call();
		List<Elder> elders = icarerService.getElderByArea(areaId);		
		return elders;
	}
    
}
