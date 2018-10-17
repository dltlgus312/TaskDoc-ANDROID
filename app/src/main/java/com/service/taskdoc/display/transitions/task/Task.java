package com.service.taskdoc.display.transitions.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;
import com.service.taskdoc.display.activity.GanttChartActivity;
import com.service.taskdoc.display.activity.ProjectActivity;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.custom.custom.dialog.task.CreateTaskDialog;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.StompBuilder;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.listener.OnBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class Task extends Fragment implements OnBackPressedListener {

    public String type;

    private static final String TYPE = "type";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private LinearLayout add;
    private LinearLayout change;
    private FloatingActionButton list;

    private Element element = new Element();
    private Chart chart = new Chart();

    private PrivateTaskService privateService;

    private Tasks tasks;

    public Task() {
        // Required empty public constructor
    }

    public static Task newInstance(String param) {
        Task fragment = new Task();
        Bundle args = new Bundle();
        args.putString(TYPE, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(TYPE).toString();
    }

    @Override
    @SuppressLint("RestrictedApi")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        add = view.findViewById(R.id.add);
        change = view.findViewById(R.id.change);
        list = view.findViewById(R.id.list);

        if (type.equals(Tasks.PUBLIC)) {
            ((ProjectProgressActivity) getActivity()).setOnBackPressedListener(this);
            tasks.setPubilc(true);
            add.setVisibility(View.INVISIBLE);
            change.setVisibility(View.INVISIBLE);
        } else {
            ((ProjectActivity) getActivity()).setOnBackPressedListener(this);
            tasks.setPubilc(false);
            add.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
        }

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClick();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClick();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeClick();
            }
        });

        privateService = new PrivateTaskService();
        privateService.setTasks(tasks);

        return view;
    }


    public void fabClick() {
        if (type.equals(Tasks.PUBLIC)) {
            if (add.getVisibility() == View.INVISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in2);
                Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in1);
                add.setVisibility(View.VISIBLE);
                change.setVisibility(View.VISIBLE);
                add.startAnimation(animation);
                change.startAnimation(animation2);
            } else {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out2);
                Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out1);
                add.setVisibility(View.INVISIBLE);
                change.setVisibility(View.INVISIBLE);
                add.startAnimation(animation);
                change.startAnimation(animation2);
            }
        }
    }

    public void addClick() {
        CreateTaskDialog.TaskEventListener listener =
                new CreateTaskDialog.TaskEventListener() {
                    @Override
                    public void publicTaskCreate(PublicTaskVO vo) {
                        PublicTaskService service = new PublicTaskService();
                        service.create(vo);
                        service.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                ((ProjectProgressActivity) getActivity()).stompBuilder.sendMessage(
                                        StompBuilder.INSERT,
                                        StompBuilder.PUBLICTASK,
                                        objects[0]
                                );
                            }
                        });
                    }

                    @Override
                    public void privateTaskCreate(PrivateTaskVO vo) {
                        privateService.create(vo);
                        privateService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                Toast.makeText(getContext(), "(업무) \"" + vo.getPttitle() + "\" 가 추가 되었습니다."
                                        , Toast.LENGTH_SHORT).show();
                                element.dataChange();
                            }
                        });
                    }

                    @Override
                    public void methodListCreate(List<com.service.taskdoc.database.business.transfer.Task> vos) {
                        Tasks tasks = new Tasks();
                        PublicTaskService service = new PublicTaskService();
                        service.setTasks(tasks);

                        service.createMulti(vos);
                        service.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                List<PublicTaskVO> vos = new ArrayList<>();
                                for (com.service.taskdoc.database.business.transfer.Task t : tasks.getPublicTasks()){
                                    PublicTaskVO v = Tasks.publicConverter(t);
                                    vos.add(v);
                                }
                                ((ProjectProgressActivity) getActivity()).stompBuilder.sendMessage(
                                        StompBuilder.INSERT,
                                        StompBuilder.PUBLICTASKS,
                                        vos
                                );
                            }
                        });
                    }
                };

        new CreateTaskDialog(getContext())
                .setTaskEventListener(listener)
                .setProject(((ProjectProgressActivity) getActivity()).project)
                .setPermision(((ProjectProgressActivity) getActivity()).project.getPpermission())
                .setActivity(getActivity())
                .showChoiceType();
        fabClick();
    }

    public void changeClick() {
        Intent intent = new Intent(getContext(), GanttChartActivity.class);

        intent.putExtra("prTasks", new Gson().toJson(tasks.getPrivateTasks()));

        if (type.equals(Tasks.PUBLIC)) {
            intent.putExtra("puTasks", new Gson().toJson(tasks.getPublicTasks()));

            intent.putExtra("project", new Gson().toJson(((ProjectProgressActivity) getActivity()).project));
            intent.putExtra("chatroom", new Gson().toJson(((ProjectProgressActivity) getActivity()).chatRoomInfoList.get(0)));
            intent.putExtra("documents", new Gson().toJson(((ProjectProgressActivity) getActivity()).documentList));
            intent.putExtra("decisions", new Gson().toJson(((ProjectProgressActivity) getActivity()).decisionList));
            intent.putExtra("chatrooms", new Gson().toJson(((ProjectProgressActivity) getActivity()).chatRoomList));
        }

        startActivity(intent);

        fabClick();
    }


    public void datachange() {
        element.dataChange();
    }


    @Override
    public void onBack() {
        int position = tabLayout.getSelectedTabPosition();

        if (element.canGoBack() && position == 0) {
            element.back();
        } else {
            if (type.equals(Tasks.PUBLIC)) {
                ((ProjectProgressActivity) getActivity()).setOnBackPressedListener(null);
                ((ProjectProgressActivity) getActivity()).onBackPressed();
            } else {
                ((ProjectActivity) getActivity()).setOnBackPressedListener(null);
                ((ProjectActivity) getActivity()).onBackPressed();
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new Fragment();

            switch (position) {
                case 0:
                    fragment = element;
                    break;
                case 1:
                    fragment = chart;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
        element.setTasks(tasks);
    }

}
