package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.ProjectVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.network.restful.crud.ChatRoomCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomService {

    private final String TAG = "ChatRoom";

    private NetworkSuccessWork networkSuccessWork;
    private ChatRoomCRUD service;

    private List<ChatRoomVO> chatRoomList;
    private int crcode;

    public ChatRoomService(int crcode) {
        this.crcode = crcode;
        service = RequestBuilder.createService(ChatRoomCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public void setChatRoomList(List<ChatRoomVO> chatRoomList){
        this.chatRoomList = chatRoomList;
    }

    public void roomList() {
        Call<List<ChatRoomVO>> request = service.getChatRoomList(crcode);
        request.enqueue(new Callback<List<ChatRoomVO>>() {
            @Override
            public void onResponse(Call<List<ChatRoomVO>> call, Response<List<ChatRoomVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    chatRoomList.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoomVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void roomView(int crcode) {
        Call<ChatRoomVO> request = service.getChatRoom(crcode);
        request.enqueue(new Callback<ChatRoomVO>() {
            @Override
            public void onResponse(Call<ChatRoomVO> call, Response<ChatRoomVO> response) {
                if (response.body() != null) {
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatRoomVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void taskList(int tcode) {
        Call<List<ChatRoomVO>> request = service.getChatTaskList(tcode);
        request.enqueue(new Callback<List<ChatRoomVO>>() {
            @Override
            public void onResponse(Call<List<ChatRoomVO>> call, Response<List<ChatRoomVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoomVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    // KEY = "project" : ProjectVO, "userInfo" : List<UserInfoVO>, "chatRoom" : ChatRoomVO
    public void insert(ProjectVO projectVO, UserInfoVO userInfoVO, ChatRoomVO chatRoomVO) {

        Map<String, Object> map = new HashMap<>();

        map.put("project", projectVO);
        map.put("userInfo", userInfoVO);
        map.put("chatRoom", chatRoomVO);

        Call<Integer> request = service.createChatRoom(map);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null && response.body() != -1) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    // KEY = "project" : ProjectVO, "userInfo" : List<UserInfoVO>, "chatRoom" : ChatRoomVO
    public void insertMulti(ProjectVO projectVO, List<UserInfoVO> userInfoVO, ChatRoomVO chatRoomVO) {

        Map<String, Object> map = new HashMap<>();

        map.put("project", projectVO);
        map.put("userInfo", userInfoVO);
        map.put("chatRoom", chatRoomVO);

        Call<Integer> request = service.createChatRoomMulti(map);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null && response.body() != -1) {
                    chatRoomVO.setCrcode(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(ChatRoomVO vo) {
        Call<Integer> request = service.updateChatRoom(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null && response.body() == 1) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int crcode) {
        Call<Integer> request = service.deleteChatRoom(crcode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null && response.body() == 1) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
