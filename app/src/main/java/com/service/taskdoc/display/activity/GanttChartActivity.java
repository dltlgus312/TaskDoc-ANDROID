package com.service.taskdoc.display.activity;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.ChatRoomInfo;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;
import com.service.taskdoc.display.custom.custom.chart.ChartDataSetting;
import com.service.taskdoc.display.custom.custom.chart.DocOnTheBarItem;
import com.service.taskdoc.display.custom.custom.dialog.chat.FocusItemSelectDialog;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionItemSelectDialog;
import com.service.taskdoc.display.custom.custom.dialog.file.DialogDocParam;
import com.service.taskdoc.display.custom.custom.dialog.file.FileDownLoadServiceDialog;
import com.service.taskdoc.display.custom.custom.dialog.memo.DialogMemoList;
import com.service.taskdoc.display.custom.ganttchart.BarItem;
import com.service.taskdoc.display.custom.ganttchart.Data;
import com.service.taskdoc.display.custom.ganttchart.GanttChart;
import com.service.taskdoc.display.custom.custom.chart.TaskBarItem;
import com.service.taskdoc.display.custom.ganttchart.OnTheBarItem;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.StompBuilder;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.service.ConvertDpPixels;
import com.service.taskdoc.service.system.support.service.DownActionView;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GanttChartActivity extends AppCompatActivity implements
        GanttChart.SeekBarListener, GanttChart.OnTheBarDrawClickListener, GanttChart.OnTheChartDrawListener,
        TextWatcher, View.OnClickListener, StompBuilder.SubscribeListener, View.OnKeyListener, GanttChart.OnBarClickListener, GanttChart.OnBarClickModifyListener {

    private Project project;

    private ChatRoomInfo chatRoomInfo;

    private Tasks tasks;

    private List<DocumentVO> documents;
    private List<DecisionVO> decisions;
    private List<ChatRoomVO> chatrooms;

    private List<TaskBarItem> bars;
    private List<DocOnTheBarItem> onTheBars;


    private LinearLayout todayFab;
    private LinearLayout searchFab;
    private FloatingActionButton listFab;
    private Spinner spinner;
    private LinearLayout searchBar;
    private EditText search;
    private GanttChart ganttChart;
    private ImageView searchButton;

    private SeekBar seekBar;

    private DownActionView downActionView;

    private StompBuilder stompBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gantt_chart);

        listFab = findViewById(R.id.listFab);
        todayFab = findViewById(R.id.todayFab);
        searchFab = findViewById(R.id.searchFab);
        spinner = findViewById(R.id.spinner);
        searchBar = findViewById(R.id.search_bar);
        search = findViewById(R.id.search);
        ganttChart = findViewById(R.id.ganttchart);
        seekBar = findViewById(R.id.hSeekbar);
        searchButton = findViewById(R.id.search_button);

        bannerText = new Paint();
        bannerText.setColor(Color.WHITE);

        searchBars = new ArrayList<>();
        searchOnTheBars = new ArrayList<>();

        init();
        fabEvent();
        seekBarEvent();
    }

    public void init() {
        seekBar.setVisibility(View.INVISIBLE);
        todayFab.setVisibility(View.INVISIBLE);
        searchFab.setVisibility(View.INVISIBLE);
        searchBar.setVisibility(View.GONE);

        seekBar.setMax((int) (GanttChart.MAXWIDTH - GanttChart.MINWIDTH));
        seekBar.setProgress((int) ganttChart.getIntervalWidth());

        search.addTextChangedListener(this);
        searchButton.setOnClickListener(this);
        search.setOnKeyListener(this);

        int searchBarDP = (int) ConvertDpPixels.convertDpToPixel(40, this);
        downActionView = new DownActionView(searchBarDP);

        // 인텐트 데이터 파싱
        this.project = new Gson().fromJson(getIntent().getStringExtra("project"), Project.class);
        this.chatRoomInfo = new Gson().fromJson(getIntent().getStringExtra("chatroom"), ChatRoomInfo.class);

        tasks = new Tasks();
        Type puTasks = new TypeToken<ArrayList<Task>>() {
        }.getType();
        Type prTasks = new TypeToken<ArrayList<Task>>() {
        }.getType();
        tasks.setPublicTasks(new Gson().fromJson(getIntent().getStringExtra("puTasks"), puTasks));
        tasks.setPrivateTasks(new Gson().fromJson(getIntent().getStringExtra("prTasks"), prTasks));

        Type documenttype = new TypeToken<ArrayList<DocumentVO>>() {
        }.getType();
        this.documents = new Gson().fromJson(getIntent().getStringExtra("documents"), documenttype);

        Type decisiontype = new TypeToken<ArrayList<DecisionVO>>() {
        }.getType();
        this.decisions = new Gson().fromJson(getIntent().getStringExtra("decisions"), decisiontype);

        Type chatroomtype = new TypeToken<ArrayList<ChatRoomVO>>() {
        }.getType();
        this.chatrooms = new Gson().fromJson(getIntent().getStringExtra("chatrooms"), chatroomtype);

        if (project != null){
            stompBuilder = new StompBuilder(project.getPcode());
            stompBuilder.setSubscribeListener(this);
            stompBuilder.connect();
        }
        chartSetting();
    }

    @Override
    public void onBackPressed() {
        if (downActionView.isOpen()) {
            downActionView.animationClose(searchBar);
        } else if (ganttChart.isClickedItem()) {
            ganttChart.closeClickeditem();
        } else {
            if (project != null)
            stompBuilder.disconnect();
            finish();
        }
    }

    /*
     * GanttChart Data Setting
     * */

    private ChartDataSetting chartDataSetting;

    public void chartSetting() {

        if (project != null){
            Calendar projectMinDate = Calendar.getInstance();
            String[] date = project.getPsdate().split("-");
            projectMinDate.set(
                    Integer.parseInt(date[0]),
                    Integer.parseInt(date[1])-1,
                    Integer.parseInt(date[2])
            );
            ganttChart.setSeekBarListener(this);
            ganttChart.setOnBarClickListener(this);
            ganttChart.setOnTheBarDrawClickListener(this);
            ganttChart.setOnTheChartDrawListener(this);
            ganttChart.setOnBarClickModifyListener(this);
            ganttChart.setMinDate(projectMinDate);
            ganttChart.setParentsOverMoveImpossible(true);

            chartDataSetting = new ChartDataSetting();
            chartDataSetting.setProject(project)
                    .setTasks(tasks)
                    .setDocuments(documents)
                    .setDecisions(decisions)
                    .setChatrooms(chatrooms)
                    .init();
        } else {
            ganttChart.setSeekBarListener(this);
            ganttChart.setOnBarClickListener(this);
            ganttChart.setOnTheBarDrawClickListener(this);
            ganttChart.setOnTheChartDrawListener(this);
            ganttChart.setOnBarClickModifyListener(this);
            ganttChart.setParentsOverMoveImpossible(true);

            chartDataSetting = new ChartDataSetting();
            chartDataSetting.setTasks(tasks)
                    .setDocuments(documents)
                    .setDecisions(decisions)
                    .setChatrooms(chatrooms)
                    .init();
        }


        chartDataSetting.setDataSettingListener(new ChartDataSetting.DataSettingListener() {
            @Override
            public void data(List<TaskBarItem> barList, List<DocOnTheBarItem> onTheBarItemList) {
                bars = barList;
                onTheBars = onTheBarItemList;

                Data data = new Data();
                data.setBars(bars);
                ganttChart.setData(data);
            }
        });
    }






    /*
     * Fab Event
     * */

    public void fabEvent() {

        listFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClick();
            }
        });

        todayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ganttChart.goToDay();
                fabClick();
            }
        });

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downActionView.animationDown(searchBar);
                fabClick();
            }
        });
    }

    public void fabClick() {
        if (!todayFab.isShown()) {
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.push_up_in1);
            Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.push_up_in2);
            todayFab.setVisibility(View.VISIBLE);
            searchFab.setVisibility(View.VISIBLE);
            todayFab.startAnimation(animation2);
            searchFab.startAnimation(animation1);
        } else {
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.push_down_out1);
            Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.push_down_out2);
            todayFab.setVisibility(View.INVISIBLE);
            searchFab.setVisibility(View.INVISIBLE);
            todayFab.startAnimation(animation2);
            searchFab.startAnimation(animation1);
        }
    }

    public void fabHide() {
        if (todayFab.isShown()) {
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.push_down_out1);
            Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.push_down_out2);
            todayFab.setVisibility(View.INVISIBLE);
            searchFab.setVisibility(View.INVISIBLE);
            todayFab.startAnimation(animation2);
            searchFab.startAnimation(animation1);
        }
    }

    public void seekBarEvent() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int i, boolean b) {
                ganttChart.resetSeekbarCount();

                if (!ganttWidthEvent) {
                    ganttChart.setIntervalWidth(GanttChart.MINWIDTH + i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }




    /*
     * Seek Bar Event
     * */

    boolean ganttWidthEvent;

    @Override
    public void setSeekPosition(int width) {
        ganttWidthEvent = true;
        seekBar.setProgress((int) (width - GanttChart.MINWIDTH));
        ganttWidthEvent = false;
    }

    @Override
    public void showSeek() {
        if (seekBar.getVisibility() == View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
            seekBar.startAnimation(animation);
            seekBar.setVisibility(View.VISIBLE);
        }
        fabHide();
    }

    @Override
    public void hideSeek() {
        if (seekBar.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(GanttChartActivity.this, R.anim.alpha_reverse);
            seekBar.startAnimation(animation);
            seekBar.setVisibility(View.INVISIBLE);
        }
    }



    /*
     * Bar Click Listener
     * */

    @Override
    public void itemSelect(BarItem barItem, List<OnTheBarItem> onTheBarItemItems) {
        ArrayAdapter<String> list = new ArrayAdapter<>(
                this, android.R.layout.simple_selectable_list_item);

        for (OnTheBarItem onTheBarItem : onTheBarItemItems) {
            DocOnTheBarItem item = (DocOnTheBarItem) onTheBarItem;

            switch (item.getDocType()) {
                case DocOnTheBarItem.DOCUMENT:
                    list.add("파일 :" + item.getDocumentVO().getDmtitle());
                    break;
                case DocOnTheBarItem.DECISION:
                    list.add("투표 :" + item.getDecisionVO().getDstitle());
                    break;
                case DocOnTheBarItem.CHATROOM:
                    list.add("회의 :" + item.getChatRoomVO().getFctitle());
                    break;
            }
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("기록된 자료");
        builder.setAdapter(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocOnTheBarItem item = (DocOnTheBarItem) onTheBarItemItems.get(i);
                switch (item.getDocType()) {
                    case DocOnTheBarItem.DOCUMENT:
                        docClick(item.getDocumentVO());
                        break;
                    case DocOnTheBarItem.DECISION:
                        decClick(item.getDecisionVO());
                        break;
                    case DocOnTheBarItem.CHATROOM:
                        chaClick(item.getChatRoomVO());
                        break;
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @Override
    public void itemSelect(BarItem barItem) {
        barItem.setHighlightCount();

        TaskBarItem item = (TaskBarItem) barItem;
        if (item.getTask().getReftcode() != 0){
            new DialogMemoList(this)
                    .setTask(item.getTask())
                    .init();
        }

    }

    @Override
    public void itemModifyClose(List<BarItem> items) {
        List<Task> publicVos = new ArrayList<>();
        List<Task> privateVos = new ArrayList<>();


        for (BarItem item : items){
            TaskBarItem i = (TaskBarItem) item;
            if (i.getTask().getReftcode() == 0){
                publicVos.add(i.getTask());
            }else {
                privateVos.add(i.getTask());
            }
        }

        if (publicVos.size()>0){
            PublicTaskService service = new PublicTaskService();
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.PUBLICTASK, objects[0]);
                }
            });
            service.updateList(publicVos);
        }

        if (privateVos.size()>0){
            PrivateTaskService service = new PrivateTaskService();
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    if (project != null)
                        stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.PRIVATETASK, objects[0]);
                }
            });
            service.updateList(privateVos);
        }

    }




    public void docClick(DocumentVO vo) {
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

        new FileDownLoadServiceDialog(this)
                .setVo(vo)
                .setPcode(project.getPcode())
                .setCrmode(chatRoomInfo.getChatRoomVO().getCrmode())
                .setPermision(project.getPpermission())
                .setFileUpdateListener(listener)
                .showFileData();
    }

    public void decClick(DecisionVO vo) {
        DecisionItemSelectDialog.DecisionEventListener listener
                = new DecisionItemSelectDialog.DecisionEventListener() {
            @Override
            public void update() {
                stompBuilder.sendMessage(StompBuilder.UPDATE, StompBuilder.DECISION, vo);
            }
        };
        new DecisionItemSelectDialog(this, vo)
                .setPcode(project.getPcode())
                .setCrmode(1)
                .setPermision(project.getPpermission())
                .setDecisionEventListener(listener)
                .showList();
    }

    public void chaClick(ChatRoomVO vo) {
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
     * GanttChart Background Custom Draw
     * */

    private Paint bannerText;
    private float size;
    private float sPosition = 0.43f;
    private float ePosition = 0.47f;
    private float vPosition = 0.85f;

    @Override
    public void drawBack(Canvas canvas) {

    }

    @Override
    public void drawFront(Canvas canvas) {
        if (ganttChart.getIntervalWidth() > GanttChart.CHANGEMODE) {

            if (size == 0) {
                size = (ganttChart.getHeight() * 0.125f) - (ganttChart.getHeight() * 0.1f);
                bannerText.setTextSize(size);
            }
            canvas.drawRect(ganttChart.getWidth() * sPosition, ganttChart.getHeight() * vPosition,
                    ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size, chartDataSetting.getDocColor());
            canvas.drawText("파일", ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size, bannerText);

            canvas.drawRect(ganttChart.getWidth() * sPosition, ganttChart.getHeight() * vPosition + size,
                    ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 2, chartDataSetting.getDecColor());
            canvas.drawText("투표", ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 2, bannerText);

            canvas.drawRect(ganttChart.getWidth() * sPosition, ganttChart.getHeight() * vPosition + size * 2,
                    ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 3, chartDataSetting.getChaColor());
            canvas.drawText("회의", ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 3, bannerText);
        }
    }





    /*
     * Input Search Listener
     * */

    private List<TaskBarItem> searchBars;
    private List<DocOnTheBarItem> searchOnTheBars;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        searchItem();
    }




    /*
     * Search Button Click Event
     * */

    @Override
    public void onClick(View view) {
        findItemHighLight();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            new KeyboardManager().hide(GanttChartActivity.this, search);
            return true;
        }
        return false;
    }

    public void findItemHighLight() {
        if (searchBars.size() > 0) {
            TaskBarItem item = searchBars.get(0);
            ganttChart.goToItem(item);
            searchBars.remove(item);
            item.setHighlightCount();
        } else if (searchOnTheBars.size() > 0) {
            DocOnTheBarItem item = searchOnTheBars.get(0);
            ganttChart.goToItem(item);
            searchOnTheBars.remove(item);
            item.setHighlightCount();
        } else searchItem();
    }

    public void searchItem() {
        String title = search.getText().toString();
        title.replaceAll(" ", "");
        if (title.equals("") || title.equals(" ")) return;

        searchBars.clear();
        searchOnTheBars.clear();

        String text = spinner.getSelectedItem().toString();

        if (text.equals("전체")){
            allSearch(title);
        } else if(text.equals("업무")){
            taskSearch(title);
        } else if (text.equals("자료")){
            docSearch(title);
        }


        if (searchBars.size() < 1 && searchOnTheBars.size() < 1) return;

        findItemHighLight();
    }

    public void allSearch(String title){
        for (TaskBarItem item : bars) {
            if (!item.isDivision() && item.getTitle().contains(title)) {
                searchBars.add(item);
            }
        }
        for (DocOnTheBarItem item : onTheBars){
            if (item.getDocType() == DocOnTheBarItem.DOCUMENT){
                if (item.getDocumentVO().getDmtitle().contains(title))
                    searchOnTheBars.add(item);
            } else if (item.getDocType() == DocOnTheBarItem.DECISION){
                if (item.getDecisionVO().getDstitle().contains(title))
                    searchOnTheBars.add(item);
            } else if (item.getDocType() == DocOnTheBarItem.CHATROOM){
                if (item.getChatRoomVO().getFctitle().contains(title))
                    searchOnTheBars.add(item);
            }
        }
    }

    public void taskSearch(String title){
        for (TaskBarItem item : bars) {
            if (!item.isDivision() && item.getTitle().contains(title)) {
                searchBars.add(item);
            }
        }
    }

    public void docSearch(String title){
        for (DocOnTheBarItem item : onTheBars){
            if (item.getDocType() == DocOnTheBarItem.DOCUMENT){
                if (item.getDocumentVO().getDmtitle().contains(title))
                    searchOnTheBars.add(item);
            } else if (item.getDocType() == DocOnTheBarItem.DECISION){
                if (item.getDecisionVO().getDstitle().contains(title))
                    searchOnTheBars.add(item);
            } else if (item.getDocType() == DocOnTheBarItem.CHATROOM){
                if (item.getChatRoomVO().getFctitle().contains(title))
                    searchOnTheBars.add(item);
            }
        }
    }





    /*
     * Network Service
     * */

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
            case StompBuilder.DOCUMENT:
                DocumentVO docVo = new Gson().fromJson(object, DocumentVO.class);
                if(docVo == null || docVo.getCrcode() != chatRoomInfo.getChatRoomVO().getCrcode()) return;
                documents.add(docVo);
                chartDataSetting.init();
                Toast.makeText(this, "(파일) \"" + docVo.getDmtitle() + "\" 가 추가 되었습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                if (decVo.getCrcode() != chatRoomInfo.getChatRoomVO().getCrcode()) return;
                decisions.add(decVo);
                chartDataSetting.init();
                Toast.makeText(this, "(투표) \"" + decVo.getDstitle() + "\" 가 추가 되었습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.CHATROOM :
                ChatRoomVO chatVo = new Gson().fromJson(object, ChatRoomVO.class);
                chatrooms.add(chatVo);
                chartDataSetting.init();
                Toast.makeText(this, "(회의) \"" + chatVo.getFctitle() + "\" 가 추가 되었습니다."
                        , Toast.LENGTH_SHORT).show();

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
                chartDataSetting.init();
                break;
            case StompBuilder.PUBLICTASKS :
                Type publicTaskType = new TypeToken<ArrayList<PublicTaskVO>>() {}.getType();
                List<PublicTaskVO> vos = new Gson().fromJson(object, publicTaskType);

                for (PublicTaskVO pvo : vos){
                    Task puT = new Task();
                    puT.setCode(pvo.getTcode());
                    puT.setTitle(pvo.getTtitle());
                    puT.setColor(pvo.getTcolor());
                    puT.setSdate(pvo.getTsdate());
                    puT.setEdate(pvo.getTedate());
                    puT.setPercent(pvo.getTpercent());
                    puT.setRefference(pvo.getTrefference());
                    puT.setSequence(pvo.getTsequence());
                    puT.setRefpcode(pvo.getPcode());

                    tasks.addSort(puT);
                }
                chartDataSetting.init();
                break;
        }
    }

    private void updateTopic(String type, String object) {
        switch (type) {
            case StompBuilder.DECISION:
                DecisionVO decVo = new Gson().fromJson(object, DecisionVO.class);
                if (decVo.getCrcode() != chatRoomInfo.getChatRoomVO().getCrcode()) return;

                DocOnTheBarItem decItem = findDecItem(decVo);
                if (decVo.getDsclose() == 1){
                    decItem.getDecisionVO().setDsclose(decVo.getDsclose());
                    decItem.setHighlightCount();
                    ganttChart.goToItem(decItem);
                    Toast.makeText(this, "(투표) \"" + decVo.getDstitle() + "\" 가 종료 되었습니다."
                            , Toast.LENGTH_SHORT).show();
                } else {
                    decItem.getDecisionVO().setTcode(decVo.getTcode());
                    chartDataSetting.init();
                    decItem.setHighlightCount();
                    Toast.makeText(this, "(투표) \"" + decVo.getDstitle() + "\" 의 업무 위치가 변경 되었습니다."
                            , Toast.LENGTH_SHORT).show();
                }

                break;

            case StompBuilder.CHATROOM :
                ChatRoomVO chatVo = new Gson().fromJson(object, ChatRoomVO.class);
                DocOnTheBarItem chatItem = findChaItem(chatVo);

                if (chatVo.getCrclose() == 1){
                    chatItem.getChatRoomVO().setCrclose(chatVo.getCrclose());
                    chatItem.setHighlightCount();
                    ganttChart.goToItem(chatItem);
                    Toast.makeText(this, "(회의) \"" + chatItem.getChatRoomVO().getFctitle() + "\" 가 종료 되었습니다."
                            , Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    chatItem.getChatRoomVO().setTcode(chatVo.getTcode());
                    chartDataSetting.init();
                    chatItem.setHighlightCount();
                    Toast.makeText(this, "(투표) \"" + chatItem.getChatRoomVO().getFctitle() + "\" 의 업무 위치가 변경 되었습니다."
                            , Toast.LENGTH_SHORT).show();
                }

                break;
            case StompBuilder.DOCUMENT:
                DocumentVO docVo = new Gson().fromJson(object, DocumentVO.class);
                DocOnTheBarItem docItem = findDocItem(docVo);
                docItem.getDocumentVO().setTcode(docVo.getTcode());
                chartDataSetting.init();
                docItem.setHighlightCount();
                Toast.makeText(this, "(자료) \"" + docVo.getDmtitle() + "\" 의 업무 위치가 변경 되었습니다."
                        , Toast.LENGTH_SHORT).show();
                break;
            case StompBuilder.PUBLICTASK :
                PublicTaskVO publicTaskVO = new Gson().fromJson(object, PublicTaskVO.class);
                for (TaskBarItem item : bars){
                    Task vo = item.getTask();
                    if (vo != null && vo.getReftcode() == 0 && vo.getCode() == publicTaskVO.getTcode()){
                        vo.setTitle(publicTaskVO.getTtitle());
                        vo.setSdate(publicTaskVO.getTsdate());
                        vo.setEdate(publicTaskVO.getTedate());
                        vo.setPercent(publicTaskVO.getTpercent());
                        vo.setColor(publicTaskVO.getTcolor());
                        item.init();
                    }
                }break;
        }
    }

    private void deleteTopic(String type, String object) {
        switch (type) {

            case StompBuilder.PUBLICTASK:

                break;
        }
    }



    /*
     * Item Find
     * */

    public DocOnTheBarItem findDecItem(DecisionVO vo) {
        for (DocOnTheBarItem i : onTheBars) {
            if (i.getDecisionVO() != null && i.getDecisionVO().getDscode() == vo.getDscode()) {
                return i;
            }
        }
        return null;
    }

    public DocOnTheBarItem findDocItem(DocumentVO vo) {
        for (DocOnTheBarItem i : onTheBars) {
            if (i.getDocumentVO() != null && i.getDocumentVO().getDmcode() == vo.getDmcode()) {
                return i;
            }
        }
        return null;
    }

    public DocOnTheBarItem findChaItem(ChatRoomVO vo) {
        for (DocOnTheBarItem i : onTheBars) {
            if (i.getChatRoomVO() != null && i.getChatRoomVO().getCrcode() == vo.getCrcode()) {
                return i;
            }
        }
        return null;
    }

}
