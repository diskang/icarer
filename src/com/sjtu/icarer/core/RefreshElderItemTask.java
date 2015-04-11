package com.sjtu.icarer.core;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

public class RefreshElderItemTask extends ProgressDialogTask<Boolean>{
	private final DbManager dbManager;
	private final Elder elder;
	protected RefreshElderItemTask(Activity activity, DbManager dbManager,Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public RefreshElderItemTask start(){
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
		if(elder!=null){
		    dbManager.getAllElderItems(elder, true);
		}
		return true;
	}

}
