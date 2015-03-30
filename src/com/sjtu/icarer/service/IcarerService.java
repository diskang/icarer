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
				.getArea(geroId, areaId, username, getDigestWithUsername());
		return model.getEntity();
	}
	
	public List<Area> getAreas(int level, int parentId){
		
		String digestValue = getDigest(level+""+parentId+username);
		HttpWrapper<Area> model = getAreaService()
				.getAreas(geroId, level, parentId, username, digestValue);
		return model.getEntities();
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
		Date currentDate = new Date();
		
		String digestValue = getDigest(currentDate+username);
		HttpWrapper<Carer> model = getScheduleService()
				.getElderCarers(geroId, elderId, currentDate, username, digestValue);
		return model.getEntities();
	}
	
	/*
	 * initialize the date to current date
	 * Assume an area binds to multiple carers
	 */
	public List<Carer> getCurrentAreaCarers(int areaId){
		
		Date currentDate = new Date();
		
		String digestValue = getDigest(currentDate+username);
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
		String digestValue = getDigest(areaId+"120ID"+username);
		HttpWrapper<Elder> model = getUserService().getElders(geroId, areaId,"1", "20","ID",username, digestValue);
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
		String digestValue = getDigestWithUsername();
		HttpWrapper<AreaItem> model = getWorkService().
				getAreaItems(geroId, username, digestValue);
		return model.getEntities();
	}
	
	public List<ElderItem> getElderItems(int elderId){
		String digestValue = getDigestWithUsername();
		HttpWrapper<ElderItem> model = getWorkService().
				getElderItems(geroId, elderId, username, digestValue);
		return model.getEntities();
	}
	
	public boolean insertCarework(ElderRecord elderRecord){
		String digestValue = getDigestWithUsername();
		HttpWrapper<?> model = getWorkService()
				.insertCarework(geroId, elderRecord, username, digestValue);
		if(model.isOk()){
			return true;
		}
		return false;
	}
	
	public boolean insertAreawork(AreaRecord areaRecord){
		String digestValue = getDigestWithUsername();
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
	
	
	@SuppressWarnings("unused")
	private String getDigest(Map<String, String[]> params){
		Map<String,Object> p = new HashMap<String,Object>(params);
		String[] usernameList = {username};
		p.put("username", usernameList);
		return HmacSHA256Utils.digest(this.digest, p);
	}
	
	/*
	 * @linedParams: param values joined in key's alphabet order
	 * */
	private String getDigest(String linedParams){
		
		return HmacSHA256Utils.digest(this.digest, linedParams);
	}
	
	private String getDigestWithUsername(){
//		Map<String,Object> p = new HashMap<String,Object>();
//		String[] usernameList = {username};
//		p.put("username", usernameList);
//		return HmacSHA256Utils.digest(this.digest, p);
		return getDigest(username);
	}
}
