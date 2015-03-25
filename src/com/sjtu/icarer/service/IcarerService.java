package com.sjtu.icarer.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit.RestAdapter;

import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.model.HttpModel;
import com.sjtu.icarer.model.User;

/**
 * @author kang shiyong
 * @Date 2015/3/25
 */
public class IcarerService {
	private RestAdapter restAdapter;
	
	/**
     * Create icarer service
     * Default CTOR
     */
	public IcarerService() {
	}
	
	/**
     * Create icarer service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
	@Inject
	public IcarerService(RestAdapter restAdapter){
		this.restAdapter = restAdapter;
	}
	
	/*
	 * 
	 * Area Part
	 * 
	 */
	public Area getArea(int geroId, int areaId){
		HttpModel<Area> model = getAreaService().getArea(geroId, areaId);
		return model.getEntity();
	}
	/*
	 * 
	 * Schedule Part
	 * 
	 */
	
	/*
	 * initialize the date to current date
	 * Assume an elder binds to multiple carers
	 */
	public List<Carer> getCurrentElderCarers(int geroId, int elderId){
		HttpModel<Carer> model = getScheduleService().getElderCarers(geroId, elderId, new Date());
		return model.getEntities();
	}
	
	/*
	 * initialize the date to current date
	 * Assume an area binds to multiple carers
	 */
	public List<Carer> getCurrentAreaCarers(int geroId, int areaId){
		HttpModel<Carer> model = getScheduleService().getAreaCarers(geroId, areaId, new Date());
		return model.getEntities();
	}
	
	/*
	 * 
	 * User Part
	 * 
	 */
	public String getUserKey(String username) {
        HttpModel<String> model= getUserService().getUserSecretKey(new User(username,"admin"));
        return model.getEntity();
    }
	
	public List<Elder> getElderByArea(int geroId){
		HttpModel<Elder> model = getUserService().getElders(geroId, 0);
		return model.getEntities();
	}
	
	/*
	 * 
	 * Work Part
	 * 
	 */
	public List<AreaItem> getAreaItems(int geroId){
		HttpModel<AreaItem> model = getWorkService().getAreaItems(geroId);
		return model.getEntities();
	}
	
	public List<ElderItem> getElderItems(int geroId, int elderId){
		HttpModel<ElderItem> model = getWorkService().getElderItems(geroId, elderId);
		return model.getEntities();
	}
	
	public boolean insertCarework(int geroId, ElderRecord elderRecord){
		HttpModel<?> model = getWorkService().insertCarework(geroId, elderRecord);
		if(model.isOk()){
			return true;
		}
		return false;
	}
	
	public boolean insertAreawork(int geroId, AreaRecord areaRecord){
		HttpModel<?> model = getWorkService().insertAreawork(geroId, areaRecord);
		if(model.isOk()){
			return true;
		}
		return false;
	}
	
	/*---------------------------------------------------------*/
	
	private RestAdapter getRestAdapter() {
        return restAdapter;
    }
	
	private AreaService getAreaService(){
		return getRestAdapter().create(AreaService.class);
	}
	
	private ScheduleService getScheduleService(){
		return getRestAdapter().create(ScheduleService.class);
	}
	
	private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }
	
	private WorkService getWorkService(){
		return getRestAdapter().create(WorkService.class);
	}
}
