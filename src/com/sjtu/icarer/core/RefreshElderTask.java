package com.sjtu.icarer.core;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

/*
 * delete all elders, trigger a request to get elders 
 * */
public class RefreshElderTask extends ProgressDialogTask<Boolean>{
	private final DbManager dbManager;
	private final Elder elder;
	protected RefreshElderTask(Activity activity, DbManager dbManager,Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public RefreshElderTask start(){
		showIndeterminate(getString(R.string.message_clear_data));
		execute();
		return this;
	}
	
	/*
	 * delete elder, so it's items and records are deleted too
	 * */
	@Override
	public Boolean call() throws Exception {
		dbManager.deleteElder(elder);
		dbManager.getElders(true);
		return true;
	}

}
