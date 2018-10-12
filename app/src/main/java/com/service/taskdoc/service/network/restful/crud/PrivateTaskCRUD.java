package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PrivateTaskCRUD {

    @GET("/privatetask/task/{tcode}")
    Call<List<PrivateTaskVO>> getPrivateTaskList(@Path("tcode") int tcode);

    @GET("/privatetask/user/{uid}")
    Call<List<PrivateTaskVO>> getPrivateTaskList(@Path("uid") String uid);

    @GET("/privatetask/{ptcode}")
    Call<PrivateTaskVO> getPrivateTaskView(@Path("ptcode") int ptcode);

    @POST("/privatetask")
    Call<Integer> createPrivateTask(@Body PrivateTaskVO privateTaskVO);

    @PUT("/privatetask")
    Call<Integer> updatePrivateTask(@Body PrivateTaskVO privateTaskVO);

    @DELETE("/privatetask/{ptcode}")
    Call<Integer> deletePrivateTask(@Path("ptcode") int ptcode);
}
