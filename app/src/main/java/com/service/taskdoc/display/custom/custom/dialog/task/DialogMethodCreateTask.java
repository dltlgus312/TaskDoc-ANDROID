package com.service.taskdoc.display.custom.custom.dialog.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.MethodBoardVO;
import com.service.taskdoc.display.custom.custom.chart.ChartDataSetting;
import com.service.taskdoc.display.custom.custom.chart.DocOnTheBarItem;
import com.service.taskdoc.display.custom.custom.chart.TaskBarItem;
import com.service.taskdoc.display.custom.ganttchart.Data;
import com.service.taskdoc.display.custom.ganttchart.GanttChart;
import com.service.taskdoc.service.network.restful.service.MethodBoardService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogMethodCreateTask extends AlertDialog.Builder {

    Context context;

    MethodBoardService methodBoardService;

    PublicTaskService publicTaskService;

    Tasks tasks;

    GanttChart ganttChart;

    public DialogMethodCreateTask(Context context){
        super(context);
        this.context = context;
        setTitle("업무를 수정해주세요");
    }

    public DialogMethodCreateTask init(){

        ganttChart = new GanttChart(context);
        ganttChart.setParentsOverMoveImpossible(true);


        tasks = new Tasks();
        methodBoardService = new MethodBoardService();

        publicTaskService = new PublicTaskService();
        publicTaskService.setTasks(tasks);

        methodBoardService.view(mbcode);
        methodBoardService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                MethodBoardVO vo = (MethodBoardVO) objects[0];
                publicTaskService.list(vo.getPcode());
                publicTaskService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        dataSetting();
                    }
                });
            }
        });


        setView(ganttChart);
        setPositiveButton("생성", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Task t : tasks.getPublicTasks()){
                    t.setRefpcode(pcode);
                    t.setPercent(0);
                }
                if (onPositiveClickListener != null) onPositiveClickListener.getTask(tasks.getPublicTasks());
            }
        });

        return this;
    }

    int diffDay;
    boolean diff;
    void dataSetting(){
        for (Task t : tasks.getPublicTasks()){
            if(t.getSdate() == null) continue;

            String [] sday  = t.getSdate().split("-");
            String [] eday  = t.getEdate().split("-");

            Calendar sC = Calendar.getInstance();
            Calendar eC = Calendar.getInstance();

            sC.set(Integer.parseInt(sday[0]), Integer.parseInt(sday[1]), Integer.parseInt(sday[2]));
            eC.set(Integer.parseInt(eday[0]), Integer.parseInt(eday[1]), Integer.parseInt(eday[2]));

            if (!diff){
                diffDay = diffDay(sC, sdate);
                diff = true;
            }



            sC.add(Calendar.DATE, diffDay);
            eC.add(Calendar.DATE, diffDay);

            int sY = sC.get(Calendar.YEAR);
            int sM = sC.get(Calendar.MONDAY) + 1;
            int sD = sC.get(Calendar.DATE);

            int eY = eC.get(Calendar.YEAR);
            int eM = eC.get(Calendar.MONTH) + 1;
            int eD = eC.get(Calendar.DATE);

            t.setSdate(sY+"-"+sM+"-"+sD);
            t.setEdate(eY+"-"+eM+"-"+eD);
        }

        ChartDataSetting chartDataSetting = new ChartDataSetting();
        chartDataSetting = new ChartDataSetting();
        chartDataSetting.setTasks(tasks).init();

        chartDataSetting.setDataSettingListener(new ChartDataSetting.DataSettingListener() {
            @Override
            public void data(List<TaskBarItem> bars, List<DocOnTheBarItem> onTheBarItems) {
                Data data = new Data();
                data.setBars(bars);
                ganttChart.setData(data);
            }
        });
    }

    int diffDay(Calendar date, Calendar target) {
        int diff = 0;

        double d1 = date.getTime().getTime();
        double d2 = target.getTime().getTime();

        diff = (int) Math.round((d2 - d1) / (1000 * 60 * 60 * 24));

        return diff;
    }






    int mbcode;

    int pcode;

    Calendar sdate;

    public Calendar getSdate() {
        return sdate;
    }

    public DialogMethodCreateTask setSdate(Calendar sdate) {
        this.sdate = sdate;
        return this;
    }

    public int getMbcode() {
        return mbcode;
    }

    public DialogMethodCreateTask setMbcode(int mbcode) {
        this.mbcode = mbcode;
        return this;
    }

    public int getPcode() {
        return pcode;
    }

    public DialogMethodCreateTask setPcode(int pcode) {
        this.pcode = pcode;
        return this;
    }

    OnPositiveClickListener onPositiveClickListener;

    public OnPositiveClickListener getOnPositiveClickListener() {
        return onPositiveClickListener;
    }

    public DialogMethodCreateTask setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
        return this;
    }

    public interface OnPositiveClickListener{
        void getTask(List<Task> taskList);
    }
}
