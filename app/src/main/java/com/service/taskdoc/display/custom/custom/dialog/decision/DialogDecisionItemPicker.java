package com.service.taskdoc.display.custom.custom.dialog.decision;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.VoterVO;
import com.service.taskdoc.display.recycle.DecisionItemListCycle;

import java.util.List;

public class DialogDecisionItemPicker extends AlertDialog.Builder {


    private List<DecisionItemVO> itemList;

    private List<List<VoterVO>> voterList;

    private DecisionItemListCycle itemcycle;

    private Context context;

    private DecisionItemVO clickedItem;

    public DialogDecisionItemPicker(Context context) {
        super(context);
        this.context = context;
    }

    public DialogDecisionItemPicker(Context context, int themeResId) {
        super(context, themeResId);
    }


    public void init() {
        setTitle("투표");

        itemcycle = new DecisionItemListCycle(itemList, voterList, true);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_decision_select_view, null);

        RecyclerView recyclerView = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemcycle);

        setView(view);

        setPositiveButton("투표", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onPositiveClickListener != null){
                    if (itemcycle.getAlreadyItemVO() == null) {
                        // create
                        onPositiveClickListener.insert(itemcycle.getNewItemVO());
                    } else {
                        // update
                        onPositiveClickListener.update(itemcycle.getAlreadyItemVO(), itemcycle.getNewItemVO());
                    }
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
     * Reference Data
     * */
    public List<DecisionItemVO> getItemList() {
        return itemList;
    }

    public DialogDecisionItemPicker setItemList(List<DecisionItemVO> itemList) {
        this.itemList = itemList;
        return this;
    }

    public List<List<VoterVO>> getVoterList() {
        return voterList;
    }

    public DialogDecisionItemPicker setVoterList(List<List<VoterVO>> voterList) {
        this.voterList = voterList;
        return this;
    }



    /*
     * Listener
     * */

    OnPositiveClickListener onPositiveClickListener;

    public OnPositiveClickListener getOnPositiveClickListener() {
        return onPositiveClickListener;
    }

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
    }

    public interface OnPositiveClickListener {
        void insert(VoterVO vo);
        void update(VoterVO oldVo, VoterVO newVo);

    }
}
