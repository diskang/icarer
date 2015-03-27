package com.sjtu.icarer.service;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.model.HttpWrapper;

public interface AreaService {
    @GET(Url.URL_AREA)
    HttpWrapper<Area> getArea(
    		@Path("gid")int geroId,
    		@Path("aid")int areaId,
    		@Query("username")String username,
    		@Query("digest")String digest
    		);
    
}
