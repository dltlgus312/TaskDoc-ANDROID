package com.service.taskdoc.display.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Methods;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.MethodBoardVO;
import com.service.taskdoc.database.transfer.MethodListVO;
import com.service.taskdoc.display.transitions.methodboard.Contents;
import com.service.taskdoc.display.transitions.methodboard.Element;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.MethodBoardService;
import com.service.taskdoc.service.network.restful.service.MethodListService;
import com.service.taskdoc.service.system.support.OnBackPressedListener;

public class MethodBoardViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MethodBoardViewActivity";

    private final int REQUESTCODE = 1;

    public MethodBoardVO vo;

    public OnBackPressedListener onBackPressedListener;

    private final String ADDCANCLEBANNER = "이미 추가된 항목입니다.";
    private final String ADDSUCCESSBANNER = "추가 완료";
    private final String ADDTITLE = "제목 입력";
    private final String ADDBANNER = "내 리스트에서 보여질 제목 입력";
    private final String DELETETITLE = "삭제";
    private final String DELETEMESSAGE = "정말 삭제 하시겠습니까?";
    private final String ACCEPT = "확인";
    private final String CANCLE = "취소";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_board_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String board = getIntent().getStringExtra("board");
        vo = new Gson().fromJson(board, MethodBoardVO.class);


        getSupportFragmentManager().beginTransaction().replace(
                R.id.content_method_board, new Contents()).disallowAddToBackStack().commit();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (this.onBackPressedListener != null) {
            onBackPressedListener.onBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (vo.getUid().equals(UserInfo.getUid())) {
            getMenuInflater().inflate(R.menu.menu_project_owner, menu);
        } else {
            getMenuInflater().inflate(R.menu.method_board_view, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_method_board_add) {
            methodAdd();
            return true;
        } else if (id == R.id.menu_project_owner_modify) {
            Intent intent = new Intent(this, MethodBoardAddActivity.class);
            intent.putExtra("board", new Gson().toJson(vo));
            startActivityForResult(intent, REQUESTCODE);
            return true;
        } else if (id == R.id.menu_project_owner_delete) {
            deleteMethod();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        try {
            if (id == R.id.nav_method_board_contents) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.content_method_board, new Contents()).disallowAddToBackStack().commit();
            } else if (id == R.id.nav_method_board_element) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.content_method_board, Element.
                                newInstance(Element.ELEMENT)).disallowAddToBackStack().commit();
            } else if (id == R.id.nav_method_board_output) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.content_method_board, Element.
                                newInstance(Element.OUTPUT)).disallowAddToBackStack().commit();
            } else if (id == R.id.nav_method_board_my_list) {
                Intent intent = new Intent(this, MethodListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else if (id == R.id.nav_method_board_back){
                finish();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESTCODE :
                if(resultCode == RESULT_OK){
                    int pcode = data.getIntExtra("pcode", 0);
                    vo.setPcode(pcode);
                }
        }
    }

    public void deleteMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(DELETETITLE);
        builder.setMessage(DELETEMESSAGE);
        builder.setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MethodBoardService methodBoardService = new MethodBoardService();
                methodBoardService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        onBackPressed();
                    }
                });
                methodBoardService.delete(vo.getMbcode());
            }
        });
        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void methodAdd() {

        for (MethodListVO vo : Methods.getMethodLists()) {
            if (vo.getMbcode() == this.vo.getMbcode()) {
                Toast.makeText(this, ADDCANCLEBANNER, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        EditText input = new EditText(this);
        input.setHint(ADDBANNER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ADDTITLE);
        builder.setView(input);

        builder.setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MethodListVO vo = new MethodListVO();
                vo.setMbcode(MethodBoardViewActivity.this.vo.getMbcode());
                vo.setMltitle(input.getText().toString());
                vo.setUid(UserInfo.getUid());

                MethodListService service = new MethodListService();
                service.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        int a = (int) objects[0];
                        if (a == -1) {
                            Toast.makeText(MethodBoardViewActivity.this, ADDCANCLEBANNER, Toast.LENGTH_SHORT).show();
                        } else if (a == 1) {
                            Methods.getMethodLists().add(vo);
                            Toast.makeText(MethodBoardViewActivity.this, ADDSUCCESSBANNER, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                service.insert(vo);
            }
        });

        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
}
