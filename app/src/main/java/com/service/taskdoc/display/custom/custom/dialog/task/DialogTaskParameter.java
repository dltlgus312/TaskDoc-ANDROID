package com.service.taskdoc.display.custom.custom.dialog.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.display.custom.ganttchart.Line;

public class DialogTaskParameter extends AlertDialog.Builder {

    Context context;

    public DialogTaskParameter(Context context) {
        super(context);
        this.context = context;
    }

    public DialogTaskParameter(Context context, int themeResId) {
        super(context, themeResId);
    }



    /*
    * Show View
    * */
    EditText title;

    LinearLayout date;
    Button sDate;
    TextView dateBanner;
    Button eDate;

    LinearLayout color;
    Button colorBanner;

    Button taskBanner;

    public DialogTaskParameter init(){

        setTitle("업무 생성");

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_task_create_view, null);

        /*
         * setting
         * */
        title = view.findViewById(R.id.title);

        date = view.findViewById(R.id.date);
        sDate = view.findViewById(R.id.start_date);
        dateBanner = view.findViewById(R.id.date_banner);
        eDate = view.findViewById(R.id.end_date);

        color = view.findViewById(R.id.color);
        colorBanner = view.findViewById(R.id.color_banner);

        taskBanner = view.findViewById(R.id.task_banner);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.dateClick(sDate, dateBanner, eDate);
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.colorClick(colorBanner);
            }
        });


        /*
        * Setting
        * */
        if (refTask != null) {
            modifyTask();
        }

        if (parentsTask != null){
            taskBanner.setText(parentsTask.getTitle());
        }


        /*
         * Default
         * */
        setView(view);
        setNegativeButton("이전", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onClickListener != null) onClickListener.before();
            }
        });

        setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onClickListener != null) onClickListener.positiveButton(
                        title.getText().toString(),
                        sDate.getText().toString(),
                        eDate.getText().toString(),
                        colorBanner.getText().toString(),
                        parentsTask
                );
            }
        });
        return this;
    }



    /*
    * 수정
    * */
    void modifyTask(){
        setTitle("업무 수정");

        title.setText(refTask.getTitle());
        colorBanner.setText(refTask.getColor());
        sDate.setText(refTask.getSdate());
        eDate.setText(refTask.getEdate());
        taskBanner.setText(parentsTask.getTitle());
        dateBanner.setText("~");
    }





    /*
    * data
    * */

    Task refTask;
    Task parentsTask;

    public Task getRefTask() {
        return refTask;
    }

    public DialogTaskParameter setRefTask(Task refTask) {
        this.refTask = refTask;
        return this;
    }

    public Task getParentsTask() {
        return parentsTask;
    }

    public DialogTaskParameter setParentsTask(Task parentsTask) {
        this.parentsTask = parentsTask;
        return this;
    }

    /*
    * Listener
    * */
    OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public DialogTaskParameter setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener{
        void before();
        void dateClick(Button sDate, TextView banner, Button eDate);
        void colorClick(Button banner);
        void positiveButton(String title, String sDate, String eDate, String color, Task parentsTask);
    }
}
