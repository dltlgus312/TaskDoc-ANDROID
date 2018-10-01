package com.service.taskdoc.display.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatRoomJoinVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.NoticeVO;
import com.service.taskdoc.database.transfer.ProjectJoinVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.display.custom.ganttchart.Line;
import com.service.taskdoc.display.recycle.DocumentCycle;
import com.service.taskdoc.display.transitions.progress.Tab1;
import com.service.taskdoc.display.transitions.progress.Tab2;
import com.service.taskdoc.display.transitions.progress.Tab3;
import com.service.taskdoc.service.network.restful.service.ChatRoomJoinService;
import com.service.taskdoc.service.network.restful.service.DocumentService;
import com.service.taskdoc.service.network.restful.service.NoticeService;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.network.restful.service.ProjectJoinService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.network.restful.service.UserInfoService;
import com.service.taskdoc.service.system.support.KeyboardManager;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.OnBackPressedListener;

import java.util.ArrayList;
import java.util.List;

public class ProjectProgressActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private OnBackPressedListener onBackPressedListener;

    private Tab1 tab1 = new Tab1();
    private Tab2 tab2 = new Tab2();
    private Tab3 tab3 = new Tab3();

    public Project project;

    public NoticeService noticeService;
    public ProjectJoinService projectJoinService;
    public ChatRoomJoinService chatRoomJoinService;
    public PublicTaskService publicService;
    public PrivateTaskService privateService;
    public DocumentService documentService;
    public UserInfoService userInfoService;

    public List<DocumentVO> documentList = new ArrayList<>();
    public List<ChatRoomInfo> chatRoomInfoList = new ArrayList<>();
    public List<NoticeVO> noticeList = new ArrayList<>();
    public List<UserInfos> userInfoList = new ArrayList<>();
    public Tasks tasks = new Tasks();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String extra = getIntent().getStringExtra("project");
        this.project = new Gson().fromJson(extra, Project.class);

        setTitle(project.getPtitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView permission = (TextView) header.findViewById(R.id.permission);
        permission.setText(project.getPpermission());

        tab1.setChatRoomInfosList(chatRoomInfoList);
        tab1.setDocumentList(documentList);
        tab1.setTasks(tasks);
        tab2.setNoticeVOList(noticeList);
        tab3.setUserInfosList(userInfoList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        documentList.clear();
        chatRoomInfoList.clear();
        noticeList.clear();
        userInfoList.clear();
        tasks.getPublicTasks().clear();
        tasks.getPrivateTasks().clear();

        networkService();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (this.onBackPressedListener != null) {
            this.onBackPressedListener.onBack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (project.getPpermission().equals(Projects.OWNER)) {
            getMenuInflater().inflate(R.menu.project_progress, menu);
            return true;
        } else return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.collabor) findUser();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.progress) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment, tab1).disallowAddToBackStack().commit();
        } else if (id == R.id.notice) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment, tab2).disallowAddToBackStack().commit();
        } else if (id == R.id.collabor) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment, tab3).disallowAddToBackStack().commit();
        } else if (id == R.id.manage) {

        } else if (id == R.id.out) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void networkService() {
        userInfoService = new UserInfoService();

        ChatRoomJoinVO chatRoomJoinVO = new ChatRoomJoinVO();
        chatRoomJoinVO.setUid(UserInfo.getUid());
        chatRoomJoinVO.setPcode(project.getPcode());

        chatRoomJoinService = new ChatRoomJoinService();
        chatRoomJoinService.setChatRoomInfoList(chatRoomInfoList);
        chatRoomJoinService.roomList(chatRoomJoinVO);
        chatRoomJoinService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment, tab1).disallowAddToBackStack().commit();
                documentService = new DocumentService(chatRoomInfoList.get(0).getChatRoomVO().getCrcode());
                documentService.setDocumentList(documentList);
                documentService.roomList();
                documentService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                    }
                });
            }
        });

        publicService = new PublicTaskService();
        publicService.setTasks(tasks);
        publicService.listAll(project.getPcode());
        publicService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
            }
        });

        noticeService = new NoticeService(project.getPcode());
        noticeService.setNoticeList(noticeList);
        noticeService.list();
        noticeService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
            }
        });


        projectJoinService = new ProjectJoinService();
        projectJoinService.setUserInfosList(userInfoList);
        projectJoinService.selectProjectJoinUsers(project.getPcode());
        projectJoinService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                tab3.datachange();
            }
        });
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public void findUser() {
        EditText input = new EditText(this);
        input.setSingleLine();
        input.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("인원 추가");
        builder.setView(input);
        builder.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new KeyboardManager().hide(ProjectProgressActivity.this, input);
                findService(input.getText().toString());
            }
        });
        AlertDialog dialog = builder.show();
        new KeyboardManager().show(this);

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                new KeyboardManager().hide(ProjectProgressActivity.this, input);
                dialog.dismiss();
                findService(input.getText().toString());
                return true;
            }
        });
    }

    public void findService(String uid) {
        userInfoService.getInfo(uid);
        userInfoService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                if (objects == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProjectProgressActivity.this);
                    builder.setTitle("NOT FOUND");
                    builder.setMessage("없는 사용자 입니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                } else {
                    UserInfoVO vo = (UserInfoVO) objects[0];

                    View view = getLayoutInflater().inflate(R.layout.recycle_item_users, null);
                    TextView id = view.findViewById(R.id.id);
                    TextView name = view.findViewById(R.id.name);
                    TextView state = view.findViewById(R.id.state);
                    TextView phone = view.findViewById(R.id.phone);

                    id.setText(vo.getUid());
                    name.setText(vo.getUname());
                    state.setText(vo.getUstate());
                    phone.setText(vo.getUphone());

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProjectProgressActivity.this);
                    builder.setTitle("추가");
                    builder.setView(view);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (!findCheckUser(vo)) {
                                Toast.makeText(ProjectProgressActivity.this,
                                        "이미 추가된 유저입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 리스트 추가
                            UserInfos userInfos = new UserInfos();
                            userInfos.setId(vo.getUid());
                            userInfos.setName(vo.getUname());
                            userInfos.setInvite(0);
                            userInfos.setPermission(Projects.MEMBER);
                            userInfos.setPhone(vo.getUphone());
                            userInfos.setState(vo.getUstate());
                            userInfoList.add(userInfos);

                            // 서버에 등록
                            ProjectJoinVO pvo = new ProjectJoinVO();
                            pvo.setPcode(project.getPcode());
                            pvo.setUid(vo.getUid());
                            pvo.setPpermission(Projects.MEMBER);
                            pvo.setPinvite(0);
                            projectJoinService.insert(pvo);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            }
        });

    }

    public boolean findCheckUser(UserInfoVO vo) {
        for (UserInfos v : this.userInfoList) {
            if (v.getId().equals(vo.getUid())) {
                return false;
            }
        }

        return true;
    }
}
