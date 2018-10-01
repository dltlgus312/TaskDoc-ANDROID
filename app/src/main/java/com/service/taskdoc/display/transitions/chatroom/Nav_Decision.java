package com.service.taskdoc.display.transitions.chatroom;


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
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.display.recycle.NavDecisionCycle;

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

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        return view;
    }

    public void setList(List<DecisionVO> list){
        this.list = list;

        cycle = new NavDecisionCycle(list);
    }

    public void notifyDataSetChanged(){
        cycle.notifyDataSetChanged();
    }

}
