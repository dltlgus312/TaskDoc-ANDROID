package com.service.taskdoc.display.transitions.progress;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.transitions.chatroom.ChatRoom;
import com.service.taskdoc.display.transitions.project.MyInfo;
import com.service.taskdoc.display.transitions.document.Element;
import com.service.taskdoc.display.transitions.task.Task;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1 extends Fragment implements TabLayout.OnTabSelectedListener {

    private ChatRoom chatRoomView = new ChatRoom();
    private Task taskView = Task.newInstance(Tasks.PUBLIC);
    private MyInfo myInfoView = new MyInfo();
    private Element documentView = new Element();

    public List<ChatRoomInfo> chatRoomInfosList;
    public List<DocumentVO> documentList;
    public Tasks tasks;

    public Tab1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_progress_task, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(this);

        getChildFragmentManager().beginTransaction().replace(R.id.fragment, chatRoomView).addToBackStack(null).commit();

        return view;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();

        switch (position){
            case 0 :
                getChildFragmentManager().beginTransaction().replace(R.id.fragment, chatRoomView).addToBackStack(null).commit();
                break;
            case 1:
                getChildFragmentManager().beginTransaction().replace(R.id.fragment, taskView).addToBackStack(null).commit();
                break;
            case 2:
                getChildFragmentManager().beginTransaction().replace(R.id.fragment, documentView).addToBackStack(null).commit();
                break;
            case 3:
                getChildFragmentManager().beginTransaction().replace(R.id.fragment, myInfoView).addToBackStack(null).commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public List<ChatRoomInfo> getChatRoomInfosList() {
        return chatRoomInfosList;
    }

    public void setChatRoomInfosList(List<ChatRoomInfo> chatRoomInfosList) {
        this.chatRoomInfosList = chatRoomInfosList;
        chatRoomView.setList(this.chatRoomInfosList);
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
        taskView.setTasks(this.tasks);
        documentView.setTasks(this.tasks);
    }

    public List<DocumentVO> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<DocumentVO> documentList) {
        this.documentList = documentList;
        documentView.setDocumentList(this.documentList);
    }
}
