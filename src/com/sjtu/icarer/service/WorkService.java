package com.sjtu.icarer.service;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.model.CareItem;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.model.HttpWrapper;

public interface WorkService {
	//TODO
    @GET(Url.URL_CARE_ITEMS)
    HttpWrapper<CareItem> getCareItems(@Path("gid")int geroId);

    @GET(Url.URL_AREA_ITEMS)
	HttpWrapper<AreaItem> getAreaItems(
			@Path("gid") int geroId,
			@Query("username")String username,
			@Query("digest")String digest
			);
    
    @GET(Url.URL_ELDER_ITEMS)
    HttpWrapper<ElderItem> getElderItems(
    		@Path("gid") int geroId, 
    		@Path("eid")int elderId,
			@Query("username")String username,
			@Query("digest")String digest
			);
    
    @POST(Url.URL_CAREWORK_RECORD)
    HttpWrapper<?> insertCarework(
    		@Path("gid")int geroId,
    		@Body ElderRecord elderRecord,
			@Query("username")String username,
			@Query("digest")String digest
			);
    
	@POST(Url.URL_AREAWORK_RECORD)
	HttpWrapper<?> insertAreawork(
			@Path("gid")int geroId,
			@Body AreaRecord areaRecord,
			@Query("username")String username,
			@Query("digest")String digest
			);
    
}
