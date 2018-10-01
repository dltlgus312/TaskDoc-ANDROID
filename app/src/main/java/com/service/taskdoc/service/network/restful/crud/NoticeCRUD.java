package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.database.transfer.NoticeVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NoticeCRUD {
    @GET("/notice/project/{pcode}")
    Call<List<NoticeVO>> getNoticeList(@Path("pcode") int pcode);

    @GET("/notice/{ncode}")
    Call<NoticeVO> getNotice(@Path("ncode") int ncode);

    @POST("/notice")
    Call<NoticeVO> createNotice(@Body NoticeVO noticeVO);

    @PUT("/notice")
    Call<Integer> updateNotice(@Body NoticeVO noticeVO);

    @DELETE("/notice/{ncode}")
    Call<Integer> deleteNotice(@Path("ncode") int ncode);
}
