package com.service.taskdoc.display.transitions.methodboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.MethodBoardVO;
import com.service.taskdoc.display.activity.MethodBoardAddActivity;
import com.service.taskdoc.display.activity.MethodBoardViewActivity;
import com.service.taskdoc.display.recycle.MethodCycle;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.MethodBoardService;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

import java.util.ArrayList;
import java.util.List;

public class MethodBoard extends Fragment implements NetworkSuccessWork, MethodCycle.Listener {

    private LinearLayout visible;
    private EditText search;
    private ImageView button;
    private FloatingActionButton add;
    private List<MethodBoardVO> methodList;
    private TextView banner;

    private List<MethodBoardVO> list = new ArrayList<>();
    private MethodCycle adapter;

    private MethodBoardService methodBoardService;

    private String type;

    public static final String MY="my";
    public static final String ALL="all";
    public static final String TYPE = "type";

    public MethodBoard() {

    }

    public static MethodBoard newInstance(String param) {

        Bundle args = new Bundle();

        args.putString(TYPE, param);
        MethodBoard fragment = new MethodBoard();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = getArguments().getString(TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_method_board, container, false);

        visible = view.findViewById(R.id.fragment_trans_method_visible);
        search = view.findViewById(R.id.fragment_trans_method_search);
        button = view.findViewById(R.id.fragment_trans_method_search_button);
        add = view.findViewById(R.id.fragment_trans_method_fab);
        banner = view.findViewById(R.id.fragment_trans_method_banner);

        if (this.type.equals(MY)) {
            visible.setVisibility(View.GONE);
        }else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new KeyboardManager().hide(getContext(), search);
                }
            });

            search.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        new KeyboardManager().hide(getContext(), search);
                        return true;
                    }
                    return false;
                }
            });

            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    textChange();
                }
            });
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodBoardAdd();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.fragment_trans_method_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MethodCycle(list);
        adapter.setListener(this);

        recyclerView.setAdapter(adapter);

        methodBoardService = new MethodBoardService();
        methodBoardService.work(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.type.equals(MY)) {
            methodBoardService.myList(UserInfo.getUid());
        } else {
            methodBoardService.list();
        }
    }

    @Override
    public void work(Object... objects) {
        if(this.type.equals(MY)){
            list.clear();
            List<MethodBoardVO> li = (List<MethodBoardVO>) objects[0];
            list.addAll(li);
            adapter.notifyDataSetChanged();
        }else {
            methodList = (List<MethodBoardVO>) objects[0];
            textChange();
        }
        if(list.size() != 0){
            banner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(MethodBoardVO vo) {
        Intent intent = new Intent(getContext(), MethodBoardViewActivity.class);
        intent.putExtra("board", new Gson().toJson(vo));
        startActivity(intent);
    }

    @Override
    public void onLongClick(MethodBoardVO vo) {

    }

    public void methodBoardAdd() {
        Intent intent = new Intent(getContext(), MethodBoardAddActivity.class);
        startActivity(intent);
    }

    public void textChange() {
        list.clear();
        for (MethodBoardVO vo : methodList) {
            if (vo.getMbtitle().contains(search.getText().toString())) {
                list.add(vo);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
