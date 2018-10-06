package com.service.taskdoc.display.custom.custom.dialog.decision;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.display.custom.ganttchart.Line;
import com.service.taskdoc.display.recycle.DecisionItemMakeCycle;

import java.util.List;

public class DecisionCreateDialog {

    private Context context;



    public DecisionCreateDialog(Context context) {
        this.context = context;
    }


    /*
    * Show Dialog View
    * */
    private String title;

    public void showDecisionParameter(){
        DialogDecParam builder = new DialogDecParam(context);
        android.app.AlertDialog dialog = builder.create();
        builder.setOnPositiveClick(new DialogDecParam.OnPositiveClick() {
            @Override
            public void getImformation(String t) {
                title = t;
                showItemCreates();
            }
        });
        dialog.show();
    }


    private List<DecisionItemVO> itemVos;

    public void showItemCreates(){
        DecisionItemMakeCycle cycle = new DecisionItemMakeCycle();
        RecyclerView recyclerView = new RecyclerView(context);

        cycle.setAddClickRePositionListener(new DecisionItemMakeCycle.AddClickRePositionListener() {
            @Override
            public void onClick() {
                cycle.notifyDataSetChanged();
                recyclerView.scrollToPosition(cycle.getItemCount()-1);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("의사결정 리스트");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemVos = cycle.getVos();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.setView(recyclerView).create();
        cycle.setDialog(dialog);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        dialog.show();
    }



    /*
    * Parameter
    * */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DecisionItemVO> getItemVos() {
        return itemVos;
    }

    public void setItemVos(List<DecisionItemVO> itemVos) {
        this.itemVos = itemVos;
    }
}
