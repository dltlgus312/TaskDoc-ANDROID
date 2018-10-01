package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.UserInfoVO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserInfoCRUD {

    @GET("/userinfo/{uid}")
    Call<UserInfoVO> getUserinfo(@Path("uid") String uid);

    @POST("/userinfo/login")
    Call<Integer> userLogin(@Body UserInfoVO userInfoVO);

    @POST("/userinfo")
    Call<Integer> createUser(@Body UserInfoVO userInfoVO);

    @PUT("/userinfo")
    Call<Integer> updateUser(@Body UserInfoVO userInfoVO);

    @DELETE("/userinfo")
    Call<Integer> deleteUser(@Body UserInfoVO userInfoVO);
}
