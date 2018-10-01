package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.database.transfer.MethodBoardVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MemoCRUD {
    @GET("/memo/{ptcode}")
    Call<List<MemoVO>> getMemoList(@Path("ptcode") int ptcode);

    @POST("/memo")
    Call<Integer> createMemo(@Body MemoVO memoVO);

    @PUT("/memo")
    Call<Integer> updateMemo(@Body MemoVO memoVO);

    @DELETE("/memo/{mcode}")
    Call<Integer> deleteMemo(@Path("mcode") int mcode);
}
