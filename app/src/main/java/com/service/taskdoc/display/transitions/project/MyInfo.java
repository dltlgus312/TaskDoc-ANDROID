package com.service.taskdoc.display.transitions.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.display.activity.MethodListActivity;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.UserInfoService;

public class MyInfo extends Fragment implements NetworkSuccessWork {

    private final String CANCLE = "취소";
    private final String MODIFY = "수정";

    private TextView accept;
    private TextView modify;
    private TextView id;
    private TextView pw;
    private EditText name;
    private EditText state;
    private EditText phone;
    private Button method;

    private UserInfoService service;

    public MyInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        accept = view.findViewById(R.id.fragment_trans_my_info_accept);
        modify = view.findViewById(R.id.fragment_trans_my_info_modify);
        id = view.findViewById(R.id.fragment_trans_my_info_id);
        pw = view.findViewById(R.id.fragment_trans_my_info_pw);
        name = view.findViewById(R.id.fragment_trans_my_info_name);
        state = view.findViewById(R.id.fragment_trans_my_info_state);
        phone = view.findViewById(R.id.fragment_trans_my_info_phone);
        method = view.findViewById(R.id.fragment_trans_my_info_method);

        id.setText(UserInfo.getUid());
        name.setText(UserInfo.getUname());
        state.setText(UserInfo.getUstate());
        phone.setText(UserInfo.getUphone());

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify();
            }
        });

        method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MethodListActivity.class);
                startActivity(intent);
            }
        });

        service = new UserInfoService();
        service.work(this);
        return view;
    }

    public void modify() {
        String mode = modify.getText().toString();

        if(mode.equals(MODIFY)){
            name.setEnabled(true);
            state.setEnabled(true);
            phone.setEnabled(true);
            accept.setEnabled(true);
            accept.setVisibility(View.VISIBLE);
            modify.setText(CANCLE);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserInfoVO userInfoVO = new UserInfoVO();
                    userInfoVO.setUid(UserInfo.getUid());
                    userInfoVO.setUpasswd(UserInfo.getUpasswd());
                    userInfoVO.setUname(name.getText().toString());
                    userInfoVO.setUstate(state.getText().toString());
                    userInfoVO.setUphone(phone.getText().toString());
                    service.update(userInfoVO);
                }
            });

        }else {
            name.setText(UserInfo.getUname());
            state.setText(UserInfo.getUstate());
            phone.setText(UserInfo.getUphone());

            name.setEnabled(false);
            state.setEnabled(false);
            phone.setEnabled(false);
            accept.setEnabled(false);
            accept.setVisibility(View.INVISIBLE);
            modify.setText(MODIFY);
        }
    }

    @Override
    public void work(Object... objects) {
        UserInfo.setUname(name.getText().toString());
        UserInfo.setUstate(state.getText().toString());
        UserInfo.setUphone(phone.getText().toString());

        name.setEnabled(false);
        state.setEnabled(false);
        phone.setEnabled(false);
        accept.setEnabled(false);
        accept.setVisibility(View.INVISIBLE);
        modify.setText(MODIFY);
    }
}
