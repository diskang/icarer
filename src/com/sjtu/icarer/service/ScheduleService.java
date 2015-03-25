package com.sjtu.icarer.service;

import java.util.Date;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.HttpModel;

public interface ScheduleService {
	//TODO
	@GET(Url.URL_SCHEDULE_PLAN)
	HttpModel<?> getSchedulePlan(@Path("gid")int geroId,@Query("start_date")String startDate);
	
	//TODO
	@GET(Url.URL_CAREWORK)
	HttpModel<?> getCarework(@Path("gid")int geroId);
	
	//TODO
    @GET(Url.URL_AREAWORK)
    HttpModel<?> getAreawork(@Path("gid")int geroId);
	
	@GET(Url.URL_ELDER_CARERS)
	HttpModel<Carer> getElderCarers(
			@Path("gid")int geroId, 
			@Path("eid")int elderId, 
			@Query("date") Date date);
	
	@GET(Url.URL_AREA_CARERS)
	HttpModel<Carer> getAreaCarers(
			@Path("gid")int geroId,
			@Path("aid")int areaId,
			@Query("date") Date date);
}
