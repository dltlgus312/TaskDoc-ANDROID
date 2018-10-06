package com.service.taskdoc.display.custom.custom.chart;

import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.data.ChartData;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.ProjectVO;

import java.util.ArrayList;
import java.util.List;

public class ChartDataSetting {

    /*
    * 필수 데이터
    * */
    private List<TaskBarItem> bars;
    private List<DocOnTheBarItem> onTheBars;

    private Tasks tasks;
    private List<DocumentVO> documents;
    private List<DecisionVO> decisions;
    private List<ChatRoomVO> chatrooms;

    private Project project;




    /*
    * 선택 데이터
    * */
    private Paint docColor;
    private Paint decColor;
    private Paint chaColor;


    // 일반적인 업무차트
    public ChartDataSetting() {
    }


    // 파일업로드 업무 선택을 위한 생성자...
    private int pcode;
    public ChartDataSetting(int pcode){
        this.pcode = pcode;
    }



    public void init(){

        bars = new ArrayList<>();
        onTheBars = new ArrayList<>();

        docColor = new Paint();
        decColor = new Paint();
        chaColor = new Paint();

        docColor.setColor(0xff7fff00);
        decColor.setColor(0xfffff007);
        chaColor.setColor(0xff007fff);

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

                if (pcode == 0 && project != null) {
                    if (project.getPpermission().equals(Projects.MEMBER)) {
                        item.setClickable(false);   // 권한
                    }
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


        if (dataSettingListener != null){
            dataSettingListener.data(bars, onTheBars);
            dataSettingListener.color(docColor, decColor, chaColor);
        }
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



    public List<TaskBarItem> getBars() {
        return bars;
    }

    public void setBars(List<TaskBarItem> bars) {
        this.bars = bars;
    }

    public List<DocOnTheBarItem> getOnTheBars() {
        return onTheBars;
    }

    public void setOnTheBars(List<DocOnTheBarItem> onTheBars) {
        this.onTheBars = onTheBars;
    }

    public Paint getDocColor() {
        return docColor;
    }

    public void setDocColor(Paint docColor) {
        this.docColor = docColor;
    }

    public Paint getDecColor() {
        return decColor;
    }

    public void setDecColor(Paint decColor) {
        this.decColor = decColor;
    }

    public Paint getChaColor() {
        return chaColor;
    }

    public void setChaColor(Paint chaColor) {
        this.chaColor = chaColor;
    }






    /*
    * 필수 데이터
    * */
    public ChartDataSetting setTasks(Tasks tasks) {
        this.tasks = tasks;
        return this;
    }

    public ChartDataSetting setDocuments(List<DocumentVO> documents) {
        this.documents = documents;
        return this;
    }

    public ChartDataSetting setDecisions(List<DecisionVO> decisions) {
        this.decisions = decisions;
        return this;
    }

    public ChartDataSetting setChatrooms(List<ChatRoomVO> chatrooms) {
        this.chatrooms = chatrooms;
        return this;
    }

    public ChartDataSetting setProject(Project project) {
        this.project = project;
        return this;
    }





    /*
     * 리스너
     * */
    private DataSettingListener dataSettingListener;

    public DataSettingListener getDataSettingListener() {
        return dataSettingListener;
    }

    public void setDataSettingListener(DataSettingListener dataSettingListener) {
        this.dataSettingListener = dataSettingListener;
        if (bars != null && bars.size() > 0){
            dataSettingListener.data(bars, onTheBars);
            dataSettingListener.color(docColor, decColor, chaColor);
        }
    }

    public interface DataSettingListener{
        void data(List<TaskBarItem> bars, List<DocOnTheBarItem> onTheBarItems);
        void color(Paint docColor, Paint decColor, Paint chaColor);
    }
}
