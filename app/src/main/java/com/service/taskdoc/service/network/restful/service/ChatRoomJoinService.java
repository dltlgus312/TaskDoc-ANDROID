package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatRoomJoinVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.database.transfer.ProjectJoinVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.network.restful.crud.ChatRoomJoinCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomJoinService {

    private final String TAG = "ChatRoomJoin";

    private NetworkSuccessWork networkSuccessWork;
    private ChatRoomJoinCRUD service;

    private List<ChatRoomInfo> chatRoomInfoList;
    private List<UserInfos> userInfosList;

    public ChatRoomJoinService() {
        service = RequestBuilder.createService(ChatRoomJoinCRUD.class);
    }

    public List<ChatRoomInfo> getChatRoomInfoList() {
        return chatRoomInfoList;
    }

    public void setChatRoomInfoList(List<ChatRoomInfo> chatRoomInfoList) {
        this.chatRoomInfoList = chatRoomInfoList;
    }

    public List<UserInfos> getUserInfosList() {
        return userInfosList;
    }

    public void setUserInfosList(List<UserInfos> userInfosList) {
        this.userInfosList = userInfosList;
    }


    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }



    public void roomList(ChatRoomJoinVO chatRoomJoinVO) {
        Call<Map<String, Object>> request = service.getChatRoomJoinRoomList(chatRoomJoinVO);
        request.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.body() != null) {
                    Map<String, Object> map = (Map<String, Object>) response.body();

                    Type chatRoomType = new TypeToken<ArrayList<ChatRoomVO>>(){}.getType();
                    Type userInfoType = new TypeToken<ArrayList<ArrayList<UserInfoVO>>>(){}.getType();

                    Gson gson = new Gson();

                    List<ChatRoomVO> chatRooms = gson.fromJson(gson.toJson(map.get("chatRoomList")), chatRoomType);
                    List<List<UserInfoVO>> userInfos = gson.fromJson(gson.toJson(map.get("userInfoList")), userInfoType);

                    for(int i=0;i<chatRooms.size();i++){
                        ChatRoomInfo chatRoomInfo = new ChatRoomInfo();

                        chatRoomInfo.setChatRoomVO(chatRooms.get(i));
                        chatRoomInfo.setUserList(userInfos.get(i));

                        chatRoomInfoList.add(chatRoomInfo);
                    }
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void userList(ChatRoomJoinVO chatRoomJoinVO) {
        Call<List<UserInfoVO>> request = service.getChatRoomJoinUserList(chatRoomJoinVO);
        request.enqueue(new Callback<List<UserInfoVO>>() {
            @Override
            public void onResponse(Call<List<UserInfoVO>> call, Response<List<UserInfoVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    List<UserInfoVO> list = response.body();

                    for (UserInfoVO vo : list){
                        UserInfos v = new UserInfos();

                        v.setId(vo.getUid());
                        v.setName(vo.getUname());
                        v.setPhone(vo.getUphone());
                        v.setState(vo.getUstate());

                        userInfosList.add(v);
                    }

                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<UserInfoVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insertList(List<ChatRoomJoinVO> vos) {
        Call<List<ChatRoomJoinVO>> request = service.createChatRoomJoinList(vos);
        request.enqueue(new Callback<List<ChatRoomJoinVO>>() {
            @Override
            public void onResponse(Call<List<ChatRoomJoinVO>> call, Response<List<ChatRoomJoinVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoomJoinVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(ChatRoomJoinVO vo, UserInfos userInfos) {
        Call<Integer> request = service.createChatRoomJoin(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null && response.body() != -1) {
                    userInfosList.add(userInfos);
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(ChatRoomJoinVO vo) {
        Call<Integer> request = service.deleteChatRoomJoin(vo);
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
