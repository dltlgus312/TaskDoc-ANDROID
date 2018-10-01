package com.service.taskdoc.service.network.restful.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.service.taskdoc.database.business.transfer.Chating;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.network.restful.crud.ChatContentsCRUD;
import com.service.taskdoc.service.network.restful.crud.MemoCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatContentsService {

    private final String TAG = "ChatContents";

    private List<Chating> chatContentsList;
    private List<UserInfos> userList;


    private NetworkSuccessWork networkSuccessWork;

    private int crcode;
    private ChatContentsCRUD service;

    public ChatContentsService(int crcode) {
        this.crcode = crcode;
        service = RequestBuilder.createService(ChatContentsCRUD.class);
    }

    public void setUserList(List<UserInfos> userList) {
        this.userList = userList;
    }

    public void setChatContentsList(List<Chating> chatContentsList) {
        this.chatContentsList = chatContentsList;
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public void list() {
        Call<List<ChatContentsVO>> request = service.getChatContentsList(crcode);
        request.enqueue(new Callback<List<ChatContentsVO>>() {
            @Override
            public void onResponse(Call<List<ChatContentsVO>> call, Response<List<ChatContentsVO>> response) {
                List<ChatContentsVO> result = response.body();

                if (response.body() != null && response.body().size() != 0) {
                    List<ChatContentsVO> vos = response.body();
                    for (ChatContentsVO vo : vos) {
                        Chating v = new Chating();
                        v.setChatContentsVO(vo);
                        for (UserInfos u : userList) {
                            if (vo.getUid().equals(u.getId())) {
                                v.setUserInfos(u);
                            }
                        }
                        chatContentsList.add(v);
                    }
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<ChatContentsVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(ChatContentsVO vo) {
        Call<ChatContentsVO> request = service.createChatContentsRoom(vo);
        request.enqueue(new Callback<ChatContentsVO>() {
            @Override
            public void onResponse(Call<ChatContentsVO> call, Response<ChatContentsVO> response) {
                if (response.body() != null) {
                    Chating chating = new Chating();
                    chating.setChatContentsVO(response.body());
                    for (UserInfos v : userList){
                        if(v.getId().equals(response.body().getUid())){
                            chating.setUserInfos(v);
                        }
                    }
                    chatContentsList.add(chating);
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<ChatContentsVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
