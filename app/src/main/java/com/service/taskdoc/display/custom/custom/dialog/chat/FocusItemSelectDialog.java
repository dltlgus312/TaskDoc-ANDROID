package com.service.taskdoc.display.custom.custom.dialog.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.ChatRoomVO;
import com.service.taskdoc.display.activity.ChatingActivity;
import com.service.taskdoc.display.custom.custom.dialog.task.DialogTaskPicker;
import com.service.taskdoc.service.network.restful.service.ChatRoomService;
import com.service.taskdoc.service.network.restful.service.DecisionService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

public class FocusItemSelectDialog {

    Context context;

    ChatRoomVO vo;

    public FocusItemSelectDialog(Context context, ChatRoomVO vo){

        this.context = context;

        this.vo = vo;
    }



    /*
    * Show & Service
    * */
    public void showSelectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("회의록");

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_focus_view, null);


        TextView title = view.findViewById(R.id.title);
        TextView date = view.findViewById(R.id.date);

        Button join = view.findViewById(R.id.join);
        Button close = view.findViewById(R.id.close);
        Button reposition = view.findViewById(R.id.reposition);

        title.setText(vo.getFctitle());
        date.setText(vo.getCrdate());


        if (vo.getCrclose() == 1){
            join.setText("회의 내용 확인");
            close.setVisibility(View.GONE);
            reposition.setVisibility(View.GONE);
        } else if (project.getPpermission().equals(Projects.MEMBER)){
            close.setVisibility(View.GONE);
            reposition.setVisibility(View.GONE);
        }

        builder.setView(view);
        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog selectDialog = builder.show();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder suggestion = new AlertDialog.Builder(context);
                suggestion.setTitle("회의 종료");
                suggestion.setMessage("정말로 회의를 종료 하시겠습니까?");
                suggestion.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        selectDialog.dismiss();

                        vo.setCrclose(1);

                        chatRoomService = new ChatRoomService(0);
                        chatRoomService.update(vo);
                        chatRoomService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                if (focusEventListener != null) focusEventListener.event();
                            }
                        });
                    }
                });

                suggestion.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                suggestion.show();

            }
        });

        reposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, project.getPcode());
                dialogTaskPicker.setOnPositiveClick(new DialogTaskPicker.OnPositiveClick() {
                    @Override
                    public void getTask(Task t) {
                        if (t == null) {
                            Toast.makeText(context, "선택된 업무가 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        vo.setTcode(t.getCode());

                        chatRoomService = new ChatRoomService(0);
                        chatRoomService.update(vo);
                        chatRoomService.work(new NetworkSuccessWork() {
                            @Override
                            public void work(Object... objects) {
                                if (focusEventListener != null) focusEventListener.event();
                            }
                        });
                    }
                });
                dialogTaskPicker.show();
            }
        });
    }

    public void goTo(){
        Intent intent = new Intent(context, ChatingActivity.class);
        intent.putExtra("project", new Gson().toJson(project));
        intent.putExtra("chatRoomVO", new Gson().toJson(vo));
        context.startActivity(intent);
    }



    /*
     * Network Service
     * */
    ChatRoomService chatRoomService;



    /*
    * Reference Data
    * */

    Project project;

    public Project getProject() {
        return project;
    }

    public FocusItemSelectDialog setProject(Project project) {
        this.project = project;
        return this;
    }





    /*
    * Close Event
    * */

    FocusEventListener focusEventListener;

    public FocusEventListener getFocusEventListener() {
        return focusEventListener;
    }

    public FocusItemSelectDialog setFocusEventListener(FocusEventListener focusEventListener) {
        this.focusEventListener = focusEventListener;
        return this;
    }

    public interface FocusEventListener {
        void event();
    }

}
