package com.service.taskdoc.display.transitions.chatroom;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.activity.ChatingActivity;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionItemSelectDialog;
import com.service.taskdoc.display.recycle.NavDecisionCycle;
import com.service.taskdoc.display.recycle.NavFileCycle;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nav_Decision extends Fragment {

    private TextView title;
    private LinearLayout titleLinear;
    private RecyclerView recyclerView;

    private List<DecisionVO> list;

    private NavDecisionCycle cycle;

    public Nav_Decision() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chat_room_nav_frame, container, false);

        titleLinear = view.findViewById(R.id.head_linear);
        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setNestedScrollingEnabled(false);

        title.setText("진행된 투표");

        titleLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToListClick();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);
        cycle.setOnClickListener(new NavDecisionCycle.OnClickListener() {
            @Override
            public void onClickItem(DecisionVO vo) {
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
        builder.setTitle("투표 목록");

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

    void clickItemView(DecisionVO vo){
        DecisionItemSelectDialog.DecisionEventListener listener
                = new DecisionItemSelectDialog.DecisionEventListener() {
            @Override
            public void update() {
                ((ChatingActivity)getActivity()).stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.DECISION, vo);
            }
        };
        new DecisionItemSelectDialog(getContext(), vo)
                .setPcode(((ChatingActivity)getActivity()).project.getPcode())
                .setCrmode(((ChatingActivity)getActivity()).chatRoomVO.getCrmode())
                .setPermision(((ChatingActivity)getActivity()).project.getPpermission())
                .setDecisionEventListener(listener)
                .showList();
    }



    /*
     * DataChange
     * */

    public void setList(List<DecisionVO> list){
        this.list = list;
        cycle = new NavDecisionCycle(list);
    }

    public void lastPosition(){
        recyclerView.scrollToPosition(cycle.getItemCount()-1);
    }

    public void notifyDataSetChanged(){
        cycle.notifyDataSetChanged();
        lastPosition();
    }

}
