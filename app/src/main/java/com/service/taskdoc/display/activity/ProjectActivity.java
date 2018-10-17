package com.service.taskdoc.display.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.display.transitions.project.Configure;
import com.service.taskdoc.display.transitions.methodboard.MethodBoard;
import com.service.taskdoc.display.transitions.project.MyInfo;
import com.service.taskdoc.display.transitions.project.Project;
import com.service.taskdoc.display.transitions.task.Task;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.listener.OnBackPressedListener;

public class ProjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TITLE_PROJECT = "프로젝트 리스트";
    private final String TITLE_USERINFO = "내 정보";
    private final String TITLE_BOARD = "게시판";
    private final String TITLE_TASK = "업무 리스트";
    private final String TITLE_CONFIGURE = "관리";

    public PrivateTaskService privateService;
    public Task taskView = Task.newInstance(Tasks.PRIVATE);

    private OnBackPressedListener onBackPressedListener;

    private Thread t;

    private final int COUNT = 100;
    private int count;

    Tasks tasks = new Tasks();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_project, new Project()).addToBackStack(null).commit();
        setTitle(TITLE_PROJECT);

        taskView.setTasks(tasks);

        privateService = new PrivateTaskService();
        privateService.setTasks(tasks);
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener){
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else if (this.onBackPressedListener != null) {
            onBackPressedListener.onBack();
        } else {
            if (t != null && t.isAlive() && count != 0){
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }else {
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProjectActivity.this, "종료 하시겠습니까?", Toast.LENGTH_SHORT).show();
                            }
                        });
                        count = COUNT;
                        for (;count > 0;count--){
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tasks.getPrivateTasks().clear();
        privateService.list(UserInfo.getUid());
        privateService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                taskView.datachange();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        try {
            if (id == R.id.nav_project) {
                setTitle(TITLE_PROJECT);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_project, new Project()).addToBackStack(null).commit();
            } else if (id == R.id.nav_task) {
                setTitle(TITLE_TASK);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_project, taskView).addToBackStack(null).commit();
            } else if (id == R.id.nav_information) {
                setTitle(TITLE_USERINFO);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_project, new MyInfo()).addToBackStack(null).commit();
            } else if (id == R.id.nav_board) {
                setTitle(TITLE_BOARD);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_project, MethodBoard.newInstance(MethodBoard.ALL)).addToBackStack(null).commit();
            }else if (id == R.id.nav_manage) {
                setTitle(TITLE_CONFIGURE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_project, new Configure()).addToBackStack(null).commit();
            } else if (id == R.id.nav_homepage) {
                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                finish();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
