package com.sjtu.icarer.core;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

public class ClearElderItemRecordTask extends ProgressDialogTask<Boolean>{
	private final DbManager dbManager;
	private final Elder elder;
	protected ClearElderItemRecordTask(Activity activity, DbManager dbManager,Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public ClearElderItemRecordTask start(){
		showIndeterminate(getString(R.string.message_clear_data));
		execute();
		return this;
	}
	
	@Override
	public Boolean call() throws Exception {
		dbManager.deleteElderRecords(elder);
		return true;
	}

}
