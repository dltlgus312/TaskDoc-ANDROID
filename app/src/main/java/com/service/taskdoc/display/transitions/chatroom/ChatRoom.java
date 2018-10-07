package com.service.taskdoc.display.transitions.chatroom;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.ProjectVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.display.activity.ChatingActivity;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.recycle.ChatRoomCycle;
import com.service.taskdoc.display.recycle.UsersCycle;
import com.service.taskdoc.service.network.restful.service.ChatRoomJoinService;
import com.service.taskdoc.service.network.restful.service.ChatRoomService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoom extends Fragment implements ChatRoomCycle.OnClickListener {

    private TextView banner;

    private ChatRoomCycle cycle;

    private List<ChatRoomInfo> list;

    private ProjectProgressActivity activity;

    private ChatRoomService chatRoomService;
    private ChatRoomJoinService chatRoomJoinService;


    public ChatRoom() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);

        banner = view.findViewById(R.id.banner);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChatingRoom();
            }
        });

        activity = (ProjectProgressActivity) getActivity();

        this.cycle = new ChatRoomCycle(list);
        cycle.setOnClickListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        if (cycle.getItemCount() > 0){
            banner.setVisibility(View.GONE);
        }

        chatRoomService = new ChatRoomService(0);

        chatRoomJoinService = new ChatRoomJoinService();
        chatRoomJoinService.setChatRoomInfoList(list);

        return view;
    }

    @Override
    public void onClick(ChatRoomInfo chatRoomInfo) {

        chatRoomInfo.setAlarm(0);
        chatRoomInfo.setAlarm(false);
        cycle.notifyDataSetChanged();

        Intent intent = new Intent(getContext(), ChatingActivity.class);
        intent.putExtra("project", new Gson().toJson(((ProjectProgressActivity)getActivity()).project));
        intent.putExtra("chatRoomVO", new Gson().toJson(chatRoomInfo.getChatRoomVO()));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        for (ChatRoomInfo info : list){
            info.setAlarm(true);
        }
        super.onResume();
    }

    @Override
    public void onLongClick() {

    }

    public void addChatingRoom(){

        List<UserInfos> addList = new ArrayList<>();

        // 내정보 상시 추가
        UserInfos my = new UserInfos();
        my.setId(UserInfo.getUid());
        addList.add(my);

        List<UserInfos> lists = new ArrayList<>();
        lists.addAll(((ProjectProgressActivity)getActivity()).userInfoList);

        Iterator<UserInfos> it = lists.iterator();
        while (it.hasNext()) {
            UserInfos vo = it.next();
            if (vo.getInvite() == 0) {
                it.remove();
                continue;
            }

            if (vo.getId().equals(UserInfo.getUid())) {
                it.remove();
                continue;
            }
        }

        UsersCycle cycle = new UsersCycle(lists);

        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("새로운 방 생성");
        builder.setView(recyclerView);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createChatRoom(addList);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.show();

        cycle.setOnClickListener(new UsersCycle.OnClickListener() {
            @Override
            public void onClick(CardView view, UserInfos userInfos) {
                if (!addList.contains(userInfos)){
                    view.setCardBackgroundColor(Color.GRAY);
                    addList.add(userInfos);
                }else {
                    view.setCardBackgroundColor(Color.WHITE);
                    addList.remove(userInfos);
                }
            }

            @Override
            public void onLongClick(CardView view, UserInfos userInfos) {

            }
        });
    }

    public void createChatRoom(List<UserInfos> addList){
        ChatRoomVO chatRoomVO = new ChatRoomVO();
        chatRoomVO.setCrmode(2);

        ProjectVO projectVO = new ProjectVO();
        projectVO.setPcode(((ProjectProgressActivity)getActivity()).project.getPcode());

        List<UserInfoVO> userList = new ArrayList<>();

        for (UserInfos user : addList){
            UserInfoVO vo = new UserInfoVO();
            vo.setUid(user.getId());
            userList.add(vo);
        }

        chatRoomService.insertMulti(projectVO, userList, chatRoomVO);
        chatRoomService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                ((ProjectProgressActivity) getActivity()).stompBuilder.sendMessage(
                        StompBuilder.INSERT, StompBuilder.CHATROOM, ""
                );
            }
        });
    }


    public void datachange(){
        cycle.notifyDataSetChanged();
    }

    public void addAlarm(String object){
        ChatContentsVO vo = new Gson().fromJson(object, ChatContentsVO.class);
        for (ChatRoomInfo info : list){
            if (info.getChatRoomVO().getCrcode() == vo.getCrcode()){
                info.setAlarm(info.getAlarm()+1);
            }
        }
        cycle.notifyDataSetChanged();
    }


    public List<ChatRoomInfo> getList() {
        return list;
    }

    public void setList(List<ChatRoomInfo> list) {
        this.list = list;
    }
}
