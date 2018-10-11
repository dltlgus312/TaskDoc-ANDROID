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
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.display.activity.ChatingActivity;
import com.service.taskdoc.display.custom.custom.dialog.chat.FocusItemSelectDialog;
import com.service.taskdoc.display.recycle.NavFocusCycle;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nav_Focus extends Fragment {

    private TextView title;
    private LinearLayout titleLinear;
    private RecyclerView recyclerView;

    private List<ChatRoomVO> list;

    private NavFocusCycle cycle;

    public Nav_Focus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chat_room_nav_frame, container, false);

        titleLinear = view.findViewById(R.id.head_linear);
        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recycle);

        title.setText("기록된 채팅");

        titleLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToListClick();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(cycle);
        cycle.setOnClickListener(new NavFocusCycle.OnClickListener() {
            @Override
            public void onClickItem(ChatRoomVO vo) {
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
        builder.setTitle("회의록 목록");

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

    void clickItemView(ChatRoomVO vo){
        FocusItemSelectDialog.FocusEventListener listener
                = new FocusItemSelectDialog.FocusEventListener() {
            @Override
            public void event() {
                ((ChatingActivity)getActivity()).stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.CHATROOM, vo);
        }
        };

        new FocusItemSelectDialog(getContext(), vo)
                .setFocusEventListener(listener)
                .setProject( ((ChatingActivity)getActivity()).project)
                .showSelectDialog();
    }





    /*
     * DataChange
     * */

    public void setList(List<ChatRoomVO> list){
        this.list = list;
        cycle = new NavFocusCycle(list);
    }

    public void lastPosition(){
        recyclerView.scrollToPosition(cycle.getItemCount()-1);
    }

    public void notifyDataSetChanged(){
        cycle.notifyDataSetChanged();
        lastPosition();
    }

}
