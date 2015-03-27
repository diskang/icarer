package com.sjtu.icarer.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.RestAdapter;

import com.sjtu.icarer.common.security.HmacSHA256Utils;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.model.HttpWrapper;
import com.sjtu.icarer.model.User;

/**
 * @author kang shiyong
 * @Date 2015/3/25
 */
public class IcarerService {
	private RestAdapter restAdapter;
	private String digest;
	private int geroId;
	private String username;
	
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
	
	public IcarerService(RestAdapter restAdapter,String digest,User user){
		this.restAdapter = restAdapter;
	    this.digest = digest;
	    this.geroId = user.getGeroId();
	    this.username = user.getUsername();
	}
	
	/*
	 * 
	 * Area Part
	 * 
	 */
	public Area getArea(int areaId){
		
		HttpWrapper<Area> model = getAreaService()
				.getArea(geroId, areaId, username, getDigest());
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
	public List<Carer> getCurrentElderCarers(int elderId){
		Map<String, Object> map= new HashMap<String, Object>();
		Date currentDate = new Date();
		map.put("date", currentDate);
		String digestValue = getDigest(map);
		HttpWrapper<Carer> model = getScheduleService()
				.getElderCarers(geroId, elderId, currentDate, username, digestValue);
		return model.getEntities();
	}
	
	/*
	 * initialize the date to current date
	 * Assume an area binds to multiple carers
	 */
	public List<Carer> getCurrentAreaCarers(int areaId){
		Map<String, Object> map= new HashMap<String, Object>();
		Date currentDate = new Date();
		map.put("date", currentDate);
		String digestValue = getDigest(map);
		HttpWrapper<Carer> model = getScheduleService()
				.getAreaCarers(geroId, areaId, currentDate, username, digestValue);
		return model.getEntities();
	}
	
	/*
	 * 
	 * User Part
	 * 
	 */
	
	public List<Elder> getElderByArea(int areaId){
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("area_id", areaId);
		String digestValue = getDigest(map);
		HttpWrapper<Elder> model = getUserService().getElders(geroId, areaId, username, digestValue);
		return model.getEntities();
	}
	
	/*GET a User Object with digest key*/
	public User authenticate(String username, String password) {
		User authUser = new User(username, password);
		HttpWrapper<User> model = getUserService().userLogin(authUser);
		authUser = model.getEntity();
		return authUser;
    }
	
	/*
	 * 
	 * Work Part
	 * 
	 */
	public List<AreaItem> getAreaItems(){
		String digestValue = getDigest();
		HttpWrapper<AreaItem> model = getWorkService().
				getAreaItems(geroId, username, digestValue);
		return model.getEntities();
	}
	
	public List<ElderItem> getElderItems(int elderId){
		String digestValue = getDigest();
		HttpWrapper<ElderItem> model = getWorkService().
				getElderItems(geroId, elderId, username, digestValue);
		return model.getEntities();
	}
	
	public boolean insertCarework(ElderRecord elderRecord){
		String digestValue = getDigest();
		HttpWrapper<?> model = getWorkService()
				.insertCarework(geroId, elderRecord, username, digestValue);
		if(model.isOk()){
			return true;
		}
		return false;
	}
	
	public boolean insertAreawork(AreaRecord areaRecord){
		String digestValue = getDigest();
		HttpWrapper<?> model = getWorkService().insertAreawork(geroId, areaRecord, username, digestValue);
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
	
	private String getDigest(Map<String, Object> params){
		Map<String,Object> p = new HashMap<String,Object>(params);
		p.put("username", username);
		return HmacSHA256Utils.digest(this.digest, p);
	}
	
	private String getDigest(){
		Map<String,Object> p = new HashMap<String,Object>();
		p.put("username", username);
		return HmacSHA256Utils.digest(this.digest, p);
	}
}
