package com.service.taskdoc.display.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatRoomJoinVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.NoticeVO;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.ProjectJoinVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.display.custom.custom.chart.DocOnTheBarItem;
import com.service.taskdoc.display.custom.custom.chart.TaskBarItem;
import com.service.taskdoc.display.transitions.progress.Tab1;
import com.service.taskdoc.display.transitions.progress.Tab2;
import com.service.taskdoc.display.transitions.progress.Tab3;
import com.service.taskdoc.service.network.restful.service.ChatRoomJoinService;
import com.service.taskdoc.service.network.restful.service.ChatRoomService;
import com.service.taskdoc.service.network.restful.service.DecisionService;
import com.service.taskdoc.service.network.restful.service.DocumentService;
import com.service.taskdoc.service.network.restful.service.NoticeService;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.network.restful.service.ProjectJoinService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.network.restful.service.UserInfoService;
import com.service.taskdoc.service.system.support.service.KeyboardManager;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.listener.OnBackPressedListener;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.ArrayList;
import java.util.List;

public class ProjectProgressActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, StompBuilder.SubscribeListener {


    public Project project;


    private Tab1 tab1 = new Tab1();
    private Tab2 tab2 = new Tab2();
    private Tab3 tab3 = new Tab3();


    public Tasks tasks = new Tasks();
    public List<NoticeVO> noticeList = new ArrayList<>();
    public List<UserInfos> userInfoList = new ArrayList<>();
    public List<ChatRoomInfo> chatRoomInfoList = new ArrayList<>();
    public List<DocumentVO> documentList = new ArrayList<>();
    public List<DecisionVO> decisionList = new ArrayList<>();
    public List<ChatRoomVO> chatRoomList = new ArrayList<>();


    public NoticeService noticeService;
    public ProjectJoinService projectJoinService;
    public ChatRoomJoinService chatRoomJoinService;
    public PublicTaskService publicService;
    public PrivateTaskService privateService;
    public UserInfoService userInfoService;
    public DocumentService documentService;
    public DecisionService decisionService;
    public ChatRoomService chatRoomService;

    public StompBuilder stompBuilder;


    private OnBackPressedListener onBackPressedListener;


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

        networkService();

    }

    public void networkService() {

        stompBuilder = new StompBuilder(project.getPcode());
        stompBuilder.setSubscribeListener(this);
        stompBuilder.connect();

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
                refreshDocument();

                decisionService = new DecisionService(chatRoomInfoList.get(0).getChatRoomVO().getCrcode());
                decisionService.setDecisionList(decisionList);
                refreshDecision();

                chatRoomService = new ChatRoomService(chatRoomInfoList.get(0).getChatRoomVO().getCrcode());
                chatRoomService.setChatRoomList(chatRoomList);
                refreshFocusRoom();
            }
        });

        noticeService = new NoticeService(project.getPcode());
        noticeService.setNoticeList(noticeList);
        refreshNotice();

        publicService = new PublicTaskService();
        publicService.setTasks(tasks);
        refreshTask();

        privateService = new PrivateTaskService();
        privateService.setTasks(tasks);
        privateService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {

            }
        });

        projectJoinService = new ProjectJoinService();
        projectJoinService.setUserInfosList(userInfoList);
        refreshProjectJoin();
    }


    /*
     * Refresh
     * */
    public void refreshChatRoom() {
        chatRoomInfoList.clear();

        ChatRoomJoinVO chatRoomJoinVO = new ChatRoomJoinVO();
        chatRoomJoinVO.setUid(UserInfo.getUid());
        chatRoomJoinVO.setPcode(project.getPcode());

        chatRoomJoinService.roomList(chatRoomJoinVO);
        chatRoomJoinService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                tab1.chatRoomViewRefresh();
            }
        });
    }

    public void refreshNotice() {
        noticeList.clear();

        noticeService.list();
        noticeService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
            }
        });
    }

    public void refreshTask() {
        tasks.getPrivateTasks().clear();
        tasks.getPublicTasks().clear();

        publicService.listAll(project.getPcode());
        publicService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
            }
        });
    }

    public void refreshProjectJoin() {
        userInfoList.clear();

        projectJoinService.selectProjectJoinUsers(project.getPcode());
        projectJoinService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                tab3.datachange();
            }
        });
    }

    public void refreshDocument() {
        documentList.clear();

        documentService.roomList();
        documentService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                tab1.documentViewRefresh();
            }
        });
    }

    public void refreshDecision() {
        decisionList.clear();

        decisionService.roomList();
        decisionService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
            }
        });
    }

    public void refreshFocusRoom() {
        chatRoomList.clear();

        chatRoomService.roomList();
        chatRoomService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {

            }
        });
    }


    /*
     * Colaboration
     * */
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

                            stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.PROJECTJOIN, pvo);
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


    /*
     * BackPress Event
     * */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (this.onBackPressedListener != null) {
            this.onBackPressedListener.onBack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            stompBuilder.disconnect();
            super.onBackPressed();
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }


    /*
     * Menu Item
     * */
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


    /*
     * Stomp Listener
     * */
    @Override
    public void topic(String msg, String type, String object) {
        // 채팅, 업무, 자료, 공지, 구성원(참여확인누를시)

        switch (msg) {
            case StompBuilder.INSERT:
                insertTopic(type, object);
                break;
            case StompBuilder.UPDATE:
                updateTopic(type, object);
                break;
            case StompBuilder.DELETE:
                deleteTopic(type, object);
                break;
        }
    }

    public void insertTopic(String type, String object) {
        switch (type) {
            case StompBuilder.CHATROOM:
                ChatRoomVO chatVo = new Gson().fromJson(object, ChatRoomVO.class);
                if (chatVo.getCrmode() != 3) refreshChatRoom();
                else if (chatVo.getCrcoderef() == chatRoomInfoList.get(0).getChatRoomVO().getCrcode()) {
                    refreshFocusRoom();
                }
                break;
            case StompBuilder.CHATROOMJOIN:
                refreshChatRoom();
                break;
            case StompBuilder.PROJECTJOIN:
                ProjectJoinVO joinVo = new Gson().fromJson(object, ProjectJoinVO.class);
                refreshProjectJoin();
                Toast.makeText(this, "(유저) \"" + joinVo.getUid() + "\" 님에게 프로젝트 초대 메시지를 보냈습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.CHATCONTENTS:
                tab1.addChatContentsAlarm(object);
                break;
            case StompBuilder.DOCUMENT:
                DocumentVO vo = new Gson().fromJson(object, DocumentVO.class);
                if (vo.getCrcode() == chatRoomInfoList.get(0).getChatRoomVO().getCrcode())
                    refreshDocument();
                Toast.makeText(this, "(파일) \"" + vo.getDmtitle() + "\" 가 추가 되었습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.DECISION:
                refreshDecision();
                break;
            case StompBuilder.PUBLICTASK :
                PublicTaskVO publicTaskVO = new Gson().fromJson(object, PublicTaskVO.class);

                Task t = new Task();
                t.setCode(publicTaskVO.getTcode());
                t.setTitle(publicTaskVO.getTtitle());
                t.setColor(publicTaskVO.getTcolor());
                t.setSdate(publicTaskVO.getTsdate());
                t.setEdate(publicTaskVO.getTedate());
                t.setPercent(publicTaskVO.getTpercent());
                t.setRefference(publicTaskVO.getTrefference());
                t.setSequence(publicTaskVO.getTsequence());
                t.setRefpcode(publicTaskVO.getPcode());

                tasks.addSort(t);

                Toast.makeText(this, "(업무) \"" + t.getTitle() + "\" 가 추가 되었습니다."
                        , Toast.LENGTH_SHORT).show();

                tab1.taskViewRefresh();
                tab1.documentViewRefresh();
                break;
        }
    }

    public void updateTopic(String type, String object) {
        switch (type) {
            case StompBuilder.CHATROOM:
                ChatRoomVO chatVo = new Gson().fromJson(object, ChatRoomVO.class);

                for (ChatRoomVO vo : chatRoomList) {
                    if (vo.getCrcode() == chatVo.getCrcode()) {
                        if (chatVo.getCrclose() == 1) {
                            vo.setCrclose(chatVo.getCrclose());
                            Toast.makeText(this, "(회의) \"" + vo.getFctitle() + "\" 가 종료 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            vo.setTcode(chatVo.getTcode());
                            Toast.makeText(this, "(투표) \"" + vo.getFctitle() + "\" 의 업무 위치가 변경 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case StompBuilder.PROJECTJOIN:
                ProjectJoinVO joinVo = new Gson().fromJson(object, ProjectJoinVO.class);
                refreshChatRoom();
                refreshProjectJoin();
                Toast.makeText(this, "(유저) \"" + joinVo.getUid() + "\" 님이 프로젝트에 참가 하였습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                if (decVo.getCrcode() != chatRoomInfoList.get(0).getChatRoomVO().getCrcode())
                    return;
                for (DecisionVO vo : decisionList) {
                    if (vo.getDscode() == decVo.getDscode()) {
                        if (decVo.getDsclose() == 1) {
                            vo.setDsclose(decVo.getDsclose());
                            Toast.makeText(this, "(투표) \"" + vo.getDstitle() + "\" 가 종료 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            vo.setTcode(decVo.getTcode());
                            Toast.makeText(this, "(투표) \"" + vo.getDstitle() + "\" 의 업무 위치가 변경 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                break;
            case StompBuilder.DOCUMENT:
                refreshDocument();
                DocumentVO docVo = new Gson().fromJson(object, DocumentVO.class);
                Toast.makeText(this, "(자료) \"" + docVo.getDmtitle() + "\" 의 업무 위치가 변경 되었습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.PUBLICTASK :
                PublicTaskVO publicTaskVO = new Gson().fromJson(object, PublicTaskVO.class);
                for (Task t : tasks.getPublicTasks()){
                    if (t.getCode() == publicTaskVO.getTcode()){
                        t.setTitle(publicTaskVO.getTtitle());
                        t.setSdate(publicTaskVO.getTsdate());
                        t.setEdate(publicTaskVO.getTedate());
                        t.setPercent(publicTaskVO.getTpercent());
                        t.setColor(publicTaskVO.getTcolor());
                    }
                }
                tab1.taskViewRefresh();
                break;
            case StompBuilder.PRIVATETASK :
                PrivateTaskVO privateTaskVO = new Gson().fromJson(object, PrivateTaskVO.class);
                if (!privateTaskVO.getUid().equals(UserInfo.getUid())) return;

                for (Task t : tasks.getPrivateTasks()){
                    if (t.getCode() == privateTaskVO.getPtcode()){
                        t.setTitle(privateTaskVO.getPttitle());
                        t.setSdate(privateTaskVO.getPtsdate());
                        t.setEdate(privateTaskVO.getPtedate());
                        t.setPercent(privateTaskVO.getPtpercent());
                        t.setColor(privateTaskVO.getPtcolor());
                    }
                }
                break;

        }
    }

    public void deleteTopic(String type, String object) {
        switch (type) {
            case StompBuilder.PROJECTJOIN:
                refreshProjectJoin();
                break;
        }
    }
}
