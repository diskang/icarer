package com.sjtu.icarer.service;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.HttpWrapper;

public interface ScheduleService {
	//TODO
	@GET(Url.URL_SCHEDULE_PLAN)
	HttpWrapper<?> getSchedulePlan(@Path("gid")int geroId,@Query("start_date")String startDate);
	
	//TODO
	@GET(Url.URL_CAREWORK)
	HttpWrapper<?> getCarework(@Path("gid")int geroId);
	
	//TODO
    @GET(Url.URL_AREAWORK)
    HttpWrapper<?> getAreawork(@Path("gid")int geroId);
	
	@GET(Url.URL_ELDER_CARERS)
	HttpWrapper<Carer> getElderCarers(
			@Path("gid")int geroId, 
			@Path("eid")int elderId, 
			@Query("date") String date,
			@Query("username")String username,
			@Query("digest")String digest
			);
	
	@GET(Url.URL_AREA_CARERS)
	HttpWrapper<Carer> getAreaCarers(
			@Path("gid")int geroId,
			@Path("aid")int areaId,
			@Query("date") String date,
			@Query("username")String username,
			@Query("digest")String digest
			);
}
