package com.service.taskdoc.display.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.UserInfoService;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

public class LoginActivity extends AppCompatActivity implements NetworkSuccessWork {

    private final String TAG = "Login";

    private final String BANNER_ID = "아이디를 확인 해주세요";
    private final String BANNER_PW = "패스워드를 확인해주세요";

    private Handler handler;

    private EditText id;
    private EditText pw;

    private UserInfoService service;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            // 이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다
            }
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        LinearLayout linearLayout = findViewById(R.id.activity_login_parents);

        LinearLayout loginForm = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_form_login, null);
        id = loginForm.findViewById(R.id.custom_form_login_id);
        pw = loginForm.findViewById(R.id.custom_form_login_pw);

        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    new KeyboardManager().hide(getApplicationContext(), pw);
                    login(pw);
                    return true;
                }
                return false;
            }
        });

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.alpha);
                linearLayout.addView(loginForm);
                loginForm.startAnimation(animation);
            }
        }, 2000);

        service = new UserInfoService();
        service.work(this);
    }

    public void login(View view){
        String id = this.id.getText().toString();
        String pw = this.pw.getText().toString();

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUid(id);
        userInfoVO.setUpasswd(pw);

        service.login(userInfoVO);
    }

    public void create(View view){
        // 회원가입 Activity으로 전환
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    @Override
    public void work(Object... objects) {
        int result = (int) objects[0];
        if(result == 1){
            Intent intent = new Intent(this, ProjectActivity.class);
            startActivity(intent);
        } else if(result == -1){
            Toast.makeText(this, BANNER_ID, Toast.LENGTH_SHORT).show();
        }else if (result == -2){
            Toast.makeText(this, BANNER_PW, Toast.LENGTH_SHORT).show();
        }
    }
}
