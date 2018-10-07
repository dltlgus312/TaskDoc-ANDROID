package com.service.taskdoc.display.custom.custom.dialog.decision;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.VoterVO;
import com.service.taskdoc.display.recycle.DecisionItemListCycle;
import com.service.taskdoc.service.network.restful.service.DecisionItemService;
import com.service.taskdoc.service.network.restful.service.VoterService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.ArrayList;
import java.util.List;

public class DialogDecisionItemList extends AlertDialog.Builder {


    private DecisionVO decisionVO;

    private List<DecisionItemVO> itemList;

    private List<List<VoterVO>> voterList;

    private DecisionItemListCycle itemcycle;

    private Context context;


    public DialogDecisionItemList(Context context, DecisionVO decisionVO) {
        super(context);

        this.context = context;
        this.decisionVO = decisionVO;
    }

    public void init() {

        setTitle("의사결정");

        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER);

        itemList = new ArrayList<>();
        voterList = new ArrayList<>();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item_decision_view, null);

        RecyclerView recyclerView = view.findViewById(R.id.recycle);
        Button voter = view.findViewById(R.id.voter);
        Button close = view.findViewById(R.id.close);


        voter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.voterClick(itemList, voterList);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.closeClick();
            }
        });


        itemcycle = new DecisionItemListCycle(itemList, voterList, false);
        itemcycle.setOnClickListener(new DecisionItemListCycle.OnClickListener() {
            @Override
            public void onClickItem(DecisionItemVO item, List<VoterVO> voters) {
                if (onClickListener != null) onClickListener.itemClick(item, voters);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemcycle);

        setView(view);

        setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        if (decisionVO.getDsclose() == 1){
            close.setVisibility(View.GONE);
            voter.setVisibility(View.GONE);
        } else if (permision != null && permision.equals(Projects.MEMBER) && crmode == 1) {
            close.setVisibility(View.GONE);
        }

        voterService = new VoterService();
        voterService.setList(voterList);
        voterService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                itemcycle.notifyDataSetChanged();
            }
        });

        decisionItemService = new DecisionItemService();
        decisionItemService.setList(itemList);
        decisionItemService.list(decisionVO.getDscode());
        decisionItemService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                for (DecisionItemVO vo : itemList) {
                    voterService.list(vo.getDsicode());
                }
                itemcycle.notifyDataSetChanged();
            }
        });
    }




    /*
     * Reference Data
     * */
    int crmode;

    String permision;

    public String getPermision() {
        return permision;
    }

    public DialogDecisionItemList setPermision(String permision) {
        this.permision = permision;
        return this;
    }

    public int getCrmode() {
        return crmode;
    }

    public DialogDecisionItemList setCrmode(int crmode) {
        this.crmode = crmode;
        return this;

    }




    /*
     * Network
     * */
    private DecisionItemService decisionItemService;

    private VoterService voterService;




    /*
     * Click Listener
     * */
    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void itemClick(DecisionItemVO item, List<VoterVO> voters);
        void voterClick(List<DecisionItemVO> items, List<List<VoterVO>> voters);
        void closeClick();
    }
}
