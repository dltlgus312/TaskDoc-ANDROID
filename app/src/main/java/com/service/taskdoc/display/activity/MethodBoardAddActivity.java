package com.service.taskdoc.display.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.transfer.MethodBoardVO;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.network.restful.service.MethodBoardService;
import com.service.taskdoc.service.system.support.service.KeyboardManager;

public class MethodBoardAddActivity extends AppCompatActivity {

    private MethodBoardVO vo;

    private TextView title;
    private EditText contents;
    private Button project;

    private int pcode;
    private int mbcode;
    private String mbtitle;

    private String TITLE = "제목 입력";
    private String PROJECTTITLE = "프로젝트 : ";
    private String ACCEPT = "확인";
    private String CANCLE = "취소";
    private String TITLEBANNER = "제목을 입력 해주세요.";
    private String PROJECTBANNER = "프로젝트 확인";
    private String PROJECTMESSAGEBANNER = "해당 프로젝트가 맞습니까?";
    private String REGISTERTITLE = "게시판 추가";
    private String REGISTERMESSAGE = "해당 게시물을 게시판에 등록 하시겠습니까?";
    private String UPDATETITLE = "게시판 수정";
    private String UPDATEMESSAGE = "해당 게시물을 수정 하시겠습니까?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_board_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.content_method_board_add_title);
        contents = findViewById(R.id.content_method_board_add_contents);
        project = findViewById(R.id.content_method_board_add_project);

        String json = getIntent().getStringExtra("board");

        if (!TextUtils.isEmpty(json)) {
            vo = new Gson().fromJson(json, MethodBoardVO.class);

            mbtitle = vo.getMbtitle();
            title.setText(mbtitle);
            contents.setText(vo.getMbcontents());

            for (Project p : Projects.getProjectList()) {
                if (p.getPcode() == vo.getPcode()) {
                    project.setText(PROJECTTITLE + p.getPtitle());
                }
            }
            pcode = vo.getPcode();
            mbcode = vo.getMbcode();
        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle();
            }
        });

        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProject();
            }
        });

        if (Build.VERSION.SDK_INT <= 21) {
            project.setBackground(getResources().getDrawable(R.drawable.style_button_accent));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.method_board_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_method_board_register) {
            setMethodBoard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle() {

        new KeyboardManager().show(this);

        EditText input = new EditText(this);
        input.setSingleLine();
        input.setHint(TITLEBANNER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(TITLE);
        builder.setView(input);

        if(vo != null){
            input.setText(mbtitle);
        }

        builder.setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mbtitle = input.getText().toString();
                title.setText(mbtitle);
            }
        });

        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void setProject() {

        ArrayAdapter<String> projectList = new ArrayAdapter<>(
                this, android.R.layout.simple_selectable_list_item);

        for (Project p : Projects.getProjectList()) {
            projectList.add(p.getPtitle());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(projectList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkProject(Projects.getProject(i));
            }
        });
        builder.show();
    }

    public void checkProject(Project p) {

        View view = getLayoutInflater().inflate(R.layout.recycle_item_project, null);

        RelativeLayout theme = view.findViewById(R.id.recycle_item_project_theme);
        TextView title = view.findViewById(R.id.recycle_item_project_title);
        TextView subTitle = view.findViewById(R.id.recycle_item_project_sub_title);
        TextView sdate = view.findViewById(R.id.recycle_item_project_sdate);
        TextView edate = view.findViewById(R.id.recycle_item_project_edate);
        TextView permision = view.findViewById(R.id.recycle_item_project_permision);

        title.setText(p.getPtitle());
        subTitle.setText(p.getPsubtitle());
        sdate.setText(p.getPsdate());
        edate.setText(p.getPedate());
        permision.setText(p.getPpermission());

        String perm = p.getPpermission();



        if (perm.equals(Projects.OWNER)) {
            theme.setBackgroundResource(R.drawable.style_project_owner);
        } else if (perm.equals(Projects.MEMBER)) {
            theme.setBackgroundResource(R.drawable.style_project_member);
        }

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams parambtn = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parambtn.setMargins(20, 30, 20, 30);
        linearLayout.addView(view, parambtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(PROJECTBANNER);
        builder.setMessage(PROJECTMESSAGEBANNER);
        builder.setView(linearLayout);
        builder.setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pcode = p.getPcode();
                project.setText("프로젝트 : " + p.getPtitle());
            }
        });

        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }


    public void setMethodBoard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (vo == null) {
            builder.setTitle(REGISTERTITLE);
            builder.setMessage(REGISTERMESSAGE);
        } else {
            builder.setTitle(UPDATETITLE);
            builder.setMessage(UPDATEMESSAGE);
        }

        builder.setPositiveButton(ACCEPT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MethodBoardVO vo = new MethodBoardVO();

                vo.setMbtitle(mbtitle);
                vo.setPcode(pcode);
                vo.setMbcode(mbcode);
                vo.setMbcontents(contents.getText().toString());
                vo.setUid(UserInfo.getUid());

                MethodBoardService service = new MethodBoardService();
                if (MethodBoardAddActivity.this.vo == null) {
                    service.work(new NetworkSuccessWork() {
                        @Override
                        public void work(Object... objects) {
                            Toast.makeText(MethodBoardAddActivity.this, "등록 완료", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                    service.insert(vo);
                } else {
                    service.work(new NetworkSuccessWork() {
                        @Override
                        public void work(Object... objects) {
                            Intent intent = new Intent();
                            intent.putExtra("pcode", vo.getPcode());
                            setResult(RESULT_OK, intent);
                            Toast.makeText(MethodBoardAddActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                    service.update(vo);
                }
            }
        });

        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

}
