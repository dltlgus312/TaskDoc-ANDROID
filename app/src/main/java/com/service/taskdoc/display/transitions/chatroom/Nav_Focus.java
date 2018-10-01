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
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.display.recycle.NavFocusCycle;

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

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(cycle);

        return view;
    }

    public void setList(List<ChatRoomVO> list){
        this.list = list;
        cycle = new NavFocusCycle(list);
    }

    public void notifyDataSetChanged(){
        cycle.notifyDataSetChanged();
    }

}
