package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.database.transfer.ProjectVO;
import com.service.taskdoc.database.transfer.UserInfoVO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatRoomCRUD {

    @GET("/chatroom/room/{crcode}")
    Call<List<ChatRoomVO>> getChatRoomList(@Path("crcode") int crcode);

    @GET("/chatroom/task/{tcode}")
    Call<List<ChatRoomVO>> getChatTaskList(@Path("tcode") int tcode);

    @GET("/chatroom/{crcode}")
    Call<ChatRoomVO> getChatRoom(@Path("crcode") int crcode);

    @POST("/chatroom") // KEY = "project" : ProjectVO, "userInfo" : UserInfoVO, "chatRoom" : ChatRoomVO
    Call<Integer> createChatRoom(@Body Map<String, Object> map);

    @POST("/chatroom/multi") // KEY = "project" : ProjectVO, "userInfo" : List<UserInfoVO>, "chatRoom" : ChatRoomVO
    Call<Integer> createChatRoomMulti(@Body Map<String, Object> map);

    @PUT("/chatroom")
    Call<Integer> updateChatRoom(@Body ChatRoomVO vo);

    @DELETE("/chatroom/{crcode}")
    Call<Integer> deleteChatRoom(@Path("crcode") int crcode);
}
