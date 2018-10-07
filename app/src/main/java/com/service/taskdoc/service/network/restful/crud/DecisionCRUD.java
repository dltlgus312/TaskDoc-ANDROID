package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DecisionCRUD {
    @GET("/decision/task/{tcode}")
    Call<List<DecisionVO>> getTaskList(@Path("tcode") int tcode);

    @GET("/decision/room/{crcode}")
    Call<List<DecisionVO>> getChatList(@Path("crcode") int crcode);

    @POST("/decision/dec")
    Call<DecisionVO> createDecision(@Body DecisionVO decisionVO);
    
    @PUT("/decision") // Document의 제목이나 내용 변경
    Call<Integer> updateDecision(@Body DecisionVO decisionVO);

    @DELETE("/decision/{dscode}") // 파일도 같이 삭제
    Call<Integer> deleteDecision(@Path("dscode") int dscode);
}
