package com.service.taskdoc.display.custom.custom.dialog.chat;

import android.content.Context;
import android.widget.Toast;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionCreateDialog;
import com.service.taskdoc.display.custom.custom.dialog.decision.DecisionItemSelectDialog;
import com.service.taskdoc.display.custom.custom.dialog.decision.DialogDecParam;
import com.service.taskdoc.display.custom.custom.dialog.task.DialogTaskPicker;
import com.service.taskdoc.service.network.restful.service.ChatRoomService;

import java.util.List;

public class FocusCreateDialog {

    Context context;

    int pcode;

    int crcode;

    public FocusCreateDialog(Context context){
        this.context = context;
    }

    /*
     * Show Dialog View
     * */
    private String title;

    public void showDecisionParameter(){
        DialogChaParam builder = new DialogChaParam(context);
        android.app.AlertDialog dialog = builder.create();
        builder.setOnPositiveClick(new DialogChaParam.OnPositiveClick() {
            @Override
            public void getImformation(String t) {
                if (t.equals("") || t == null) {
                    Toast.makeText(context, "이름이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                title = t;
                showTaskPicker();
            }
        });
        dialog.show();
    }



    Task task;

    void showTaskPicker(){
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

    void dataSetting(){
        ChatRoomVO vo = new ChatRoomVO();
        vo.setFctitle(title);
        vo.setCrmode(3);
        vo.setCrclose(0);
        vo.setCrcoderef(crcode);
        vo.setTcode(task.getCode());

        if (chatRoomCreateListener != null) chatRoomCreateListener.getParameter(vo);
    }








    /*
    * Data Setting
    * */
    public int getPcode() {
        return pcode;
    }

    public FocusCreateDialog setPcoce(int pcode) {
        this.pcode = pcode;
        return this;
    }

    public int getCrcode() {
        return crcode;
    }

    public FocusCreateDialog setCrcode(int crcode) {
        this.crcode = crcode;
        return this;
    }




    /*
    * Listener
    * */

    ChatRoomCreateListener chatRoomCreateListener;

    public ChatRoomCreateListener getChatRoomCreateListener() {
        return chatRoomCreateListener;
    }

    public FocusCreateDialog setChatRoomCreateListener(ChatRoomCreateListener chatRoomCreateListener) {
        this.chatRoomCreateListener = chatRoomCreateListener;
        return this;
    }

    public interface ChatRoomCreateListener{
        void getParameter(ChatRoomVO vo);
    }
}
