package com.service.taskdoc.display.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Methods;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.MethodListVO;
import com.service.taskdoc.display.recycle.MethodListCycle;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.MethodBoardService;
import com.service.taskdoc.service.network.restful.service.MethodListService;

public class MethodListActivity extends AppCompatActivity implements NetworkSuccessWork, MethodListCycle.ClickListener {

    private MethodBoardService methodBoardService;
    private MethodListService service;
    private MethodListCycle cycle;
    private MethodListVO menuRefVo;

    private TextView banner;

    private final String DELETE_TITLE = "삭제";
    private final String DELETE_BANNER = "정말 삭제 하시겠습니까?";
    private final String MODIFY_TITLE= "수정";

    private final String DIALOG_YES = "예";
    private final String DIALOG_NO = "아니요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HorizontalScrollView horizontalScrollView = findViewById(R.id.scroll);
        horizontalScrollView.setVisibility(View.GONE);

        service = new MethodListService();
        service.work(this);

        cycle = new MethodListCycle(Methods.getMethodLists());
        cycle.setClickListener(this);

        banner = findViewById(R.id.banner);

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cycle);

        service.getList(UserInfo.getUid());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_project_owner, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_project_owner_modify:
                modify();
                return true;
            case R.id.menu_project_owner_delete:
                delete();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void work(Object... objects) {
        if(Methods.getMethodLists().size() != 0){
            banner.setVisibility(View.GONE);
        }
        cycle.notifyDataSetChanged();
    }

    public void delete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(DELETE_TITLE);
        builder.setMessage(DELETE_BANNER);

        builder.setPositiveButton(DIALOG_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                service.delete(menuRefVo);
            }
        });

        builder.setNegativeButton(DIALOG_NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }

    public void modify(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(MODIFY_TITLE);

        EditText input = new EditText(this);
        input.setSingleLine();
        input.setFocusable(true);
        input.setText(menuRefVo.getMltitle());

        builder.setView(input);

        builder.setPositiveButton(DIALOG_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MethodListVO vo = new MethodListVO();

                vo.setMltitle(input.getText().toString());
                vo.setUid(menuRefVo.getUid());
                vo.setMbcode(menuRefVo.getMbcode());

                service.update(menuRefVo, vo);
            }
        });

        builder.show();

    }

    @Override
    public void onClick(MethodListVO vo) {
        methodBoardService = new MethodBoardService();
        methodBoardService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                Intent intent = new Intent(MethodListActivity.this, MethodBoardViewActivity.class);
                intent.putExtra("board", new Gson().toJson(objects[0]));
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        methodBoardService.view(vo.getMbcode());
    }

    @Override
    public void onLongClick(MethodListVO vo, View view) {
        registerForContextMenu(view);
        menuRefVo = vo;
    }
}
