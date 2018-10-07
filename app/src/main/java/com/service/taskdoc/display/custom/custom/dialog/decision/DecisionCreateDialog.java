package com.service.taskdoc.display.custom.custom.dialog.decision;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.display.custom.custom.dialog.task.DialogTaskPicker;
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
                if (t.equals("") || t == null) {
                    Toast.makeText(context, "이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
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

                if (cycle.getVos().size() < 1){
                    Toast.makeText(context, "의사결정 항목은 1개 이상이여야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                itemVos = cycle.getVos();

                if (crmode == 1)
                    showTaskPicker();
                else
                    dataSetting();
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


    private Task task;

    public void showTaskPicker(){
        DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, pcode);
        dialogTaskPicker.setOnPositiveClick(new DialogTaskPicker.OnPositiveClick() {
            @Override
            public void getTask(Task t) {
                if (t == null) {
                    Toast.makeText(context, "선택된 업무가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                task = t;

                dataSetting();
            }
        });
        dialogTaskPicker.show();
    }

    public void dataSetting(){
        DecisionVO vo = new DecisionVO();

        if (crmode == 1){
            vo.setTcode(task.getCode());
        }
        vo.setCrcode(crcode);
        vo.setDsclose(0);
        vo.setDstitle(title);

        if (decisionCreateListener != null) decisionCreateListener.getParameter(vo, itemVos);
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




    /*
    * Reference Data
    * */
    private int pcode;

    private int crcode;

    private int crmode;

    public int getPcode() {
        return pcode;
    }

    public DecisionCreateDialog setPcode(int pcode) {
        this.pcode = pcode;
        return this;
    }

    public int getCrcode() {
        return crcode;
    }

    public DecisionCreateDialog setCrcode(int crcode) {
        this.crcode = crcode;
        return this;
    }

    public int getCrmode() {
        return crmode;
    }

    public DecisionCreateDialog setCrmode(int crmode) {
        this.crmode = crmode;
        return this;
    }






    /*
    * Event Listener
    * */

    private DecisionCreateListener decisionCreateListener;

    public DecisionCreateListener getDecisionCreateListener() {
        return decisionCreateListener;
    }

    public DecisionCreateDialog setDecisionCreateListener(DecisionCreateListener decisionCreateListener) {
        this.decisionCreateListener = decisionCreateListener;
        return this;
    }

    public interface DecisionCreateListener{
        void getParameter(DecisionVO vo, List<DecisionItemVO> vos);
    }
}
