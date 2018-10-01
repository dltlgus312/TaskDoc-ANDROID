package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.ChatRoomJoinVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.MemoVO;
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

public interface ChatRoomJoinCRUD {

    @POST("/chatroomjoin/room")
    Call<Map<String, Object>> getChatRoomJoinRoomList(@Body ChatRoomJoinVO chatRoomJoinVO);

    @POST("/chatroomjoin/user")
    Call<List<UserInfoVO>> getChatRoomJoinUserList(@Body ChatRoomJoinVO chatRoomJoinVO);

    @POST("/chatroomjoin")
    Call<Integer> createChatRoomJoin(@Body ChatRoomJoinVO chatRoomJoinVO);

    @POST("/chatroomjoin/multiple")
    Call<List<ChatRoomJoinVO>> createChatRoomJoinList(@Body List<ChatRoomJoinVO> chatRoomJoinVOList);

    @DELETE("/chatroomjoin")
    Call<Integer> deleteChatRoomJoin(@Body ChatRoomJoinVO chatRoomJoinVO);
}
