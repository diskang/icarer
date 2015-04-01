package com.sjtu.icarer.core;

import java.util.List;

import javax.inject.Inject;

import android.app.Activity;
import android.os.Handler;

import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;

public class AreaTask extends SafeAsyncTask<List<Area>>{
	@Inject
	IcarerServiceProvider icarerServiceProvider;
	protected IcarerService icarerService;
//	private int areaId;
	private int level;
	private int parentId;
	private Activity activity;
//	public AreaTask(int areaId,Activity activity) {
//		// TODO Auto-generated constructor stub
//		this.areaId =areaId;
//		this.activity = activity;
//	}
	
	public AreaTask(int level,int parentId,Activity activity,Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		this.level = level;
		this.parentId = parentId;
		this.activity = activity;
	}
	
	@Override
	public List<Area> call() throws Exception {
		// TODO Auto-generated method stub
		icarerService = icarerServiceProvider.getService(activity);
		return icarerService.getAreas(level, parentId);
		
	}
	

}
