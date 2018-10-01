package com.service.taskdoc.display.custom.custom;

import android.graphics.Color;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.display.custom.ganttchart.BarItem;

import java.util.Calendar;

public class TaskBarItem extends BarItem {

    private Task task;

    public TaskBarItem(Task task){
        this.task = task;
        init();
    }

    public TaskBarItem(boolean division){
        super(division);
    }

    @Override
    protected void updatePercent(int percent) {
        task.setPercent(percent);
    }

    public void init(){

        setTitle(task.getTitle());
        setPercent(task.getPercent());
        setBarColor(task.getColor());

        String[] sDate = task.getSdate().split("-");
        Calendar sCal = Calendar.getInstance();
        int y = Integer.parseInt(sDate[0]);
        int m = Integer.parseInt(sDate[1]) - 1;
        int d = Integer.parseInt(sDate[2]);
        sCal.set(y, m, d);
        setSdate(sCal);

        String[] eDate = task.getEdate().split("-");
        Calendar eCal = Calendar.getInstance();
        int ey = Integer.parseInt(eDate[0]);
        int em = Integer.parseInt(eDate[1]) - 1;
        int ed = Integer.parseInt(eDate[2]);
        eCal.set(ey, em, ed);
        setEdate(eCal);

        if (task.getReftcode() != 0) super.setTextColor(Color.GREEN);
    }

    public Task getTask() {
        return task;
    }

}
