package com.service.taskdoc.display.custom.custom.dialog.memo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.display.recycle.DecisionItemMakeCycle;
import com.service.taskdoc.service.network.restful.service.MemoService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DialogMemoList{

    Context context;

    List<MemoVO> vos = new ArrayList<>();

    MemoService service;

    DecisionItemMakeCycle cycle;

    public DialogMemoList(Context context) {
        this.context = context;

    }

    public void init(){
        service = new MemoService();
        service.list(task.getCode());
        service.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                vos.addAll((ArrayList<MemoVO>) objects[0]);
                cycle.notifyDataSetChanged();
            }
        });

        cycle = new DecisionItemMakeCycle(vos);
        RecyclerView recyclerView = new RecyclerView(context);

        cycle.setAddClickRePositionListener(new DecisionItemMakeCycle.AddClickRePositionListener() {
            @Override
            public void onClick() {
                cycle.notifyDataSetChanged();
                recyclerView.scrollToPosition(cycle.getItemCount()-1);
            }
        });

        cycle.setSucessClickListener(new DecisionItemMakeCycle.SucessClickListener() {
            @Override
            public void onClick(MemoVO vo, String text) {
                if (vo.getMcode() != 0){
                    service.update(vo, text);
                    service.work(new NetworkSuccessWork() {
                        @Override
                        public void work(Object... objects) {

                        }
                    });
                }else {
                    vo.setMcontents(text);
                    vo.setPtcode(task.getCode());
                    service.insert(vo);
                    service.work(new NetworkSuccessWork() {
                        @Override
                        public void work(Object... objects) {
                            vo.setMcode((Integer) objects[0]);
                        }
                    });
                }
            }

            @Override
            public void onDelete(List<MemoVO> vos, MemoVO vo) {
                service.delete(vos, vo);
                service.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        cycle.notifyDataSetChanged();
                    }
                });
            }
        });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("\"" + task.getTitle() + "\" 작성된 메모");
        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        android.support.v7.app.AlertDialog dialog = builder.setView(recyclerView).create();
        cycle.setDialog(dialog);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);

        dialog.show();
    }


    Task task;

    public Task getTask() {
        return task;
    }

    public DialogMemoList setTask(Task task) {
        this.task = task;
        return this;
    }
}
