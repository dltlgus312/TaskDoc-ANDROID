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
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.activity.ChatingActivity;
import com.service.taskdoc.display.custom.custom.dialog.file.DialogDocParam;
import com.service.taskdoc.display.custom.custom.dialog.file.FileDownLoadServiceDialog;
import com.service.taskdoc.display.recycle.NavFileCycle;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nav_File extends Fragment {

    private TextView title;
    private LinearLayout titleLinear;
    private RecyclerView recyclerView;

    private NavFileCycle cycle;

    private List<DocumentVO> list;

    public Nav_File() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chat_room_nav_frame, container, false);

        titleLinear = view.findViewById(R.id.head_linear);
        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recycle);

        title.setText("업로드 파일");

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

        cycle.setOnClickListener(new NavFileCycle.OnClickListener() {
            @Override
            public void onClickItem(DocumentVO vo) {
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
        builder.setTitle("파일 목록");

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


    void clickItemView(DocumentVO vo){
        DialogDocParam.FileUpdateListener listener =
                new DialogDocParam.FileUpdateListener() {
                    @Override
                    public void update(DocumentVO vo) {
                        ((ChatingActivity)getActivity()).stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.DOCUMENT, vo);
                    }

                    @Override
                    public void insert(DocumentVO vo) {
                        ((ChatingActivity)getActivity()). stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.DOCUMENT, vo);

                        ChatContentsVO chatContentsVO = new ChatContentsVO();
                        chatContentsVO.setCrcode(((ChatingActivity)getActivity()).chatRoomVO.getCrcode());
                        chatContentsVO.setDmcode(vo.getDmcode());
                        chatContentsVO.setCcontents(vo.getDmtitle());
                        chatContentsVO.setUid(UserInfo.getUid());
                        ((ChatingActivity)getActivity()).chatContentsService.insert(chatContentsVO);
                    }
                };

        new FileDownLoadServiceDialog(getContext())
                .setVo(vo)
                .setPcode(((ChatingActivity)getActivity()).project.getPcode())
                .setCrmode(((ChatingActivity)getActivity()).chatRoomVO.getCrmode())
                .setPermision(((ChatingActivity)getActivity()).project.getPpermission())
                .setFileUpdateListener(listener)
                .showFileData();
    }




    /*
    * DataChange
    * */

    public void setList(List<DocumentVO> list){
        this.list = list;
        cycle = new NavFileCycle(list);
    }

    public void lastPosition(){
        recyclerView.scrollToPosition(cycle.getItemCount()-1);
    }


    public void notifyDataSetChanged(){
        cycle.notifyDataSetChanged();
        lastPosition();
    }
}
