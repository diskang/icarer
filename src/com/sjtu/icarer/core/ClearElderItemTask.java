package com.sjtu.icarer.core;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

public class ClearElderItemTask extends ProgressDialogTask<Boolean>{
	private final DbManager dbManager;
	private final Elder elder;
	protected ClearElderItemTask(Activity activity, DbManager dbManager,Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public ClearElderItemTask start(){
		showIndeterminate(getString(R.string.message_clear_data));
		execute();
		return this;
	}
	
	/*
	 * delete elder's items, so elder's records are deleted too
	 * */
	@Override
	public Boolean call() throws Exception {
		dbManager.deleteElderItems(elder);
		dbManager.deleteElderRecords(elder);
		return true;
	}

}
