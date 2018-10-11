package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.FeedBackVO;
import com.service.taskdoc.database.transfer.NoticeVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FeedBackCRUD {
    @GET("/feedback/{dmcode}")
    Call<List<FeedBackVO>> getList(@Path("dmcode") int dmcode);

    @POST("/feedback")
    Call<FeedBackVO> create(@Body FeedBackVO feedBackVo);

    @PUT("/feedback")
    Call<Integer> update(@Body FeedBackVO feedBackVo);

    @DELETE("/feedback/{fbcode}")
    Call<Integer> delete(@Path("fbcode") int fbcode);
}
