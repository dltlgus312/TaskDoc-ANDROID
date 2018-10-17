package com.service.taskdoc.display.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
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
import com.service.taskdoc.display.custom.custom.dialog.chat.FocusCreateDialog;
import com.service.taskdoc.display.custom.custom.dialog.chat.FocusItemSelectDialog;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionCreateDialog;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionItemSelectDialog;
import com.service.taskdoc.display.custom.custom.dialog.file.DialogDocParam;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MultipartBody;

import static android.view.View.GONE;

public class ChatingActivity extends AppCompatActivity implements
        NetworkSuccessWork, StompBuilder.SubscribeListener, ChatingCycle.OnClickListener {


    List<UserInfos> userList = new ArrayList<>();
    List<DocumentVO> documentList = new ArrayList<>();
    List<DecisionVO> decisionList = new ArrayList<>();
    List<ChatRoomVO> focusList = new ArrayList<>();
    List<Chating> chatContentsList = new ArrayList<>();
    List<Chating> searchContentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        viewSet();
        drawerSet();
        eventSet();
        initSet();
        netWork();
    }



    /*
     * Find View & Listener
     * */

    DownActionView docAction;
    DownActionView searchAction;

    LinearLayout searchLinear;
    EditText search;
    ImageView searchButton;
    ImageButton searchUp;

    RecyclerView recyclerView;
    ChatingCycle cycle;

    LinearLayout chatLinear;
    LinearLayout inputLinear;
    EditText input;
    ImageButton add;
    ImageButton send;

    LinearLayout docLinear;
    LinearLayout file;
    LinearLayout focus;
    LinearLayout decision;

    void viewSet() {
        search = findViewById(R.id.search);
        searchButton = findViewById(R.id.search_button);
        searchLinear = findViewById(R.id.searchLinear);
        searchUp = findViewById(R.id.search_up);
        recyclerView = findViewById(R.id.recycle);
        chatLinear = findViewById(R.id.chatLinear);
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
    }

    void eventSet() {
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
        search.setOnKeyListener(new View.OnKeyListener() {

            /*
             * Search Contents
             * */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    new KeyboardManager().hide(ChatingActivity.this, search);
                    return true;
                }
                return false;
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
                goToSearchPosition();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
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
                goToSearchPosition();
            }
        });

    }



    /*
     * Left Drawer Pager
     * */

    Nav navView = new Nav();

    void drawerSet() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_view, navView).addToBackStack(null).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setUserList(userList);
        navView.setDocumentList(documentList);
        navView.setDecisionList(decisionList);
        navView.setFocusList(focusList);
    }



    /*
     * Extra & Initial Setting
     * */

    public Project project;
    public ChatRoomVO chatRoomVO;
    public List<UserInfos> projectJoinUserInfo;

    void initSet() {

        projectJoinUserInfo = new ArrayList<>();

        int downDocDP = (int) ConvertDpPixels.convertDpToPixel(100, this);
        docAction = new DownActionView(downDocDP);

        int downSearchDP = (int) ConvertDpPixels.convertDpToPixel(28, this);
        searchAction = new DownActionView(downSearchDP);


        cycle = new ChatingCycle(chatContentsList, documentList, decisionList, focusList);
        cycle.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);



        project = new Gson().fromJson(getIntent().getStringExtra("project"), Project.class);
        chatRoomVO = new Gson().fromJson(getIntent().getStringExtra("chatRoomVO"), ChatRoomVO.class);


        if (project.getPpermission().equals(Projects.MEMBER) || chatRoomVO.getCrmode() != 1) {
            focus.setVisibility(GONE);
        }

        if (chatRoomVO.getCrmode() == 3) {
            recyclerView.setBackgroundColor(0xff000000);
            navView.setFocusVisibleGone(true);
            cycle.setModeFocus();
        }

        if (chatRoomVO.getCrclose() == 1) {
            chatLinear.setVisibility(GONE);
        }

    }



    /*
     * NetWork Init
     * */

    public StompBuilder stompBuilder;

    public ChatRoomJoinService chatRoomJoinService;
    public DocumentService documentService;
    public ChatRoomService focusService;
    public DecisionService decisionService;
    public ChatContentsService chatContentsService;
    public ProjectJoinService projectJoinService;

    void netWork() {

        // Query Reference Data
        int crcode = chatRoomVO.getCrcode();

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





    /*
     * Service
     * */

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
                search.requestFocus();
                inputLinear.setBackgroundResource(R.color.colorWhiteGray);
            } else {
                new KeyboardManager().hide(this, search);
                input.setEnabled(true);
                input.requestFocus();
                inputLinear.setBackgroundResource(R.color.colorWhite);
            }
        }
        return super.onOptionsItemSelected(item);
    }




    /*
     * Business
     * */

    void addCollabor() {
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

    void checkUserAdd(UserInfos userInfos) {
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

    void goToSearchPosition() {
        if (searchContentsList.size() > 0) {
            recyclerView.scrollToPosition(chatContentsList.indexOf(searchContentsList.get(searchContentsList.size() - 1)));
            searchContentsList.remove(searchContentsList.size() - 1);
        }
    }

    void refresh() {
        if (recyclerView.isShown()) {
            cycle.notifyDataSetChanged();
            navView.notifyDataSetChanged();
            recyclerView.scrollToPosition(cycle.getItemCount() - 1);
        } else {

        }
    }




    /*
     * Add Document
     * */

    void addDoc() {
        docAction.animationClose(docLinear);
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

    void addDec() {
        docAction.animationClose(docLinear);
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

    void addFoc() {
        docAction.animationClose(docLinear);

        FocusCreateDialog.ChatRoomCreateListener listener
                = new FocusCreateDialog.ChatRoomCreateListener() {
            @Override
            public void getParameter(ChatRoomVO vo) {
                focusService.insertMulti(project.getPcode(), userList, vo);
                focusService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.CHATROOM, vo);

                        // 채팅 전송
                        ChatContentsVO chatContentsVO = new ChatContentsVO();
                        chatContentsVO.setCrcode(chatRoomVO.getCrcode());
                        chatContentsVO.setCrcoderef(vo.getCrcode());
                        chatContentsVO.setCcontents(vo.getFctitle());
                        chatContentsVO.setUid(UserInfo.getUid());
                        chatContentsService.insert(chatContentsVO);
                    }
                });
            }
        };

        new FocusCreateDialog(this)
                .setChatRoomCreateListener(listener)
                .setPcoce(project.getPcode())
                .setCrcode(chatRoomVO.getCrcode())
                .showDecisionParameter();
    }




    /*
     * Click Contents
     * */

    @Override
    public void onDocClick(View view, DocumentVO vo) {

        DialogDocParam.FileUpdateListener listener =
                new DialogDocParam.FileUpdateListener() {
                    @Override
                    public void update(DocumentVO vo) {
                        stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.DOCUMENT, vo);
                    }

                    @Override
                    public void insert(DocumentVO vo) {
                        stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.DOCUMENT, vo);
                    }
                };

        new FileDownLoadServiceDialog(ChatingActivity.this)
        .setVo(vo)
        .setPcode(project.getPcode())
        .setCrmode(chatRoomVO.getCrmode())
        .setPermision(project.getPpermission())
        .setFileUpdateListener(listener)
        .showFileData();
    }

    @Override
    public void onDecClick(View view, DecisionVO vo) {
        DecisionItemSelectDialog.DecisionEventListener listener
                = new DecisionItemSelectDialog.DecisionEventListener() {
            @Override
            public void update() {
                stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.DECISION, vo);
            }
        };
        new DecisionItemSelectDialog(this, vo)
                .setPcode(project.getPcode())
                .setCrmode(chatRoomVO.getCrmode())
                .setPermision(project.getPpermission())
                .setDecisionEventListener(listener)
                .showList();
    }

    @Override
    public void onChaClick(View view, ChatRoomVO vo) {
        FocusItemSelectDialog.FocusEventListener listener
                = new FocusItemSelectDialog.FocusEventListener() {
            @Override
            public void event() {
                stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.CHATROOM, vo);
            }
        };

        new FocusItemSelectDialog(this, vo)
                .setFocusEventListener(listener)
                .setProject(project)
                .showSelectDialog();
    }





    /*
     * NetWork Service
     * */

    @Override
    public void work(Object... objects) {

        // ChatContentService 에서 보낸 이벤트
        if (objects != null && objects.length > 0) {
            stompBuilder.sendMessage(StompBuilder.INSERT, StompBuilder.CHATCONTENTS, objects[0]);
            return;
        }

        refresh();
    }

    void sendMessage(String msg) {
        ChatContentsVO vo = new ChatContentsVO();
        vo.setCrcode(chatRoomVO.getCrcode());
        vo.setCcontents(msg);
        vo.setUid(UserInfo.getUid());

        chatContentsService.insert(vo);
        input.setText("");
    }


    // Stomp Subscribe
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

    // Stomp Subscribe Process
    void insertTopic(String type, String object) {
        switch (type) {
            case StompBuilder.PROJECTJOIN:
                projectJoinService.selectProjectJoinUsers(project.getPcode());
                projectJoinService.work(null);
                break;
            case StompBuilder.CHATCONTENTS:
                ChatContentsVO contentsVo = new Gson().fromJson(object, ChatContentsVO.class);
                if (contentsVo.getCrcode() != chatRoomVO.getCrcode()) return;
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
                if (docVo.getCrcode() != chatRoomVO.getCrcode()) return;

                documentList.add(docVo);
                refresh();
                break;
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                if (decVo.getCrcode() != chatRoomVO.getCrcode()) return;

                decisionList.add(decVo);
                refresh();
                break;
            case StompBuilder.CHATROOM:
                ChatRoomVO chatVo = new Gson().fromJson(object, ChatRoomVO.class);
                focusList.add(chatVo);
                refresh();
                break;
        }
    }

    void updateTopic(String type, String object) {
        switch (type) {
            case StompBuilder.CHATROOM :
                ChatRoomVO chatVo = new Gson().fromJson(object, ChatRoomVO.class);
                for (ChatRoomVO vo : focusList){
                    if (vo.getCrcode() == chatVo.getCrcode()){
                        if (chatVo.getCrclose() == 1){
                            vo.setCrclose(chatVo.getCrclose());
                            Toast.makeText(this, "(회의) \"" + vo.getFctitle() + "\" 가 종료 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                            refresh();
                            break;
                        } else {
                            vo.setTcode(chatVo.getTcode());
                            Toast.makeText(this, "(투표) \"" + vo.getFctitle() + "\" 의 업무 위치가 변경 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                for (Chating chat : chatContentsList) {
                    if (chat.getChatContentsVO().getCrcoderef() == chatVo.getCrcode()) {
                        recyclerView.scrollToPosition(chatContentsList.indexOf(chat));
                        break;
                    }
                }
                break;
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                if (decVo.getCrcode() != chatRoomVO.getCrcode()) return;
                for (DecisionVO vo : decisionList) {
                    if (vo.getDscode() == decVo.getDscode()) {
                        if (decVo.getDsclose() == 1){
                            vo.setDsclose(decVo.getDsclose());
                            Toast.makeText(this, "(투표) \"" + vo.getDstitle() + "\" 가 종료 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            vo.setTcode(decVo.getTcode());
                            Toast.makeText(this, "(투표) \"" + vo.getDstitle() + "\" 의 업무 위치가 변경 되었습니다."
                                    , Toast.LENGTH_SHORT).show();
                        }
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
            case StompBuilder.DOCUMENT:
                DocumentVO docVo = new Gson().fromJson(object, DocumentVO.class);
                Toast.makeText(this, "(자료) \"" + docVo.getDmtitle() + "\" 의 업무 위치가 변경 되었습니다."
                        , Toast.LENGTH_SHORT).show();

                for (Chating chat : chatContentsList) {
                    if (chat.getChatContentsVO().getDmcode() == docVo.getDmcode()) {
                        recyclerView.scrollToPosition(chatContentsList.indexOf(chat));
                        break;
                    }
                }
                break;
            case StompBuilder.PROJECTJOIN:
                ProjectJoinVO projectJoinVO = new Gson().fromJson(object, ProjectJoinVO.class);
                if (chatRoomVO.getCrmode() != 1) return;

                for (UserInfos user : projectJoinUserInfo) {
                    if (projectJoinVO.getUid().equals(user.getId())) {
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

    void deleteTopic(String type, String object) {

    }

}
