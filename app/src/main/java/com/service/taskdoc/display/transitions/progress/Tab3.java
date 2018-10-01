package com.service.taskdoc.display.transitions.progress;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.recycle.UsersCycle;
import com.service.taskdoc.service.network.restful.service.ProjectJoinService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab3 extends Fragment implements UsersCycle.OnClickListener {

    private ProjectProgressActivity activity;

    private TextView banner;

    public List<UserInfos> userInfosList;

    private UsersCycle cycle;

    private ProjectJoinService projectJoinService;

    private final String EXILETITLE = "프로젝트 추방";
    private final String EXILEMESSAGE = "해당 회원과 관련된 자료는 모두 사라집니다. 정말 추방 하시겠습니까?";

    public Tab3() {
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


        fab.setVisibility(View.GONE);

        this.projectJoinService = activity.projectJoinService;

        cycle = new UsersCycle(userInfosList);
        cycle.setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recycle);
//        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        if(cycle.getItemCount() > 0){
            banner.setVisibility(View.GONE);
        }

        return view;
    }

    public void datachange(){
        if (cycle!=null)
            cycle.notifyDataSetChanged();
    }


    @Override
    public void onClick(CardView view, UserInfos userInfos) {

    }

    @Override
    public void onLongClick(CardView view, UserInfos userInfos) {
        if (activity.project.getPpermission().equals(Projects.OWNER)){

        }
    }

    public List<UserInfos> getUserInfosList() {
        return userInfosList;
    }

    public void setUserInfosList(List<UserInfos> userInfosList) {
        this.userInfosList = userInfosList;
    }
}
