package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.ProjectJoinVO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProjectJoinCRUD {

    @GET("/projectjoin/{uid}")
    Call<Map<String, Object>> getProjects(@Path("uid") String uid);

    @GET("/projectjoin/collaboration/{pcode}")
    Call<Map<String, Object>> getUsers(@Path("pcode") int pcode);

    @POST("/projectjoin")
    Call<Integer> setProjectJoin(@Body ProjectJoinVO projectJoinVO);

    @PUT("/projectjoin")
    Call<Integer> putProjectJoin(@Body ProjectJoinVO projectJoinVO);

    @HTTP(method = "DELETE", path = "/projectjoin", hasBody = true)
    Call<Integer> delProjectJoin(@Body ProjectJoinVO projectJoinVO);
}
