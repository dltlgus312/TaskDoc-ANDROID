package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.ProjectVO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProjectCRUD {
    @POST("/project")
    Call<Integer> setProject(@Body Map<String, Object> data);

    @PUT("/project")
    Call<Integer> putProject(@Body ProjectVO projectVO);

    @DELETE("/project/{pcode}")
    Call<Integer> delProject(@Path("pcode") int pcode);
}
