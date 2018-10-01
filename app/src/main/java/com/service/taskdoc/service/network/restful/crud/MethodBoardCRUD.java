package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.MethodBoardVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MethodBoardCRUD {
    @GET("/methodboard/all")
    Call<List<MethodBoardVO>> getMethodBoardList();

    @GET("/methodboard/mylist/{uid}")
    Call<List<MethodBoardVO>> getMethodBoardMyList(@Path("uid") String uid);

    @GET("/methodboard/{mbcode}")
    Call<MethodBoardVO> getMethodBoard(@Path("mbcode") int mbcode);

    @POST("/methodboard")
    Call<Integer> createMethodBoard(@Body MethodBoardVO methodBoardVO);

    @PUT("/methodboard")
    Call<Integer> updateMethodBoard(@Body MethodBoardVO methodBoardVO);

    @DELETE("/methodboard/{mbcode}")
    Call<Integer> deleteMethodBoard(@Path("mbcode") int mbcode);
}
