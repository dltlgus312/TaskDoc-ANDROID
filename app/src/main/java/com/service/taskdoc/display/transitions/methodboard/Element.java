package com.service.taskdoc.display.transitions.methodboard;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.PublicTaskVO;
import com.service.taskdoc.display.activity.MethodBoardViewActivity;
import com.service.taskdoc.display.recycle.TaskCycle;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.listener.OnBackPressedListener;

import java.util.List;

public class Element extends Fragment implements NetworkSuccessWork, TaskCycle.ClickListener, OnBackPressedListener {

    private Tasks tasks;
    private TaskCycle cycle;
    private RecyclerView recyclerView;

    private MethodBoardViewActivity activity;

    private PublicTaskService service;

    private String type;

    public static final String ELEMENT = "element";
    public static final String OUTPUT = "output";
    public static final String TYPE = "type";

    private TextView banner;
    private LinearLayout path;
    private TextView home;

    public Element() {
        // Required empty public constructor
    }

    public static Element newInstance(String param) {
        Bundle args = new Bundle();
        args.putString(TYPE, param);
        Element fragment = new Element();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = getArguments().getString(TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_method_board_element, container, false);
        this.banner = view.findViewById(R.id.banner);
        path = view.findViewById(R.id.path);
        home = view.findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cycle.goTo(0);
                recyclerView.scheduleLayoutAnimation();
                path.removeViews(1, path.getChildCount()-1);
            }
        });


        this.service = new PublicTaskService();
        service.work(this);

        activity = ((MethodBoardViewActivity) getActivity());
        activity.setOnBackPressedListener(this);

        tasks = new Tasks();

        cycle = new TaskCycle(tasks);

        cycle.setContentVisible(View.GONE);
        cycle.setClickListener(this);

        if (this.type.equals(OUTPUT)) {
            cycle.setOutput(true);
        } else {
            cycle.setDownArrowVisible(View.VISIBLE);
        }

        recyclerView = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        return view;
    }

    @Override
    public void work(Object... objects) {

        List<PublicTaskVO> pTasks = (List<PublicTaskVO>) objects[0];
        tasks.getPublicTasks().clear();

        for (PublicTaskVO vo : pTasks) {
            Task t = new Task();

            t.setCode(vo.getTcode());
            t.setTitle(vo.getTtitle());
            t.setColor(vo.getTcolor());
            t.setSdate(vo.getTsdate());
            t.setEdate(vo.getTedate());
            t.setRefference(vo.getTrefference());
            t.setSequence(vo.getTsequence());
            t.setRefpcode(vo.getPcode());

            tasks.getPublicTasks().add(t);
        }
        cycle.init();
        recyclerView.scheduleLayoutAnimation();

        if (cycle.getItemCount() != 0) {
            banner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        service.list(activity.vo.getPcode());
    }

    @Override
    public void onBack() {
        if (cycle.canGoBack()) {
            cycle.back();
            recyclerView.scheduleLayoutAnimation();
            this.path.removeViewAt(this.path.getChildCount()-1);
        } else {
            this.activity.setOnBackPressedListener(null);
            this.activity.onBackPressed();
        }
    }

    @Override
    public void onClick(Task task) {
        cycle.go(task);
        recyclerView.scheduleLayoutAnimation();

        Button p = new Button(getContext());
        p.setText(task.getTitle());

        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = path.indexOfChild(p);
                cycle.goTo(i);
                recyclerView.scheduleLayoutAnimation();
                path.removeViews(i+1, path.getChildCount() - (i + 1));
            }
        });

        this.path.addView(p);
    }

    @Override
    public void onLongClick(View view, Task task) {

    }
}
