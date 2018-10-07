package com.service.taskdoc.display.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Chating;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.ChatRoomJoinVO;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.ProjectJoinVO;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionCreateDialog;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionItemSelectDialog;
import com.service.taskdoc.display.custom.custom.dialog.file.DialogFilePicker;
import com.service.taskdoc.display.custom.custom.dialog.file.FileDownLoadServiceDialog;
import com.service.taskdoc.display.custom.custom.dialog.file.FileUpLoadServiceDialog;
import com.service.taskdoc.display.recycle.ChatingCycle;
import com.service.taskdoc.display.recycle.UsersCycle;
import com.service.taskdoc.display.transitions.chatroom.Nav;
import com.service.taskdoc.service.network.restful.service.ChatContentsService;
import com.service.taskdoc.service.network.restful.service.ChatRoomJoinService;
import com.service.taskdoc.service.network.restful.service.ChatRoomService;
import com.service.taskdoc.service.network.restful.service.DecisionItemService;
import com.service.taskdoc.service.network.restful.service.DecisionService;
import com.service.taskdoc.service.network.restful.service.DocumentService;
import com.service.taskdoc.service.network.restful.service.ProjectJoinService;
import com.service.taskdoc.service.system.support.service.ConvertDpPixels;
import com.service.taskdoc.service.system.support.service.DownActionView;
import com.service.taskdoc.service.system.support.service.KeyboardManager;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MultipartBody;

import static android.view.View.GONE;

public class ChatingActivity extends AppCompatActivity implements NetworkSuccessWork, TextWatcher, StompBuilder.SubscribeListener, ChatingCycle.OnClickListener {

    private Project project;
    private ChatRoomVO chatRoomVO;
    private List<UserInfos> projectJoinUserInfo;

    private DownActionView docAction;
    private DownActionView searchAction;

    private LinearLayout searchLinear;
    private EditText search;
    private ImageView searchButton;
    private ImageButton searchUp;

    private RecyclerView recyclerView;
    private ChatingCycle cycle;

    private LinearLayout inputLinear;
    private EditText input;
    private ImageButton add;
    private ImageButton send;

    private LinearLayout docLinear;
    private LinearLayout file;
    private LinearLayout focus;
    private LinearLayout decision;

    private Nav navView = new Nav();

    private List<UserInfos> userList = new ArrayList<>();
    private List<DocumentVO> documentList = new ArrayList<>();
    private List<DecisionVO> decisionList = new ArrayList<>();
    private List<ChatRoomVO> focusList = new ArrayList<>();
    private List<Chating> chatContentsList = new ArrayList<>();
    private List<Chating> searchContentsList = new ArrayList<>();

    private ChatRoomJoinService chatRoomJoinService;
    private DocumentService documentService;
    private ChatRoomService focusService;
    private DecisionService decisionService;
    private ChatContentsService chatContentsService;
    private ProjectJoinService projectJoinService;

    private StompBuilder stompBuilder;

    private DialogFilePicker dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        project = new Gson().fromJson(getIntent().getStringExtra("project"), Project.class);
        chatRoomVO = new Gson().fromJson(getIntent().getStringExtra("chatRoomVO"), ChatRoomVO.class);

        int downDocDP = (int) ConvertDpPixels.convertDpToPixel(100, this);
        docAction = new DownActionView(downDocDP);

        int downSearchDP = (int) ConvertDpPixels.convertDpToPixel(28, this);
        searchAction = new DownActionView(downSearchDP);

        search = findViewById(R.id.search);
        searchButton = findViewById(R.id.search_button);
        searchLinear = findViewById(R.id.searchLinear);
        searchUp = findViewById(R.id.search_up);
        recyclerView = findViewById(R.id.recycle);
        inputLinear = findViewById(R.id.input_linear);
        input = findViewById(R.id.input);
        add = findViewById(R.id.add_doc);
        send = findViewById(R.id.send);
        docLinear = findViewById(R.id.doc_linear);
        file = findViewById(R.id.file);
        focus = findViewById(R.id.focus);
        decision = findViewById(R.id.decision);

        searchLinear.setVisibility(GONE);
        docLinear.setVisibility(GONE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (docAction.animationDown(docLinear)) {
                    searchAction.animationClose(searchLinear);
                    new KeyboardManager().hide(ChatingActivity.this, search);
                    input.setEnabled(true);
                    inputLinear.setBackgroundResource(R.color.colorWhite);
                }
            }
        });
        searchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAction.animationClose(searchLinear);
                new KeyboardManager().hide(ChatingActivity.this, search);
                input.setEnabled(true);
                inputLinear.setBackgroundResource(R.color.colorWhite);
            }
        });


        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDoc();
            }
        });
        focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoc();
            }
        });
        decision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDec();
            }
        });

        if (chatRoomVO.getCrmode() == 3) {
            focus.setVisibility(GONE);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_view, navView).addToBackStack(null).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = input.getText().toString();
                sendMessage(msg);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLastPosition();
            }
        });

        navView.setUserList(userList);
        navView.setDocumentList(documentList);
        navView.setDecisionList(decisionList);
        navView.setFocusList(focusList);

        cycle = new ChatingCycle(chatContentsList, documentList, decisionList, focusList);
        cycle.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        search.addTextChangedListener(this);

        connetionNetwork();
    }

    public void connetionNetwork() {

        // Query Reference Data
        int crcode = chatRoomVO.getCrcode();

        projectJoinUserInfo = new ArrayList<>();

        stompBuilder = new StompBuilder(project.getPcode());
        stompBuilder.connect();
        stompBuilder.setSubscribeListener(this);



        /*
         * Project All Join USER
         * */
        // Chapter 1
        projectJoinService = new ProjectJoinService();
        projectJoinService.setUserInfosList(projectJoinUserInfo);
        projectJoinService.selectProjectJoinUsers(project.getPcode());
        projectJoinService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {

                // Query Reference Data
                ChatRoomJoinVO vo = new ChatRoomJoinVO();
                vo.setPcode(project.getPcode());
                vo.setCrcode(chatRoomVO.getCrcode());


                /*
                 * Chating Room Join USER
                 * */
                // Chapter 2
                chatRoomJoinService = new ChatRoomJoinService();
                chatRoomJoinService.setUserInfosList(userList);
                chatRoomJoinService.userList(vo);
                chatRoomJoinService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        for (UserInfos vo : userList) {
                            for (UserInfos v : projectJoinUserInfo) {
                                if (vo.getId().equals(v.getId())) {
                                    vo.setPermission(v.getPermission());
                                    break;
                                }
                            }
                        }


                        /*
                         * Chating Contents
                         * */
                        // Chapter 3
                        chatContentsService = new ChatContentsService(crcode);
                        chatContentsService.setChatContentsList(chatContentsList);
                        chatContentsService.setUserList(userList);
                        chatContentsService.list();
                        chatContentsService.work(ChatingActivity.this);
                    }
                });
            }
        });



        /*
         * Document Data
         * */
        documentService = new DocumentService(crcode);
        documentService.setDocumentList(documentList);
        documentService.roomList();
        documentService.work(this);

        decisionService = new DecisionService(crcode);
        decisionService.setDecisionList(decisionList);
        decisionService.roomList();
        decisionService.work(this);

        focusService = new ChatRoomService(crcode);
        focusService.setChatRoomList(focusList);
        focusService.roomList();
        focusService.work(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            // Drawer Pager Close
            drawer.closeDrawer(GravityCompat.START);

        } else if (searchAction.isOpen()) {

            // Search Linear Close
            searchAction.animationClose(searchLinear);
            new KeyboardManager().hide(ChatingActivity.this, search);
            input.setEnabled(true);
            inputLinear.setBackgroundResource(R.color.colorWhite);

        } else if (docAction.isOpen()) {

            // Add Doc Linear Close
            docAction.animationClose(docLinear);

        } else {
            stompBuilder.disconnect();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (chatRoomVO.getCrmode() == 2) {
            getMenuInflater().inflate(R.menu.chating_mode1, menu);
        } else {
            getMenuInflater().inflate(R.menu.chating_mode2, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.collabor) {
            addCollabor();
            return true;
        } else if (id == R.id.search) {
            if (searchAction.animationDown(searchLinear)) {
                docAction.animationClose(docLinear);
                new KeyboardManager().show(this);
                input.setEnabled(false);
                inputLinear.setBackgroundResource(R.color.colorWhiteGray);
            } else {
                new KeyboardManager().hide(this, search);
                input.setEnabled(true);
                inputLinear.setBackgroundResource(R.color.colorWhite);
            }
        }
        return super.onOptionsItemSelected(item);
    }




    /*
     * Business
     * */

    public void addCollabor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        List<UserInfos> list = new ArrayList<>();
        list.addAll(projectJoinUserInfo);

        Iterator<UserInfos> it = list.iterator();
        while (it.hasNext()) {
            UserInfos vo = it.next();
            if (vo.getInvite() == 0) {
                it.remove();
                continue;
            }

            for (UserInfos v : userList) {
                if (vo.getId().equals(v.getId())) {
                    it.remove();
                    continue;
                }
            }
        }

        UsersCycle cycle = new UsersCycle(list);

        RecyclerView recyclerView = new RecyclerView(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        builder.setTitle("유저 추가");
        builder.setView(recyclerView);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.show();


        cycle.setOnClickListener(new UsersCycle.OnClickListener() {
            @Override
            public void onClick(CardView view, UserInfos userInfos) {
                dialog.dismiss();
                checkUserAdd(userInfos);
            }

            @Override
            public void onLongClick(CardView view, UserInfos userInfos) {
            }
        });
    }

    public void checkUserAdd(UserInfos userInfos) {
        for (UserInfos vo : userList) {
            if (vo.getId().equals(userInfos.getId())) {
                Toast.makeText(this, "이미 추가된 사용자 입니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ChatRoomJoinVO vo = new ChatRoomJoinVO();

        vo.setPcode(project.getPcode());
        vo.setCrcode(chatRoomVO.getCrcode());
        vo.setUid(userInfos.getId());

        chatRoomJoinService.insert(vo);
        stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.CHATROOMJOIN, vo);
    }

    public void goToLastPosition() {
        if (searchContentsList.size() > 0) {
            recyclerView.scrollToPosition(chatContentsList.indexOf(searchContentsList.get(searchContentsList.size() - 1)));
            searchContentsList.remove(searchContentsList.size() - 1);
        }
    }

    public void refresh() {
        if (recyclerView.isShown()) {
            cycle.notifyDataSetChanged();
            navView.notifyDataSetChanged();
            recyclerView.scrollToPosition(cycle.getItemCount() - 1);
        } else {

        }
    }




    /*
     * Search Contents
     * */

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        searchContentsList.clear();
        String text = search.getText().toString();

        if (text.equals("") || text.equals(" ")) {
            recyclerView.scrollToPosition(cycle.getItemCount() - 1);
            return;
        }

        for (Chating vo : chatContentsList) {
            if (vo.getChatContentsVO().getCcontents() != null && vo.getChatContentsVO().getCcontents().contains(text)) {
                searchContentsList.add(vo);
            }
        }
        goToLastPosition();
    }




    /*
     * Add Document
     * */

    public void addDoc() {
        FileUpLoadServiceDialog.FileUpLoadDialogListener listener = new FileUpLoadServiceDialog.FileUpLoadDialogListener() {
            @Override
            public void formatDate(List<MultipartBody.Part> multiPartList, DocumentVO vo) {

                AlertDialog dialog = new FileDownLoadServiceDialog(ChatingActivity.this).getArcDialog();
                dialog.show();

                documentService.upload(multiPartList, vo);
                documentService.setFileLoadService(new DocumentService.FileLoadService() {
                    @Override
                    public void success(DocumentVO vo) {
                        Toast.makeText(ChatingActivity.this,
                                "파일을 업로드 하였습니다.", Toast.LENGTH_SHORT).show();
                        stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.DOCUMENT, vo);
                        dialog.dismiss();


                        // 채팅 전송
                        ChatContentsVO chatContentsVO = new ChatContentsVO();
                        chatContentsVO.setCrcode(chatRoomVO.getCrcode());
                        chatContentsVO.setDmcode(vo.getDmcode());
                        chatContentsVO.setCcontents(vo.getDmtitle());
                        chatContentsVO.setUid(UserInfo.getUid());
                        chatContentsService.insert(chatContentsVO);
                    }

                    @Override
                    public void fail(String msg) {
                        Toast.makeText(ChatingActivity.this,
                                "파일을 업로드 하는데 실패 하였습니다.\n실패사유:" + msg, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        };

        new FileUpLoadServiceDialog(this)
                .setPcode(project.getPcode())
                .setCrcode(chatRoomVO.getCrcode())
                .setCrmode(chatRoomVO.getCrmode())
                .setFileUpLoadDialogListener(listener)
                .docUploadProcess();
    }

    public void addDec() {
        DecisionCreateDialog.DecisionCreateListener listener
                = new DecisionCreateDialog.DecisionCreateListener() {
            @Override
            public void getParameter(DecisionVO vo, List<DecisionItemVO> vos) {
                decisionService.insert(vo);
                decisionService.setInsertListener(new DecisionService.InsertListener() {
                    @Override
                    public void success(DecisionVO vo) {
                        for (DecisionItemVO v : vos)
                            v.setDscode(vo.getDscode());

                        DecisionItemService decisionItemService = new DecisionItemService();
                        decisionItemService.insert(vos);
                        decisionItemService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.DECISION, vo);

                                // 채팅 전송
                                ChatContentsVO chatContentsVO = new ChatContentsVO();
                                chatContentsVO.setCrcode(chatRoomVO.getCrcode());
                                chatContentsVO.setDscode(vo.getDscode());
                                chatContentsVO.setCcontents(vo.getDstitle());
                                chatContentsVO.setUid(UserInfo.getUid());
                                chatContentsService.insert(chatContentsVO);
                            }
                        });
                    }
                });
            }
        };

        new DecisionCreateDialog(this)
                .setDecisionCreateListener(listener)
                .setPcode(project.getPcode())
                .setCrcode(chatRoomVO.getCrcode())
                .setCrmode(chatRoomVO.getCrmode())
                .showDecisionParameter();

    }

    public void addFoc() {

    }




    /*
     * Click Contents
     * */

    @Override
    public void onDocClick(View view, DocumentVO vo) {
        new FileDownLoadServiceDialog(ChatingActivity.this, vo);
    }

    @Override
    public void onDecClick(View view, DecisionVO vo) {
        DecisionItemSelectDialog.DecisionEventListener listener
                = new DecisionItemSelectDialog.DecisionEventListener() {
            @Override
            public void closeClick() {
                stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.DECISION, vo);
            }
        };
        new DecisionItemSelectDialog(this, vo)
                .setCrmode(chatRoomVO.getCrmode())
                .setPermision(project.getPpermission())
                .setDecisionEventListener(listener)
                .showList();
    }

    @Override
    public void onChaClick(View view, ChatRoomVO vo) {

    }





    /*
     * NetWork Access
     * */

    public void sendMessage(String msg) {
        ChatContentsVO vo = new ChatContentsVO();
        vo.setCrcode(chatRoomVO.getCrcode());
        vo.setCcontents(msg);
        vo.setUid(UserInfo.getUid());

        chatContentsService.insert(vo);
        input.setText("");
    }

    @Override
    public void work(Object... objects) {

        // ChatContentService 에서 보낸 이벤트
        if (objects != null && objects.length > 0) {
            stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.CHATCONTENTS, objects[0]);
            return;
        }

        refresh();
    }


    /*
     * Stomp Subscribe
     * */
    @Override
    public void topic(String msg, String type, String object) {
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

    private void insertTopic(String type, String object) {
        switch (type) {
            case StompBuilder.PROJECTJOIN:
                projectJoinService.selectProjectJoinUsers(project.getPcode());
                projectJoinService.work(null);
                break;
            case StompBuilder.CHATCONTENTS:
                ChatContentsVO contentsVo = new Gson().fromJson(object, ChatContentsVO.class);
                Chating c = new Chating();
                c.setChatContentsVO(contentsVo);
                for (UserInfos u : userList) {
                    if (contentsVo.getUid().equals(u.getId())) {
                        c.setUserInfos(u);
                    }
                }
                chatContentsList.add(c);
                refresh();
                break;
            case StompBuilder.CHATROOMJOIN:
                ChatRoomJoinVO joinVo = new Gson().fromJson(object, ChatRoomJoinVO.class);

                for (UserInfos user : projectJoinUserInfo) {
                    if (user.getId().equals(joinVo.getUid())) {
                        userList.add(user);
                        Toast.makeText(this, "\"" + user.getName() + "\"" +
                                " 님을 채팅방에 추가 하셨습니다.", Toast.LENGTH_SHORT).show();
                        refresh();
                        break;
                    }
                }
                break;
            case StompBuilder.DOCUMENT:
                DocumentVO docVo = new Gson().fromJson(object, DocumentVO.class);
                documentList.add(docVo);
                refresh();
                break;
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                decisionList.add(decVo);
                refresh();
                break;
        }
    }

    private void updateTopic(String type, String object) {
        switch (type) {
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                if (decVo.getCrcode() != chatRoomVO.getCrcode()) return;
                for (DecisionVO vo : decisionList) {
                    if (vo.getDscode() == decVo.getDscode()) {
                        vo.setDsclose(decVo.getDsclose());
                        Toast.makeText(this, "(투표) \"" + vo.getDstitle() + "\" 가 종료 되었습니다."
                                , Toast.LENGTH_SHORT).show();
                        refresh();
                        break;
                    }
                }
                for (Chating chat : chatContentsList) {
                    if (chat.getChatContentsVO().getDscode() == decVo.getDscode()) {
                        recyclerView.scrollToPosition(chatContentsList.indexOf(chat));
                        break;
                    }
                }
                break;
            case StompBuilder.PROJECTJOIN :
                ProjectJoinVO projectJoinVO = new Gson().fromJson(object, ProjectJoinVO.class);

                if (chatRoomVO.getCrmode() != 1) return;
                for (UserInfos user : projectJoinUserInfo){
                    if (projectJoinVO.getUid().equals(user.getId())){
                        userList.add(user);
                        refresh();
                        Toast.makeText(this, "\"" + user.getName() + "\" 님이 프로젝트에 참가 하였습니다."
                                , Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                break;
        }
    }

    private void deleteTopic(String type, String object) {

    }
}
