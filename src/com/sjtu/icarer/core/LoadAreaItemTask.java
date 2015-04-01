package com.sjtu.icarer.core;

import java.util.List;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.persistence.DbManager;

public class LoadAreaItemTask extends ProgressDialogTask<List<AreaItem>>{
	private final DbManager dbManager;
	private final Boolean forceReload;
	
	protected LoadAreaItemTask(Activity activity,DbManager dbManager, Boolean forceReload) {
		super(activity);
		this.dbManager = dbManager;
		this.forceReload = forceReload;
	}
	
	public LoadAreaItemTask start(){
		showIndeterminate(getString(R.string.message_fetch_areaitem_data));
		execute();
		return this;
	}
	
	@Override
	public List<AreaItem> call() throws Exception {
		List<AreaItem> areaItems = dbManager.getAreaItems(forceReload);
		return areaItems;
	}
}
