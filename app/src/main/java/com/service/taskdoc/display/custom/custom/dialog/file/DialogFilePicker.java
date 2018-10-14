package com.service.taskdoc.display.custom.custom.dialog.file;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.service.taskdoc.display.custom.ganttchart.Line;
import com.service.taskdoc.display.recycle.FileCycle;

import java.util.ArrayList;
import java.util.List;

public class DialogFilePicker extends AlertDialog.Builder {

    private FileCycle fileCycle;

    private OnPositiveClick onPositiveClick;


    public DialogFilePicker(Context context) {
        super(context);

        fileCycle = new FileCycle();
        fileCycle.setOnClickListener(new FileCycle.OnClickListener() {
            @Override
            public void folderClick(FileCycle.Item item) {
                fileCycle.go(item.getName());
            }

            @Override
            public void fileClick(FileCycle.Item item) {
                if (item.getImg() == 2){
                    item.setImg(3);
                }else {
                    item.setImg(2);
                }
            }
        });

        RecyclerView recyclerView = new RecyclerView(context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileCycle);


        Button back = new Button(context);
        back.setText("뒤로가기");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fileCycle.canGoBack()){
                    fileCycle.back();
                }
            }
        });

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        linearLayout.addView(recyclerView);
        linearLayout.addView(back);

        this.setView(linearLayout);
        this.setTitle("파일 목록");

        setPositiveButton("다음", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onPositiveClick.selectFilePath(fileCycle.getSelectList());
            }
        });
        setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

    }

    public OnPositiveClick getOnPositiveClick() {
        return onPositiveClick;
    }

    public void setOnPositiveClick(OnPositiveClick onPositiveClick) {
        this.onPositiveClick = onPositiveClick;
    }

    public DialogFilePicker(Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnPositiveClick{
        void selectFilePath(List<FileCycle.Item> items);
    }

}
