package com.service.taskdoc.display.transitions.chatroom;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.UserInfoVO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nav extends Fragment {

    private Nav_File navFile = new Nav_File();
    private Nav_Focus navFocus = new Nav_Focus();
    private Nav_Decision navDecision =  new Nav_Decision();
    private Nav_User navUser = new Nav_User();

    public Nav() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chat_room_nav, container, false);
        LinearLayout quit = view.findViewById(R.id.quit);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        getChildFragmentManager().beginTransaction().replace(R.id.file, navFile).addToBackStack(null).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.focus, navFocus).addToBackStack(null).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.decision, navDecision).addToBackStack(null).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.user, navUser).addToBackStack(null).commit();

        return view;
    }

    public void setDocumentList(List<DocumentVO> list){
        navFile.setList(list);
    }

    public void setDecisionList(List<DecisionVO> list){
        navDecision.setList(list);
    }

    public void setUserList(List<UserInfos> list){
        navUser.setList(list);
    }

    public void setFocusList(List<ChatRoomVO> list){
        navFocus.setList(list);
    }

    public void notifyDataSetChanged(){
        navFile.notifyDataSetChanged();
        navDecision.notifyDataSetChanged();
        navUser.notifyDataSetChanged();
        navFocus.notifyDataSetChanged();
    }
}
