package com.service.taskdoc.database.business;

import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatRoomJoinVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.UserInfoVO;

import java.util.List;

public class ChatRoomInfo {
    private List<UserInfoVO> userList;
    private ChatRoomVO chatRoomVO;

    private int alarm;

    public List<UserInfoVO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfoVO> userList) {
        this.userList = userList;
    }

    public ChatRoomVO getChatRoomVO() {
        return chatRoomVO;
    }

    public void setChatRoomVO(ChatRoomVO chatRoomVO) {
        this.chatRoomVO = chatRoomVO;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }
}
