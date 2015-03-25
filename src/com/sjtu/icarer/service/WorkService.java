package com.sjtu.icarer.service;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.AreaItem;
import com.sjtu.icarer.model.AreaRecord;
import com.sjtu.icarer.model.CareItem;
import com.sjtu.icarer.model.ElderItem;
import com.sjtu.icarer.model.ElderRecord;
import com.sjtu.icarer.model.HttpModel;

public interface WorkService {
	//TODO
    @GET(Url.URL_CARE_ITEMS)
    HttpModel<CareItem> getCareItems(@Path("gid")int geroId);

    @GET(Url.URL_AREA_ITEMS)
	HttpModel<AreaItem> getAreaItems(@Path("gid") int geroId);
    
    @GET(Url.URL_ELDER_ITEMS)
    HttpModel<ElderItem> getElderItems(@Path("gid") int geroId, @Path("eid")int elderId);
    
    @POST(Url.URL_CAREWORK_RECORD)
    HttpModel<?> insertCarework(@Path("gid")int geroId,@Body ElderRecord elderRecord);
    
	@POST(Url.URL_AREAWORK_RECORD)
	HttpModel<?> insertAreawork(@Path("gid")int geroId,@Body AreaRecord areaRecord);
    
}
