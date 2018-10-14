package com.service.taskdoc.display.custom.custom.dialog.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.ChartData;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.display.custom.custom.chart.ChartDataSetting;
import com.service.taskdoc.display.custom.custom.chart.DocOnTheBarItem;
import com.service.taskdoc.display.custom.custom.chart.TaskBarItem;
import com.service.taskdoc.display.custom.ganttchart.BarItem;
import com.service.taskdoc.display.custom.ganttchart.Data;
import com.service.taskdoc.display.custom.ganttchart.GanttChart;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.ArrayList;
import java.util.List;

public class DialogTaskPicker extends AlertDialog.Builder implements NetworkSuccessWork {

    /*
     * Make Data
     * */
    private List<TaskBarItem> items;

    private TaskBarItem refItem;

    private ChartDataSetting chartDataSetting;

    private GanttChart ganttChart;


    /*
     * Data
     * */
    private Tasks tasks;
    private int pcode;
    private String banner = "업무를 선택 해주세요";


    public String getBanner() {
        return banner;
    }

    public DialogTaskPicker setBanner(String banner) {
        this.banner = banner;
        return this;
    }

    Context context;

    boolean privateTask;

    public DialogTaskPicker(Context context) {
        super(context);
    }

    public DialogTaskPicker(Context context, int pcode) {
        super(context);
        this.context = context;
        init(pcode);
    }

    public DialogTaskPicker(Context context, int pcode, boolean privateTask) {
        super(context);
        this.context = context;
        this.privateTask = privateTask;
        init(pcode);
    }

    public void init(int pcode) {

        this.tasks = new Tasks();

        publicTaskService = new PublicTaskService();
        publicTaskService.setTasks(tasks);
        if (privateTask) {
            publicTaskService.listAll(pcode);

        } else {
            publicTaskService.list(pcode);
        }
        publicTaskService.work(this);

        ganttChart = new GanttChart(context);
        ganttChart.setBackgroundColor(0xff000000);
        ganttChart.setOnTheChartDrawListener(new GanttChart.OnTheChartDrawListener() {
            @Override
            public void drawFront(Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(ganttChart.getTextColor());
                paint.setTextSize(ganttChart.getTextSize());
                paint.setStrokeWidth(5);

                float width = paint.measureText(banner, 0, banner.length());
                canvas.drawText(banner, ganttChart.getWidth() / 2 - (width/2), ganttChart.getHeight() * 0.9f, paint);
            }

            @Override
            public void drawBack(Canvas canvas) {

            }
        });
        ganttChart.setOnBarClickListener(new GanttChart.OnBarClickListener() {
            @Override
            public void itemSelect(BarItem barItem) {
                refItem = (TaskBarItem) barItem;
                banner = "선택하신 업무는 \"" + refItem.getTitle() + "\" 입니다.";
            }
        });

        setView(ganttChart);
        setTitle("업무선택");

        setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onPositiveClick != null) {
                    if (refItem != null)
                        onPositiveClick.getTask(refItem.getTask());
                    else onPositiveClick.getTask(null);
                }
            }
        });
        setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }


    /*
     * Listener
     * */
    private OnPositiveClick onPositiveClick;

    public OnPositiveClick getOnPositiveClick() {
        return onPositiveClick;
    }

    public void setOnPositiveClick(OnPositiveClick onPositiveClick) {
        this.onPositiveClick = onPositiveClick;
    }

    private OutputTaskGetter outputTaskGetter;

    public OutputTaskGetter getOutputTaskGetter() {
        return outputTaskGetter;
    }

    public void setOutputTaskGetter(OutputTaskGetter outputTaskGetter) {
        this.outputTaskGetter = outputTaskGetter;
    }


    public interface OnPositiveClick {
        void getTask(Task task);
    }

    public interface OutputTaskGetter {
        void getTask(Task task);
    }


    /*
     * Network
     * */
    private PublicTaskService publicTaskService;

    @Override
    public void work(Object... objects) {

        if (outputTaskGetter != null) {
            outputTaskGetter.getTask(tasks.getPublicTasks().get(0));
        } else {
            chartDataSetting = new ChartDataSetting(pcode);
            chartDataSetting.setTasks(tasks).init();

            chartDataSetting.setDataSettingListener(new ChartDataSetting.DataSettingListener() {
                @Override
                public void data(List<TaskBarItem> bars, List<DocOnTheBarItem> onTheBarItems) {
                    for (TaskBarItem item : bars)
                        item.setClickable(false);

                    items = bars;
                    Data data = new Data();
                    data.setBars(items);
                    ganttChart.setData(data);
                }
            });
        }
    }


}
