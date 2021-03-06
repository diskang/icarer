package com.sjtu.icarer.core;

import java.util.List;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.persistence.DbManager;

public class LoadElderItemTask extends ProgressDialogTask<List<ElderItem>>{
	private final DbManager dbManager;
	private final Elder elder;
	protected LoadElderItemTask(Activity activity,DbManager dbManager, Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public LoadElderItemTask start(){
		showIndeterminate(getString(R.string.message_fetch_careitem_data));
		execute();
		return this;
	}
	
	@Override
	public List<ElderItem> call() throws Exception {
		List<ElderItem> elderItems = dbManager.getAllElderItems(elder, false);
		return elderItems;
	}
}
