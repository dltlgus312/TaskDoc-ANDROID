package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.FileVO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DecisionItemCRUD {
    @GET("/decisionitem/{dscode}")
    Call<List<DecisionItemVO>> getItemList(@Path("dscode") int dscode);

    @POST("/decisionitem")
    Call<List<DecisionItemVO>> insertItem(List<DecisionItemVO> vos);

    @PUT("decisionitem")
    Call<Integer> updateItem(DecisionItemVO vo);

    @DELETE("/decisionitem/{dsicode}") // 파일도 같이 삭제
    Call<Integer> deleteItem(@Path("dsicode") int dsicode);
}
