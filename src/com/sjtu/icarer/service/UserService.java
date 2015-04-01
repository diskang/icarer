package com.sjtu.icarer.service;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.model.HttpWrapper;
import com.sjtu.icarer.model.User;

public interface UserService {
	/*admin获取自己用户信息*/
	@GET(Url.URL_USER)
	User getUser(@Path("uid")int userId);
	
	@GET(Url.URL_USER_ELDERS)
	HttpWrapper<Elder> getElders(
			@Path("gid")int geroId, 
			@Query("area_id")int areaId,
			@Query("page")String page,
			@Query("rows")String rows,
			@Query("sort")String sort,
			@Query("username")String username,
			@Query("digest") String digest
			);
	
	//pad login
	@POST(Url.URL_USER_KEY)
    HttpWrapper<User> userLogin(@Body User user);
}
