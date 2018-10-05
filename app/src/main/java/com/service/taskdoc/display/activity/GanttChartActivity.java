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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.custom.custom.chart.DocOnTheBarItem;
import com.service.taskdoc.display.custom.ganttchart.BarItem;
import com.service.taskdoc.display.custom.ganttchart.Data;
import com.service.taskdoc.display.custom.ganttchart.GanttChart;
import com.service.taskdoc.display.custom.custom.chart.TaskBarItem;
import com.service.taskdoc.display.custom.ganttchart.OnTheBarItem;
import com.service.taskdoc.service.system.support.ConvertDpPixels;
import com.service.taskdoc.service.system.support.DownActionView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GanttChartActivity extends AppCompatActivity implements GanttChart.SeekBarListener, GanttChart.OnTheBarDrawClickListener, GanttChart.OnTheChartDrawListener, TextWatcher, View.OnClickListener {

    private Project project;

    private Tasks tasks;

    private List<DocumentVO> documents;
    private List<DecisionVO> decisions;
    private List<ChatRoomVO> chatrooms;

    private List<TaskBarItem> bars;
    private List<DocOnTheBarItem> onTheBars;

    private List<TaskBarItem> searchBars;
    private List<DocOnTheBarItem> searchOnTheBars;

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


    private Paint docColor;
    private Paint decColor;
    private Paint chaColor;
    private Paint bannerText;
    private float size;
    private float sPosition = 0.43f;
    private float ePosition = 0.47f;
    private float vPosition = 0.85f;

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

        docColor = new Paint();
        decColor = new Paint();
        chaColor = new Paint();
        bannerText = new Paint();

        docColor.setColor(0xff7fff00);
        decColor.setColor(0xfffff007);
        chaColor.setColor(0xff007fff);
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

        int searchBarDP = (int) ConvertDpPixels.convertDpToPixel(40, this);
        downActionView = new DownActionView(searchBarDP);

        ganttChart.setSeekBarListener(this);
        ganttChart.setOnTheBarDrawClickListener(this);
        ganttChart.setOnTheChartDrawListener(this);

        // 인텐트 데이터 파싱
        this.project = new Gson().fromJson(getIntent().getStringExtra("project"), Project.class);

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

        Type decisiontype = new TypeToken<ArrayList<DocumentVO>>() {
        }.getType();
        this.decisions = new Gson().fromJson(getIntent().getStringExtra("decisions"), decisiontype);

        Type chatroomtype = new TypeToken<ArrayList<DocumentVO>>() {
        }.getType();
        this.chatrooms = new Gson().fromJson(getIntent().getStringExtra("chatrooms"), chatroomtype);

        chartDataSetting();
    }



    /*
     * GanttChart Data Setting
     * */

    public void chartDataSetting() {
        bars = new ArrayList<>();
        onTheBars = new ArrayList<>();

        if (tasks.getPublicTasks().size() == 0) {
            // 개인업무 리스트에서 호출할 경우
            int tcode = 0;
            for (Task t : tasks.getPrivateTasks()) {
                if (tcode != t.getReftcode()) {
                    bars.add(new TaskBarItem(true)); // 구분자
                    tcode = t.getReftcode();
                }
                bars.add(new TaskBarItem(t));
            }
        } else {
            // 공용업무 리스트에서 호출할 경우
            for (Task t : tasks.getPublicTasks()) {
                if (t.getSdate() == null) continue;
                if (t.getCode() == t.getRefference()) {
                    bars.add(new TaskBarItem(true)); // 구분자
                }

                TaskBarItem item = new TaskBarItem(t);

                setDocItem(item); // 자료 추가

                if (project.getPpermission().equals(Projects.MEMBER)) {
                    item.setClickable(false);   // 권한
                }

                bars.add(item);

                for (Task tt : tasks.getPrivateTasks()) {
                    if (t.getCode() == tt.getReftcode()) {
                        bars.add(new TaskBarItem(tt));
                    }
                }
            }
            arrowHierarchy(); // 계층 화살표
        }

        Data data = new Data();
        data.setBars(bars);
        ganttChart.setData(data);
    }

    public void setDocItem(TaskBarItem item) {
        if (documents != null) {
            for (DocumentVO vo : this.documents) {
                if (item.getTask().getCode() == vo.getTcode()) {
                    DocOnTheBarItem docOnBarItem = new DocOnTheBarItem(vo);
                    docOnBarItem.setPaint(docColor);
                    item.addOnTheBarDraws(docOnBarItem);
                    onTheBars.add(docOnBarItem);
                }
            }
        }

        if (decisions != null) {
            for (DecisionVO vo : this.decisions) {
                if (item.getTask().getCode() == vo.getTcode()) {
                    DocOnTheBarItem docOnBarItem = new DocOnTheBarItem(vo);
                    docOnBarItem.setPaint(decColor);
                    item.addOnTheBarDraws(docOnBarItem);
                    onTheBars.add(docOnBarItem);
                }
            }
        }

        if (chatrooms != null) {
            for (ChatRoomVO vo : this.chatrooms) {
                if (item.getTask().getCode() == vo.getTcode()) {
                    DocOnTheBarItem docOnBarItem = new DocOnTheBarItem(vo);
                    docOnBarItem.setPaint(chaColor);
                    item.addOnTheBarDraws(docOnBarItem);
                    onTheBars.add(docOnBarItem);
                }
            }
        }
    }

    public void arrowHierarchy() {
        for (int i = 0; i < bars.size(); i++) {
            TaskBarItem parentsItem = bars.get(i);
            if (parentsItem.isDivision()) continue;

            for (int j = 0; j < bars.size(); j++) {
                TaskBarItem childItem = bars.get(j);
                if (childItem == parentsItem || childItem.isDivision()) continue;

                if (childItem.getTask().getReftcode() == 0 && parentsItem.getTask().getCode() == childItem.getTask().getRefference()
                        || childItem.getTask().getReftcode() != 0 && parentsItem.getTask().getCode() == childItem.getTask().getReftcode()) {
                    parentsItem.addArrowList(j);
                    childItem.setDownDepth(parentsItem.getDepthArrow() - 1);
                }
            }
        }
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
     * OnTheBarDraw Click Listener
     * */

    @Override
    public void itemSelect(BarItem barItem, List<OnTheBarItem> onTheBarItemItems) {
        ArrayAdapter<String> list = new ArrayAdapter<>(
                this, android.R.layout.simple_selectable_list_item);

        for (OnTheBarItem onTheBarItem : onTheBarItemItems) {
            DocOnTheBarItem item = (DocOnTheBarItem) onTheBarItem;

            switch (item.getDocType()) {
                case DocOnTheBarItem.DOCUMENT:
                    list.add("파일 : " + item.getDocumentVO().getDmtitle());
                    break;
                case DocOnTheBarItem.DECISION:
                    list.add("투표 : " + item.getDecisionVO().getDstitle());
                    break;
                case DocOnTheBarItem.CHATROOM:
                    list.add("회의 : " + item.getChatRoomVO().getFctitle());
                    break;
            }
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("기록된 자료");
        builder.setAdapter(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }



    /*
     * GanttChart Background Custom Draw
     * */

    @Override
    public void drawBack(Canvas canvas) {

    }

    @Override
    public void drawFront(Canvas canvas){
        if (ganttChart.getIntervalWidth() > GanttChart.CHANGEMODE) {

            if (size == 0) {
                size = (ganttChart.getHeight() * 0.125f) - (ganttChart.getHeight() * 0.1f);
                bannerText.setTextSize(size);
            }
            canvas.drawRect(ganttChart.getWidth() * sPosition, ganttChart.getHeight() * vPosition,
                    ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size, docColor);
            canvas.drawText("파일", ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size, bannerText);

            canvas.drawRect(ganttChart.getWidth() * sPosition, ganttChart.getHeight() * vPosition + size,
                    ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 2, decColor);
            canvas.drawText("투표", ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 2, bannerText);

            canvas.drawRect(ganttChart.getWidth() * sPosition, ganttChart.getHeight() * vPosition + size * 2,
                    ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 3, chaColor);
            canvas.drawText("회의", ganttChart.getWidth() * ePosition, ganttChart.getHeight() * vPosition + size * 3, bannerText);
        }
    }



    /*
     * Input Search Listener
     * */

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

    public void findItemHighLight(){
        if (searchBars.size() > 0) {
            TaskBarItem item = searchBars.get(0);
            ganttChart.setFloodingX(-(searchBars.get(0).getLeft() - ganttChart.getWidth() / 3));
            ganttChart.setFloodingY(-(searchBars.get(0).getTop() - ganttChart.getHeight() / 3));
            searchBars.remove(item);
        } else if (searchOnTheBars.size() > 0) {
            DocOnTheBarItem item = searchOnTheBars.get(0);
            ganttChart.setFloodingX(-(searchBars.get(0).getLeft() - ganttChart.getWidth() / 3));
            ganttChart.setFloodingY(-(searchBars.get(0).getTop() - ganttChart.getHeight() / 3));
            searchBars.remove(item);
        } else searchItem();
    }

    public void searchItem() {
        String title = search.getText().toString();
        title.replaceAll(" ", "");
        if (title.equals("") || title.equals(" ")) return;

        searchBars.clear();
        for (TaskBarItem item : bars) {
            if (!item.isDivision() && item.getTitle().contains(title)) {
                searchBars.add(item);
            }
        }

        if (searchBars.size() < 1) return;

        findItemHighLight();
    }
}
