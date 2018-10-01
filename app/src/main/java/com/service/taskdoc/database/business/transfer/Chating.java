package com.service.taskdoc.database.business.transfer;

import com.service.taskdoc.database.transfer.ChatContentsVO;

public class Chating {

    private UserInfos userInfos;
    private ChatContentsVO chatContentsVO;

    public UserInfos getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(UserInfos userInfos) {
        this.userInfos = userInfos;
    }

    public ChatContentsVO getChatContentsVO() {
        return chatContentsVO;
    }

    public void setChatContentsVO(ChatContentsVO chatContentsVO) {
        this.chatContentsVO = chatContentsVO;
    }
}
