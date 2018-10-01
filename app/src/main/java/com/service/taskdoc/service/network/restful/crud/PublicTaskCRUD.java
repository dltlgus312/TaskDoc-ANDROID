package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.PublicTaskVO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PublicTaskCRUD {

    @GET("/publictask/{pcode}")
    Call<List<PublicTaskVO>> getPublictaskList(@Path("pcode") int pcode);

    @GET("/publictask/all/{pcode}/{uid}")
    Call<Map<String, Object>> getListAll(@Path("pcode") int pcode, @Path("uid") String uid);

    @POST("/publictask")
    Call<Integer> createPublicTask(@Body PublicTaskVO publicTaskVO);

    @PUT("/publictask")
    Call<Integer> updatePublicTask(@Body PublicTaskVO publicTaskVO);

    @DELETE("/publictask/{tcode}")
    Call<Integer> deletePublicTask(@Path("tcode") int tcode);
}
