package com.sjtu.icarer.service;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.HttpModel;
import com.sjtu.icarer.model.User;

public interface UserService {
	//TODO
	@GET(Url.URL_USER)
	User getUser(@Path("uid")int userId);
	
	@GET(Url.URL_USER_ELDERS)
	HttpModel<Elder> getElders(@Path("gid")int geroId, @Query("area_id")int areaId);
	
	@POST(Url.URL_USER_KEY)
    HttpModel<String> getUserSecretKey(@Body User user);
	
}
