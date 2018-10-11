package com.service.taskdoc.display.transitions.chatroom;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.display.recycle.NavUserCycle;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nav_User extends Fragment {

    private TextView title;
    private LinearLayout titleLinear;
    private RecyclerView recyclerView;

    private List<UserInfos> list;

    private NavUserCycle cycle;

    public Nav_User() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chat_room_nav_frame, container, false);

        titleLinear = view.findViewById(R.id.head_linear);
        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recycle);

        title.setText("참여중 유저");

        titleLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToListClick();
            }
        });

        cycle = new NavUserCycle(list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(cycle);
        cycle.setOnClickListener(new NavUserCycle.OnClickListener() {
            @Override
            public void onClickItem(UserInfos vo) {
                clickItemView(vo);
            }
        });
        lastPosition();

        return view;
    }



    /*
     * Click Event
     * */

    void goToListClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("참여 인원 목록");

        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(cycle);

        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setView(recyclerView);
        builder.show();
    }

    void clickItemView(UserInfos vo){

    }







    /*
     * DataChange
     * */

    public void setList(List<UserInfos> list){
        this.list = list;
    }

    public void lastPosition(){
        recyclerView.scrollToPosition(cycle.getItemCount()-1);
    }

    public void notifyDataSetChanged(){
        cycle.notifyDataSetChanged();
        lastPosition();
    }

}
