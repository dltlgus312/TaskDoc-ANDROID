package com.service.taskdoc.display.transitions.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.display.activity.ProjectProgressActivity;
import com.service.taskdoc.display.recycle.MemoCycle;
import com.service.taskdoc.display.recycle.TaskCycle;
import com.service.taskdoc.service.network.restful.service.MemoService;
import com.service.taskdoc.service.network.restful.service.PrivateTaskService;
import com.service.taskdoc.service.network.restful.service.PublicTaskService;
import com.service.taskdoc.service.system.support.service.ConvertDpPixels;
import com.service.taskdoc.service.system.support.service.KeyboardManager;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.ArrayList;
import java.util.List;

public class Element extends Fragment implements TaskCycle.ClickListener, TaskCycle.ChartClickListener, TaskCycle.RecycleExpandClickListener {

    private Tasks tasks;
    private TaskCycle cycle;
    private RecyclerView recyclerView;

    private LinearLayout path;
    private TextView banner;
    private TextView home;

    private com.service.taskdoc.database.business.transfer.Task menuRefTask;

    private final String SUCCESS = "확인";
    private final String CANCLE = "취소";
    private final String DELETETITLE = "업무 삭제";
    private final String DELETEBANNER = "해당 업무 삭제시 복구 불가능 합니다. 정말 삭제 하시겠습니까?";

    public Element() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_method_board_element, container, false);

        path = view.findViewById(R.id.path);
        banner = view.findViewById(R.id.banner);
        home = view.findViewById(R.id.home);

        int originalHeight = (int) ConvertDpPixels.convertDpToPixel(232, getContext());

        cycle = new TaskCycle(tasks);
        cycle.setOutput(false);
        cycle.setContentVisible(View.VISIBLE);
        cycle.setDownArrowVisible(View.GONE);
        cycle.setClickListener(this);
        cycle.setChartClickListener(this);
        cycle.setRecycleExpandClickListener(this, originalHeight);

        recyclerView = view.findViewById(R.id.recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cycle);
        cycle.init();

        if (cycle.getItemCount() > 0) {
            banner.setVisibility(View.GONE);
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cycle.goTo(0);
                recyclerView.scheduleLayoutAnimation();
                path.removeViews(1, path.getChildCount() - 1);
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_project_owner, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_project_owner_modify:
                modifyView();
                return true;
            case R.id.menu_project_owner_delete:
                deleteView();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void deleteView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(DELETETITLE);
        builder.setMessage(DELETEBANNER);
        builder.setPositiveButton(SUCCESS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete(menuRefTask);
            }
        });

        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    public void modifyView() {

    }

    public void delete(com.service.taskdoc.database.business.transfer.Task task) {
        boolean isPublic = Tasks.isPublic(task);

        if (isPublic) {
            PublicTaskService service = new PublicTaskService();
            service.delete(task.getCode());
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    int result = (int) objects[0];
                    if (result != 1) {
                        Task task = (Task) getParentFragment();
                    } else {
                        tasks.getPublicTasks().remove(task);
                        cycle.removeCopyTask(task);
                        cycle.notifyDataSetChanged();
                    }
                }
            });
        } else {
            PrivateTaskService service = new PrivateTaskService();
            service.delete(task.getCode());
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    int result = (int) objects[0];
                    if (result != 1) {
                        Task task = (Task) getParentFragment();
                    } else {
                        tasks.getPrivateTasks().remove(task);
                        cycle.removeCopyTask(task);
                        cycle.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void modify(com.service.taskdoc.database.business.transfer.Task task) {
        boolean isPublic = Tasks.isPublic(task);

        if (isPublic) {
            PublicTaskService service = new PublicTaskService();
            service.update(Tasks.publicConverter(task));
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    int result = (int) objects[0];
                    if (result != 1) {
                        Task task = (Task) getParentFragment();
                    } else {
                        cycle.notifyDataSetChanged();
                    }
                }
            });
        } else {
            PrivateTaskService service = new PrivateTaskService();
            service.update(Tasks.privateConverter(task));
            service.work(new NetworkSuccessWork() {
                @Override
                public void work(Object... objects) {
                    cycle.notifyDataSetChanged();
                }
            });
        }
    }

    public void dataChange() {
        if (cycle != null) {
            cycle.goTo(0);
            recyclerView.scheduleLayoutAnimation();
            path.removeViews(+1, path.getChildCount() - 1);
            cycle.notifyDataSetChanged();
        }
    }


    /* Event */
    @Override
    public void onClick(com.service.taskdoc.database.business.transfer.Task task) {
        cycle.go(task);
        recyclerView.scheduleLayoutAnimation();

        Button p = new Button(getContext());
        p.setText(task.getTitle());

        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = path.indexOfChild(p);
                cycle.goTo(i);
                recyclerView.scheduleLayoutAnimation();
                path.removeViews(i + 1, path.getChildCount() - (i + 1));
            }
        });

        this.path.addView(p);
    }

    @Override
    public void onLongClick(View view, com.service.taskdoc.database.business.transfer.Task task) {
        if (task.getRefpcode() != 0) {
            if (((ProjectProgressActivity) getActivity()).project.getPpermission().equals(Projects.OWNER)) {
                menuRefTask = task;
                registerForContextMenu(view);
            }
        } else {
            registerForContextMenu(view);
            menuRefTask = task;
        }
    }

    public boolean canGoBack() {
        return cycle.canGoBack();
    }

    public void back() {
        cycle.back();
        recyclerView.scheduleLayoutAnimation();
        this.path.removeViewAt(this.path.getChildCount() - 1);
    }

    @Override
    public void onChartClick(com.service.taskdoc.database.business.transfer.Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("진행도");

        View view = getLayoutInflater().inflate(R.layout.custom_form_task_seekbar, null);
        TextView textView = view.findViewById(R.id.text);
        SeekBar seekBar = view.findViewById(R.id.seekbar);
        CheckBox checkbox = view.findViewById(R.id.checkbox);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(seekBar.getProgress() + "%");
                task.setPercent(seekBar.getProgress());
                if (seekBar.getProgress() == seekBar.getMax()) {
                    checkbox.setChecked(true);
                } else {
                    checkbox.setChecked(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            int percent;

            @Override
            public void onClick(View view) {

                if (checkbox.isChecked()) {
                    percent = task.getPercent();
                    seekBar.setProgress(100);
                } else {
                    seekBar.setProgress(percent);
                }
            }
        });

        textView.setText(task.getPercent() + "%");
        seekBar.setProgress(task.getPercent());

        builder.setView(view);
        builder.setPositiveButton(SUCCESS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                modify(task);
            }
        });
        builder.show();
    }

    public void onExpandClick(RecyclerView recyclerView, int ptcode) {

        List<MemoVO> memos = new ArrayList<>();

        MemoCycle memoCycle = new MemoCycle(memos);

        MemoService memoService = new MemoService();
        memoService.list(ptcode);
        memoService.work(new NetworkSuccessWork() {
            @Override
            public void work(Object... objects) {
                if (objects.length > 0 && objects[0] instanceof Integer) {
                    int result = (int) objects[0];

                    if (result == -1) {
                        Task task = (Task) getParentFragment();
                    }
                } else if (objects.length > 0 && objects[0] instanceof List) {
                    memos.addAll((List<MemoVO>) objects[0]);
                }
                memoCycle.notifyDataSetChanged();
            }
        });

        memoCycle.setOnClickListener(new MemoCycle.OnClickListener() {
            @Override
            public void onModifyClick(MemoVO memo, String changeText) {
                memoService.update(memo, changeText);
            }

            @Override
            public void onDeleteClick(MemoVO memo) {
                memoService.delete(memos, memo);
            }
        });

        // 스크롤 안에 스크롤이 가능하게 해주는.......
        RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };

        recyclerView.addOnItemTouchListener(mScrollTouchListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(memoCycle);
    }

    @Override
    public void onExpandAddClick(RecyclerView recyclerView, int ptcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        EditText editText = new EditText(getContext());

        builder.setView(editText);
        new KeyboardManager().show(getContext());

        builder.setPositiveButton(SUCCESS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MemoCycle memoCycle = (MemoCycle) recyclerView.getAdapter();
                List<MemoVO> memos = memoCycle.getMemos();

                MemoVO memo = new MemoVO();
                memo.setPtcode(ptcode);
                memo.setMcontents(editText.getText().toString());

                MemoService memoService = new MemoService();
                memoService.insert(memo);
                memoService.work(new NetworkSuccessWork() {
                    @Override
                    public void work(Object... objects) {
                        int result = (int) objects[0];
                        if (result != -1) {
                            memo.setMcode(result);
                            memos.add(memo);
                            memoCycle.notifyDataSetChanged();
                        } else {
                            Task task = (Task) getParentFragment();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton(CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.show();
    }


    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }
}
