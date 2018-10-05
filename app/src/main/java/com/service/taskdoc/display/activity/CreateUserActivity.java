package com.service.taskdoc.display.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.UserInfoService;

public class CreateUserActivity extends AppCompatActivity implements NetworkSuccessWork {

    private final String TAG = "CreateUser";

    private final String BANNER_PW_CH = "비밀번호를 확인해주세요";
    private final String BANNER_FAILED = "이미 가입되어 있는 아이디 입니다.";
    private final String BANNER_SUCCESS = "가입 완료";

    private UserInfoService service;

    private EditText id;
    private EditText pw;
    private EditText pw_ch;
    private EditText name;
    private EditText phone;
    private TextView warning;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        id =  findViewById(R.id.create_user_id);
        pw =  findViewById(R.id.create_user_pw);
        pw_ch = findViewById(R.id.create_user_pw_ch);
        name = findViewById(R.id.create_user_name);
        phone = findViewById(R.id.create_user_phone);
        warning = findViewById(R.id.create_user_warning);
        register = findViewById(R.id.create_user_register);

        pw_ch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!pw.getText().toString().equals(pw_ch.getText().toString())){
                    warning.setVisibility(View.VISIBLE);
                }else {
                    warning.setVisibility(View.INVISIBLE);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        if(Build.VERSION.SDK_INT <= 21){
            register.setBackground(getResources().getDrawable(R.drawable.style_button_accent));
        }

        service = new UserInfoService();
        service.work(this);
    }

    public void createUser(){

        // 구문검사
        if(!pw.getText().toString().equals(pw_ch.getText().toString())){
            Toast.makeText(this, BANNER_PW_CH, Toast.LENGTH_SHORT).show();
            return;
        }

        UserInfoVO userInfoVO = new UserInfoVO();

        userInfoVO.setUid(id.getText().toString());
        userInfoVO.setUpasswd(pw.getText().toString());
        userInfoVO.setUname(name.getText().toString());
        userInfoVO.setUphone(phone.getText().toString());

        service.create(userInfoVO);
    }

    @Override
    public void work(Object... objects) {
        int result = (int) objects[0];

        if (result == 1){
            Toast.makeText(this, BANNER_SUCCESS, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }else if (result == -1){
            Toast.makeText(this, BANNER_FAILED, Toast.LENGTH_SHORT).show();
        }
    }
}
