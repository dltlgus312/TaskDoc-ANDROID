package com.service.taskdoc.display.transitions.project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.transfer.ProjectVO;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.recycle.ProjectCycle;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.ProjectJoinService;
import com.service.taskdoc.service.network.restful.service.ProjectService;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class Project extends Fragment implements ProjectCycle.Listener, NetworkSuccessWork {

    private final String TAG = "TransProject";

    private final String BANNER_INVITE = "프로젝트 참가에 동의 하시겠습니까?";
    private final String BANNER_OWNER_DELETE = "삭제된 프로젝트는 복구가 불가능 합니다. 정말 삭제 하시겠습니까?";
    private final String BANNER_DELETE = "프로젝트에 관련한 회원의 기록은 모두 삭제(자료 포함) 됩니다. 정말 나가시겠습니까?";

    private final String DIALOG_TITLE_INVITE = "초대";
    private final String DIALOG_TITLE_OWNER = "프로젝트 삭제";
    private final String DIALOG_TITLE_DELETE = "프로젝트 나가기";
    private final String DIALOG_YES = "예";
    private final String DIALOG_NO = "아니요";
    private final String DIALOG_CONFIRM = "확인";
    private final String DIALOG_CANCLE = "취소";

    private View view;
    private TextView banner;
    private ProjectCycle projectCycle;
    private com.service.taskdoc.database.business.transfer.Project ownerClickItemParam;
    private RecyclerView recyclerView;

    private List<StompBuilder> stompBuilders;

    /* 네트워크 객체 */
    private ProjectJoinService projectJoinService;
    private ProjectService projectService;

    public Project() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.fragment_project, null);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                projectEditDialog();
            }
        });

        banner = view.findViewById(R.id.banner);

        recyclerView = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Map<String, List<com.service.taskdoc.database.business.transfer.Project>> projectMap = Projects.getAllProjectList();
        projectCycle = new ProjectCycle(projectMap, getContext());
        projectCycle.setClick(this);
        recyclerView.setAdapter(projectCycle);

        projectJoinService = new ProjectJoinService();
        projectService = new ProjectService();

        projectService.work(this);
        projectJoinService.work(this);

        stompBuilders = new ArrayList<>();

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_project_owner, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_project_owner_modify:
                projectEditDialog(ownerClickItemParam);
                return true;
            case R.id.menu_project_owner_delete:
                projectDeleteDialog(ownerClickItemParam);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {

        for (StompBuilder t : stompBuilders)
            t.disconnect();

        stompBuilders.clear();

        projectJoinService.selectProject();
        super.onResume();
    }

    @Override
    public void work(Object... objects) {
        projectCycle.notifyDataSetChanged();

        if(0 != projectCycle.getItemCount()){
            banner.setVisibility(View.GONE);
        }

        if (stompBuilders.size() == 0){
            for (com.service.taskdoc.database.business.transfer.Project p : Projects.getProjectInviteList()){
                StompBuilder s = new StompBuilder(p.getPcode());
                s.connect();
                stompBuilders.add(s);
            }
        }
    }

    /* CLICK EVENT */
    @Override
    public void setOnClick(com.service.taskdoc.database.business.transfer.Project project, View view) {
        if (project.getPinvite() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(DIALOG_TITLE_INVITE);
            builder.setMessage("\"" + project.getPtitle() + "\" " + BANNER_INVITE);
            builder.setPositiveButton(DIALOG_YES,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            project.setPinvite(1);
                            projectJoinService.update(project);

                            for (StompBuilder s : stompBuilders){
                                if (project.getPcode() == s.getPcode()){
                                    s.sendMessage(StompBuilder.UPDATE, StompBuilder.PROJECTJOIN, "");
                                }
                            }
                        }
                    });
            builder.setNegativeButton(DIALOG_NO,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        } else {
            projectConnect(project);
        }
    }

    @Override
    public void setLongClick(com.service.taskdoc.database.business.transfer.Project project, View view) {
        if (project.getPpermission().equals(Projects.OWNER)) {
            registerForContextMenu(view);
            this.ownerClickItemParam = project;
        } else {
            projectDeleteDialog(project);
        }
    }

    public void projectDeleteDialog(com.service.taskdoc.database.business.transfer.Project project) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (project.getPpermission().equals(Projects.OWNER)) {
            builder.setTitle(DIALOG_TITLE_OWNER);
            builder.setMessage("\"" + project.getPtitle() + "\" " + BANNER_OWNER_DELETE);
        } else {
            builder.setTitle(DIALOG_TITLE_DELETE);
            builder.setMessage("\"" + project.getPtitle() + "\" " + BANNER_DELETE);
        }

        builder.setPositiveButton(DIALOG_YES,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (project.getPpermission().equals(Projects.OWNER)){
                            projectService.delete(project);
                        }else{
                            projectJoinService.delete(project);
                        }
                    }
                });
        builder.setNegativeButton(DIALOG_NO,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public void projectEditDialog(com.service.taskdoc.database.business.transfer.Project... project) {
        int isEmpty = 0;
        if (project.length == 0)
            isEmpty = 1;

        View view = getLayoutInflater().inflate(R.layout.custom_form_project_create, null);
        LinearLayout date = view.findViewById(R.id.date);
        TextView banner = view.findViewById(R.id.date_banner);
        EditText title = view.findViewById(R.id.custom_form_project_create_title);
        EditText subTitle = view.findViewById(R.id.custom_form_project_create_sub_title);
        Button sDate = view.findViewById(R.id.custom_form_project_create_start_date);
        Button eDate = view.findViewById(R.id.custom_form_project_create_end_date);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(sDate, eDate, banner);
            }
        });

        if (isEmpty == 0) {
            title.setText(project[0].getPtitle());
            subTitle.setText(project[0].getPsubtitle());
            sDate.setText(project[0].getPsdate());
            eDate.setText(project[0].getPedate());
            banner.setText("~");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        int finalIsEmpty = isEmpty;
        builder.setPositiveButton(DIALOG_CONFIRM,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ProjectVO projectVO = new ProjectVO();
                        projectVO.setPtitle(title.getText().toString());
                        projectVO.setPsubtitle(subTitle.getText().toString());
                        projectVO.setPsdate(sDate.getText().toString());
                        projectVO.setPedate(eDate.getText().toString());
                        if (finalIsEmpty == 0) {
                            projectVO.setPcode(project[0].getPcode());
                            projectService.update(project[0], projectVO);
                        } else {
                            projectService.create(projectVO);
                        }
                    }
                });
        builder.setNegativeButton(DIALOG_CANCLE,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public void projectConnect(com.service.taskdoc.database.business.transfer.Project project) {
        Intent intent = new Intent(getContext(), ProjectProgressActivity.class);
        intent.putExtra("project", new Gson().toJson(project));
        getContext().startActivity(intent);
    }

    public void datePicker(Button sDate, Button eDate, TextView banner){
        List<Calendar> selectedDates = null;

        OnSelectDateListener listener = new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendar) {
                Calendar start = calendar.get(0);
                Calendar end = calendar.get(calendar.size()-1);

                int sY = start.get(Calendar.YEAR);
                int sM = start.get(Calendar.MONTH)+1;
                int sD = start.get(Calendar.DATE);

                int eY = end.get(Calendar.YEAR);
                int eM = end.get(Calendar.MONTH)+1;
                int eD = end.get(Calendar.DATE);
                sDate.setText(sY+"-"+sM+"-"+sD);
                eDate.setText(eY+"-"+eM+"-"+eD);
                banner.setText("~");
            }
        };

        DatePickerBuilder builder = new DatePickerBuilder(getContext(), listener)
                .pickerType(CalendarView.RANGE_PICKER).headerColor(R.color.colorPrimary);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    public class ProjectLoad extends AsyncTask<String, Integer, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(getContext(), R.style.DialogTheme);

        @Override
        protected Void doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.setCancelable(false);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            asyncDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
