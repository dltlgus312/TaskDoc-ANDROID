package com.service.taskdoc.display.activity;

import android.content.Intent;
import android.os.Handler;
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
