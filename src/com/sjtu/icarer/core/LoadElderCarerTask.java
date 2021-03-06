package com.sjtu.icarer.core;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.sjtu.icarer.R;
import com.sjtu.icarer.core.utils.ProgressDialogTask;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;

public class LoadElderCarerTask extends ProgressDialogTask<List<Carer>>{
	private final DbManager dbManager;
	private final Elder elder;
	
	protected LoadElderCarerTask(Activity activity,DbManager dbManager,Elder elder) {
		super(activity);
		this.dbManager = dbManager;
		this.elder = elder;
	}
	
	public LoadElderCarerTask start(){
		showIndeterminate(getString(R.string.message_fetch_carer_data));
		execute();
		return this;
	}
	
	@Override
	public List<Carer> call() throws Exception {
		List<Carer> carers = dbManager.getCarerByElder(elder, false);
		if(carers==null||carers.size()==0)return new ArrayList<Carer>();//TODO
		return carers;
	}
}
