package com.service.taskdoc.display.transitions.document;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.recycle.DocumentCycle;
import com.service.taskdoc.service.system.support.ConvertDpPixels;
import com.service.taskdoc.service.system.support.DownActionView;
import com.service.taskdoc.service.system.support.OnBackPressedListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Element extends Fragment implements DocumentCycle.ClickListener, OnBackPressedListener, View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

    private Tasks tasks;
    private List<DocumentVO> documentList;

    private DocumentCycle cycle;

    private RecyclerView recyclerView;
    private LinearLayout stack;
    private Spinner spinner;
    private LinearLayout searchBar;
    private EditText search;
    private LinearLayout addFab;
    private LinearLayout multiFab;
    private LinearLayout searchFab;
    private FloatingActionButton listFab;

    private DownActionView downActionView;

    private String searchMode = "전체";

    public Element() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_element, container, false);

        recyclerView = view.findViewById(R.id.recycle);
        spinner = view.findViewById(R.id.spinner);
        searchBar = view.findViewById(R.id.search_bar);
        search = view.findViewById(R.id.search);
        stack = view.findViewById(R.id.stack);
        listFab = view.findViewById(R.id.listFab);
        addFab = view.findViewById(R.id.addFab);
        multiFab = view.findViewById(R.id.multiFab);
        searchFab = view.findViewById(R.id.searchFab);
        Button home = view.findViewById(R.id.home);

        cycle = new DocumentCycle(tasks, documentList);
        cycle.setClickListener(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        ((ProjectProgressActivity) getActivity()).setOnBackPressedListener(this);


        spinner.setOnItemSelectedListener(this);
        search.addTextChangedListener(this);
        searchBar.setVisibility(View.GONE);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cycle.goTo(0);
                recyclerView.scheduleLayoutAnimation();
                stack.removeViews(1, stack.getChildCount() - 1);
            }
        });

        addFab.setVisibility(View.INVISIBLE);
        multiFab.setVisibility(View.INVISIBLE);
        searchFab.setVisibility(View.INVISIBLE);

        listFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFabClick();
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFabClick();
            }
        });

        multiFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFabClick();
            }
        });

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downActionView.animationDown(searchBar);
                listFabClick();
            }
        });

        int searchBarDP = (int) ConvertDpPixels.convertDpToPixel(40, getContext());
        downActionView = new DownActionView(searchBarDP);

        return view;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public void setDocumentList(List<DocumentVO> documentList) {
        this.documentList = documentList;
    }

    public void listFabClick(){
        if (!addFab.isShown()){
            Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in1);
            Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in2);
            Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in3);
            addFab.setVisibility(View.VISIBLE);
            multiFab.setVisibility(View.VISIBLE);
            searchFab.setVisibility(View.VISIBLE);
            addFab.startAnimation(animation3);
            multiFab.startAnimation(animation2);
            searchFab.startAnimation(animation1);
        } else {
            Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out1);
            Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out2);
            Animation animation3 = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out3);
            addFab.setVisibility(View.INVISIBLE);
            multiFab.setVisibility(View.INVISIBLE);
            searchFab.setVisibility(View.INVISIBLE);
            addFab.startAnimation(animation3);
            multiFab.startAnimation(animation2);
            searchFab.startAnimation(animation1);
        }
    }

    @Override
    public void folderClick(Task task) {
        cycle.go(task);
        recyclerView.scheduleLayoutAnimation();
        Button button = new Button(getContext());
        button.setText(task.getTitle());
        button.setOnClickListener(this);
        stack.addView(button);
    }

    @Override
    public void folderLongClick(Task task) {

    }

    @Override
    public void fileClick(DocumentVO vo) {

    }

    @Override
    public void fileLongClick(DocumentVO vo) {

    }

    @Override
    public void onBack() {
        if (cycle.canGoBack()){
            cycle.back();
            recyclerView.scheduleLayoutAnimation();
            stack.removeViewAt(stack.getChildCount()-1);
        } else {
            ((ProjectProgressActivity) getActivity()).setOnBackPressedListener(null);
            ((ProjectProgressActivity) getActivity()).onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        int index = stack.indexOfChild(view);
        cycle.goTo(index);
        recyclerView.scheduleLayoutAnimation();
        stack.removeViews(index + 1, stack.getChildCount() - (index + 1));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        cycle.search(search.getText().toString(), searchMode);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        searchMode = adapterView.getItemAtPosition(i)+"";
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
