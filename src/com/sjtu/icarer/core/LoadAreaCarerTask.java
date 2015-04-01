package com.sjtu.icarer.core;

import java.util.List;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.persistence.DbManager;

public class LoadAreaCarerTask extends ProgressDialogTask<List<Carer>>{
	private final DbManager dbManager;
	private final Boolean forceReload;
	
	protected LoadAreaCarerTask(Activity activity,DbManager dbManager, Boolean forceReload) {
		super(activity);
		this.dbManager = dbManager;
		this.forceReload = forceReload;
	}
	
	public LoadAreaCarerTask start(){
		showIndeterminate(getString(R.string.message_fetch_carer_data));
		execute();
		return this;
	}
	
	@Override
	public List<Carer> call() throws Exception {
		List<Carer> carers = dbManager.getCarerByArea(forceReload);
		return carers;
	}
}
