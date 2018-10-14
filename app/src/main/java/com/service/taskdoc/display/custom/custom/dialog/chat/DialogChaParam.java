package com.service.taskdoc.display.custom.custom.dialog.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.service.taskdoc.R;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.display.activity.ChatingActivity;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

import java.security.Key;

public class DialogChaParam extends AlertDialog.Builder {

    private OnPositiveClick onPositiveClick;

    /*
     * 데이터 셋팅
     * */
    public DialogChaParam(Context context){
        super(context);
        setTitle("회의록 만들기");

        EditText title = new EditText(context);
        title.setHint(R.string.title);
        title.setSingleLine();
        title.requestFocus();

        title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    new KeyboardManager().hide(context, title);
                    return true;
                }
                return false;
            }
        });

        RecyclerView recyclerView = new RecyclerView(context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(title);
        linearLayout.addView(recyclerView);

        setView(linearLayout);

        setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new KeyboardManager().hide(context, title);
                if (onPositiveClick != null) onPositiveClick.getImformation(title.getText().toString());
            }
        });

        setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        new KeyboardManager().show(context);

    }


    /*
     * listener
     * */

    public OnPositiveClick getOnPositiveClick() {
        return onPositiveClick;
    }

    public void setOnPositiveClick(OnPositiveClick onPositiveClick) {
        this.onPositiveClick = onPositiveClick;
    }

    public interface OnPositiveClick{
        void getImformation(String title);
    }

}
