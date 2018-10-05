package com.service.taskdoc.display.transitions.task;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.display.activity.GanttChartActivity;
import com.service.taskdoc.display.activity.ProjectActivity;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.listener.OnBackPressedListener;

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

    private PublicTaskService publicService;
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
            publicService = ((ProjectProgressActivity) getActivity()).publicService;
            privateService = ((ProjectProgressActivity) getActivity()).privateService;
            ((ProjectProgressActivity) getActivity()).setOnBackPressedListener(this);
            tasks.setPubilc(true);
            add.setVisibility(View.INVISIBLE);
            change.setVisibility(View.INVISIBLE);
        } else {
            privateService = ((ProjectActivity) getActivity()).privateService;
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


        fabClick();
    }

    public void changeClick() {
        Intent intent = new Intent(getContext(), GanttChartActivity.class);

        intent.putExtra("puTasks", new Gson().toJson(tasks.getPublicTasks()));
        intent.putExtra("prTasks", new Gson().toJson(tasks.getPrivateTasks()));

        if (type.equals(Tasks.PUBLIC)) {
            intent.putExtra("project", new Gson().toJson(((ProjectProgressActivity) getActivity()).project));
            intent.putExtra("documents", new Gson().toJson(((ProjectProgressActivity) getActivity()).documentList));
        }

        startActivity(intent);

        fabClick();
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
