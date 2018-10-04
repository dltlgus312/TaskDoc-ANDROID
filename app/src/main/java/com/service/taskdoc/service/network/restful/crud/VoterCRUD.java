package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.VoterVO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VoterCRUD {
    @GET("/voter/{dsicode}")
    Call<List<VoterVO>> getList(@Path("dsicode") int dsicode);

    @POST("/voter")
    Call<Integer> insert(VoterVO vo);

    @PUT("voter") // KEY = "oldVo" : VoterVO, "newVo" : VoterVO )
    Call<Integer> update(Map<String, VoterVO> vo);
}
