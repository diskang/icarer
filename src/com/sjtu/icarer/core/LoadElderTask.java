package com.sjtu.icarer.core;

import java.util.List;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

public class LoadElderTask extends ProgressDialogTask<List<Elder>>{
	private final DbManager dbManager;
	private final Boolean forceReload;
	protected LoadElderTask(Activity activity,DbManager dbManager, Boolean forceReload) {
		super(activity);
		this.dbManager = dbManager;
		this.forceReload = forceReload;
	}
	
	public LoadElderTask start(){
		showIndeterminate(getString(R.string.message_fetch_data));
		execute();
		return this;
	}
	
	@Override
	public List<Elder> call() throws Exception {
		List<Elder> elders = dbManager.getElders(forceReload);
		return elders;
	}
}
