package com.sjtu.icarer.core;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

public class ClearElderTask extends ProgressDialogTask<Boolean>{
	private final DbManager dbManager;
	private final Elder elder;
	protected ClearElderTask(Activity activity, DbManager dbManager,Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public ClearElderTask start(){
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
		dbManager.deleteElderItems(elder);
		dbManager.deleteElderRecords(elder);
		return true;
	}

}
