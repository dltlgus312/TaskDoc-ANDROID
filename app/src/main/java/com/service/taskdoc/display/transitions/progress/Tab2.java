package com.service.taskdoc.display.transitions.progress;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.transfer.NoticeVO;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.recycle.NoticeCycle;
import com.service.taskdoc.service.network.restful.service.NoticeService;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment implements NoticeCycle.OnClickListener {

    private ProjectProgressActivity activity;

    private TextView banner;

    private NoticeVO ownerRefNotice;

    public List<NoticeVO> noticeVOList;

    private NoticeCycle cycle;
    ;
    private NoticeService service;

    public Tab2() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_project, null);

        activity = (ProjectProgressActivity) getActivity();
        activity.setOnBackPressedListener(null);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        banner = view.findViewById(R.id.banner);

        if (activity.project.getPpermission().equals(Projects.MEMBER)) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNotice();
                }
            });
        }

        service = activity.noticeService;

        cycle = new NoticeCycle(noticeVOList);
        cycle.setOnClickListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        if(cycle.getItemCount() > 0){
            banner.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(ownerRefNotice.getNtitle());
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_project_owner, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_project_owner_modify:
                addNotice(ownerRefNotice);
                return true;
            case R.id.menu_project_owner_delete:
                service.delete(ownerRefNotice.getNcode());
                service.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        noticeVOList.remove(ownerRefNotice);
                        cycle.notifyDataSetChanged();
                        if (cycle.getItemCount() != 0) {
                            banner.setVisibility(View.GONE);
                        }
                    }
                });

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addNotice(NoticeVO ... vo){

        View view = getLayoutInflater().inflate(R.layout.custom_form_title_content, null);
        EditText title = view.findViewById(R.id.title);
        EditText content = view.findViewById(R.id.content);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NoticeVO v = new NoticeVO();
                v.setPcode(activity.project.getPcode());
                v.setNtitle(title.getText().toString());
                v.setNcontents(content.getText().toString());

                if(vo.length > 0){
                    v.setPcode(vo[0].getPcode());
                    v.setNcode(vo[0].getNcode());
                    service.update(v);
                    service.work(new NetworkSuccessWork() {
                        @Override
                        public void work(Object... objects) {
                            vo[0].setNcontents(v.getNcontents());
                            vo[0].setNtitle(v.getNtitle());
                            cycle.notifyDataSetChanged();
                        }
                    });
                } else {
                    service.create(v);
                    service.work(new NetworkSuccessWork() {
                        @Override
                        public void work(Object... objects) {
                            noticeVOList.add(0, (NoticeVO) objects[0]);
                            cycle.notifyDataSetChanged();
                            if (cycle.getItemCount() != 0) {
                                banner.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        builder.show();

        if(vo.length > 0){
            service.view(vo[0].getNcode());
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    title.setText(((NoticeVO)objects[0]).getNtitle());
                    content.setText(((NoticeVO)objects[0]).getNcontents());
                }
            });
        }
    }

    @Override
    public void onClick(NoticeVO vo) {
        service.view(vo.getNcode());
        service.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                NoticeVO v = (NoticeVO) objects[0];
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(v.getNtitle());
                builder.setMessage(v.getNcontents());
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void onLongClick(View view, NoticeVO vo) {
        if(activity.project.getPpermission().equals(Projects.OWNER)){
            this.ownerRefNotice = vo;
            registerForContextMenu(view);
        }
    }

    public List<NoticeVO> getNoticeVOList() {
        return noticeVOList;
    }

    public void setNoticeVOList(List<NoticeVO> noticeVOList) {
        this.noticeVOList = noticeVOList;
    }
}
