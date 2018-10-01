package com.service.taskdoc.display.custom.custom;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.display.custom.ganttchart.GanttChart;
import com.service.taskdoc.display.custom.ganttchart.OnTheBarItem;

import java.util.Calendar;

public class DocOnTheBarItem extends OnTheBarItem {

    private DocumentVO documentVO;
    private DecisionVO decisionVO;
    private ChatRoomVO chatRoomVO;

    private Paint paint;




    public final static int DOCUMENT = 0;
    public final static int DECISION = 1;
    public final static int CHATROOM = 2;

    private int docType;

    public DocOnTheBarItem(DocumentVO vo) {
        this.documentVO = vo;
        this.docType = DOCUMENT;
    }
    public DocOnTheBarItem(DecisionVO vo) {
        this.decisionVO = vo;
        this.docType = DECISION;
    }
    public DocOnTheBarItem(ChatRoomVO vo) {
        this.chatRoomVO = vo;
        this.docType = CHATROOM;
    }

    @Override
    protected void setdate(Calendar sdate, Calendar edate) {
        String[] date = null;

        int Y, M, D;

        switch (docType){
            case DOCUMENT :
                date = documentVO.getDmdate().split("-");
                break;
            case DECISION :
                date = decisionVO.getDsdate().split("-");
                break;
            case CHATROOM :
                date = chatRoomVO.getCrdate().split("-");
                break;
        }

        if (date == null) return;

        Y = Integer.parseInt(date[0]);
        M = Integer.parseInt(date[1]) - 1;
        D = Integer.parseInt(date[2]);

        sdate.set(Y, M, D);
        edate.set(Y, M, D + 1);
    }

    @Override
    protected void drawItem(float left, float top, float right, float bottom, Canvas canvas) {

        float width = right - left;
        float margin = width / 10 ;
        float interval = width / 3;

        if (width * GanttChart.TOPWIDTHWEEKUNIT < GanttChart.CHANGEMODE){
            setClickable(false);
            return;
        }
        setClickable(true);

        switch (docType){
            case DOCUMENT :
                canvas.drawRect(left + margin, top + margin, left + interval, bottom-margin, paint);
                break;
            case DECISION :
                canvas.drawRect(left + interval, top + margin, left + interval * 2, bottom-margin, paint);
                break;
            case CHATROOM :
                canvas.drawRect(left + interval * 2, top + margin, right - margin, bottom-margin, paint);
                break;
        }
    }



    public DocumentVO getDocumentVO() {
        return documentVO;
    }

    public void setDocumentVO(DocumentVO documentVO) {
        this.documentVO = documentVO;
    }

    public DecisionVO getDecisionVO() {
        return decisionVO;
    }

    public void setDecisionVO(DecisionVO decisionVO) {
        this.decisionVO = decisionVO;
    }

    public ChatRoomVO getChatRoomVO() {
        return chatRoomVO;
    }

    public void setChatRoomVO(ChatRoomVO chatRoomVO) {
        this.chatRoomVO = chatRoomVO;
    }

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
