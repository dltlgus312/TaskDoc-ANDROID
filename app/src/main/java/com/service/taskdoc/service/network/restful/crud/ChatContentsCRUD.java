package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatContentsCRUD {

    @GET("/chatcontents/{crcode}")
    Call<List<ChatContentsVO>> getChatContentsList(@Path("crcode") int crcode);

    @POST("/chatcontents")
    Call<ChatContentsVO> createChatContentsRoom(@Body ChatContentsVO vo);

}
