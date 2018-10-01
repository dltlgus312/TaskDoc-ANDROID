package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.MethodListVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MethodListCRUD {
    @GET("/methodlist/{uid}")
    Call<List<MethodListVO>> getMethodList(@Path("uid") String uid);

    @POST("/methodlist")
    Call<Integer> createMethod(@Body MethodListVO methodListVO);

    @PUT("/methodlist")
    Call<Integer> updateMethod(@Body MethodListVO methodListVO);

    @HTTP(method = "DELETE", path = "/methodlist", hasBody = true)
    Call<Integer> deleteMethod(@Body MethodListVO methodListVO);
}
