package com.service.taskdoc.display.transitions.document;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.custom.custom.dialog.file.FileDownLoadServiceDialog;
import com.service.taskdoc.display.recycle.DocumentCycle;
import com.service.taskdoc.service.network.restful.service.DocumentService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyElements extends Fragment implements DocumentCycle.ClickListener {

    private LinearLayout visible;
    private FloatingActionButton add;
    private TextView banner;
    private RecyclerView recyclerView;


    private List<DocumentVO> list = new ArrayList<>();

    private DocumentCycle cycle;

    private DocumentService service;

    public MyElements() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_method_board, container, false);
        visible = view.findViewById(R.id.fragment_trans_method_visible);
        add = view.findViewById(R.id.fragment_trans_method_fab);
        banner = view.findViewById(R.id.fragment_trans_method_banner);

        visible.setVisibility(View.GONE);
        add.setVisibility(View.GONE);


        recyclerView = view.findViewById(R.id.fragment_trans_method_recycle);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);

        cycle = new DocumentCycle(list);
        cycle.setClickListener(this);
        recyclerView.setAdapter(cycle);

        service = new DocumentService(0);
        service.setDocumentList(list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        service.userList();
        service.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                recyclerView.scheduleLayoutAnimation();
                cycle.notifyDataSetChanged();
                if (cycle.getItemCount() > 0) banner.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void folderClick(Task task) {

    }

    @Override
    public void folderLongClick(Task task) {

    }

    @Override
    public void fileClick(DocumentVO vo) {
        new FileDownLoadServiceDialog(getContext(), vo);
    }

    @Override
    public void fileLongClick(DocumentVO vo) {

    }
}
