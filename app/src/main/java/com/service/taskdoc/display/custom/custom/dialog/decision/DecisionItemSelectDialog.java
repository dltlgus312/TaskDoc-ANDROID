package com.service.taskdoc.display.custom.custom.dialog.decision;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.VoterVO;
import com.service.taskdoc.display.custom.custom.dialog.task.DialogTaskPicker;
import com.service.taskdoc.service.network.restful.service.DecisionService;
import com.service.taskdoc.service.network.restful.service.VoterService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.List;

public class DecisionItemSelectDialog implements DialogDecisionItemList.OnClickListener {

    private Context context;

    private DecisionVO vo;

    public DecisionItemSelectDialog(Context context, DecisionVO vo) {
        this.context = context;
        this.vo = vo;
    }


    /*
     * Service
     * */
    private DialogDecisionItemList dialogDecisionItemList;
    private AlertDialog itemListDial;

    public void showList() {
        dialogDecisionItemList = new DialogDecisionItemList(context, vo);
        dialogDecisionItemList.setCrmode(crmode);
        dialogDecisionItemList.setPermision(permision);
        dialogDecisionItemList.setOnClickListener(this);
        dialogDecisionItemList.init();
        itemListDial = dialogDecisionItemList.show();
    }





    /*
     * Reference Data
     * */

    private int crmode;

    private String permision;

    private int pcode;

    public int getCrmode() {
        return crmode;
    }

    public DecisionItemSelectDialog setCrmode(int crmode) {
        this.crmode = crmode;
        return this;
    }

    public String getPermision() {
        return permision;
    }

    public DecisionItemSelectDialog setPermision(String permision) {
        this.permision = permision;
        return this;
    }

    public int getPcode() {
        return pcode;
    }

    public DecisionItemSelectDialog setPcode(int pcode) {
        this.pcode = pcode;
        return this;
    }





    /*
     * Click Event
     * */
    @Override
    public void itemClick(DecisionItemVO item, List<VoterVO> voters) {
        itemListDial.dismiss();

    }

    DialogDecisionItemPicker dialogDecisionItemPicker;
    AlertDialog itemPickerDialog;

    @Override
    public void voterClick(List<DecisionItemVO> items, List<List<VoterVO>> voters) {
        itemListDial.dismiss();

        voterService = new VoterService();
        voterService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                Toast.makeText(context, "투표가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        DialogDecisionItemPicker.OnPositiveClickListener listener = new
                DialogDecisionItemPicker.OnPositiveClickListener() {
                    @Override
                    public void insert(VoterVO vo) {
                        voterService.insert(vo);
                    }

                    @Override
                    public void update(VoterVO oldVo, VoterVO newVo) {
                        voterService.update(oldVo, newVo);
                    }
                };

        dialogDecisionItemPicker = new DialogDecisionItemPicker(context);
        dialogDecisionItemPicker.setOnPositiveClickListener(listener);
        dialogDecisionItemPicker.setItemList(items);
        dialogDecisionItemPicker.setVoterList(voters);
        dialogDecisionItemPicker.init();
        itemPickerDialog = dialogDecisionItemPicker.show();
    }

    @Override
    public void closeClick() {
        itemListDial.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("투표 종료");
        builder.setMessage("정말로 종료 하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                vo.setDsclose(1);

                decisionService = new DecisionService(crmode);
                decisionService.update(vo);
                decisionService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        if (decisionEventListener != null) decisionEventListener.update();
                    }
                });
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @Override
    public void reposition(){
        DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, pcode);
        dialogTaskPicker.setOnPositiveClick(new DialogTaskPicker.OnPositiveClick() {
            @Override
            public void getTask(Task t) {
                if (t == null) {
                    Toast.makeText(context, "선택된 업무가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                vo.setTcode(t.getCode());

                decisionService = new DecisionService(crmode);
                decisionService.update(vo);
                decisionService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        if (decisionEventListener != null) decisionEventListener.update();
                    }
                });

            }
        });
        dialogTaskPicker.show();
    }


    /*
     * Network Service
     * */
    private DecisionService decisionService;
    private VoterService voterService;


    /*
     * Listener
     * */
    DecisionEventListener decisionEventListener;

    public DecisionEventListener getDecisionEventListener() {
        return decisionEventListener;
    }

    public DecisionItemSelectDialog setDecisionEventListener(DecisionEventListener decisionEventListener) {
        this.decisionEventListener = decisionEventListener;
        return this;
    }

    public interface DecisionEventListener {
        void update();
    }


}
