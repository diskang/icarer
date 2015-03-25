package com.sjtu.icarer.service;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.model.HttpModel;

import retrofit.http.GET;
import retrofit.http.Path;

public interface AreaService {
    @GET(Url.URL_AREA)
    HttpModel<Area> getArea(@Path("gid")int geroId,@Path("aid")int areaId);
    
}
