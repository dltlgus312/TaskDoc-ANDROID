package com.service.taskdoc.display.custom.custom.dialog.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.service.taskdoc.R;
import com.service.taskdoc.database.business.Methods;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.MethodListVO;
import com.service.taskdoc.database.transfer.MethodVO;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CreateTaskDialog {

    Context context;

    public CreateTaskDialog(Context context) {
        this.context = context;
    }

    public void showChoiceType() {
        if (permision.equals(Projects.OWNER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("유형 선택");

            ArrayAdapter<String> types = new ArrayAdapter<>(
                    context, android.R.layout.simple_selectable_list_item);

            types.add("공용 업무");
            types.add("개인 업무");

            builder.setAdapter(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        showChoiceMethod();
                    } else {
                        privateTask();
                    }
                }
            });

            builder.show();
        } else {
            privateTask();
        }
    }

    void showChoiceMethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("유형 선택");

        ArrayAdapter<String> types = new ArrayAdapter<>(
                context, android.R.layout.simple_selectable_list_item);

        types.add("방법론");
        types.add("직접");

        builder.setAdapter(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    showChoiceMymethod();
                } else {
                    publicTask();
                }
            }
        });

        builder.show();
    }

    void showChoiceMymethod() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("유형 선택");

        ArrayAdapter<String> types = new ArrayAdapter<>(
                context, android.R.layout.simple_selectable_list_item);

        types.add("기본 제공 선택");
        types.add("내 방법론 선택");

        builder.setAdapter(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    primaryMethodTask();
                } else {
                    myMethodTask();
                }
            }
        });
        builder.show();
    }

    void myMethodTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("리스트 선택");

        ArrayAdapter<String> types = new ArrayAdapter<>(
                context, android.R.layout.simple_selectable_list_item);

        for (MethodListVO vo : Methods.getMethodLists()) types.add(vo.getMltitle());

        builder.setAdapter(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                datePicker(Methods.getMethodLists().get(i));
            }
        });

        builder.show();
    }

    void datePicker(MethodListVO vo){

        List<Calendar> selectedDates = null;

        OnSelectDateListener listener = new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendar) {
                methodListCreateTask(vo, calendar.get(0));
            }
        };

        String[] s = project.getPsdate().split("-");

        Calendar sC = Calendar.getInstance();

        sC.set(Integer.parseInt(s[0]), Integer.parseInt(s[1]) - 1, Integer.parseInt(s[2]) - 1);

        DatePickerBuilder builder = new DatePickerBuilder(context, listener)
                .pickerType(CalendarView.ONE_DAY_PICKER).headerColor(R.color.colorPrimary);

        builder.minimumDate(sC);
        DatePicker datePicker = builder.build();
        datePicker.show();

        Toast.makeText(context, "시작 날짜를 선택 해주세요", Toast.LENGTH_SHORT).show();
    }

    void publicTask() {
        showCreateTaskView(true);
    }

    void privateTask() {
        showCreateTaskView(false);
    }




    /*
     * 업무 생성 서비스
     * */

    // 기본 방법론 선택
    void primaryMethodTask() {

    }

    // 내 방법론 리스트 선택
    void methodListCreateTask(MethodListVO vo, Calendar calendar){
        DialogMethodCreateTask.OnPositiveClickListener listener
                = new DialogMethodCreateTask.OnPositiveClickListener() {
            @Override
            public void getTask(List<Task> taskList) {
                if (taskEventListener != null){
                    taskEventListener.methodListCreate(taskList);
                }
            }
        };

        new DialogMethodCreateTask(context)
                .setPcode(project.getPcode())
                .setMbcode(vo.getMbcode())
                .setSdate(calendar)
                .setOnPositiveClickListener(listener)
                .init().show();
    }

    // 개인, 공용 생성
    void showCreateTaskView(boolean isPublic) {
        DialogTaskPicker.OnPositiveClick taskListener = new DialogTaskPicker.OnPositiveClick() {
            @Override
            public void getTask(Task t) {
                // 업무선택후 확인을 누르면....
                DialogTaskParameter.OnClickListener listener =
                        new DialogTaskParameter.OnClickListener() {
                            @Override
                            public void before() {
                                showCreateTaskView(isPublic);
                            }

                            @Override
                            public void dateClick(Button sDate, TextView banner, Button eDate) {
                                if (t != null)
                                    showDatePicker(sDate, banner, eDate, t.getSdate(), t.getEdate());
                                else
                                    showDatePicker(sDate, banner, eDate, project.getPsdate(), project.getPedate());
                            }

                            @Override
                            public void colorClick(Button banner) {
                                showColorPiccker(banner);
                            }

                            @Override
                            public void positiveButton(String title, String sDate, String eDate, String color, Task parentsTask) {
                                if (taskEventListener != null) {
                                    /*
                                     * 업무 생성
                                     * */
                                    if (isPublic) {
                                        PublicTaskVO vo = new PublicTaskVO();
                                        vo.setTtitle(title);
                                        vo.setTsdate(sDate);
                                        vo.setTedate(eDate);
                                        vo.setTcolor(color);
                                        vo.setPcode(project.getPcode());
                                        if (parentsTask != null)
                                            vo.setTrefference(parentsTask.getCode());
                                        taskEventListener.publicTaskCreate(vo);
                                    } else {
                                        PrivateTaskVO vo = new PrivateTaskVO();
                                        if (parentsTask.getReftcode() == 0) {
                                            // 부모가 공용
                                            vo.setPttitle(title);
                                            vo.setPtsdate(sDate);
                                            vo.setPtedate(eDate);
                                            vo.setPtcolor(color);
                                            vo.setTcode(parentsTask.getCode());
                                            vo.setUid(UserInfo.getUid());
                                        } else {
                                            // 부모가 개인
                                            vo.setPttitle(title);
                                            vo.setPtsdate(sDate);
                                            vo.setPtedate(eDate);
                                            vo.setPtcolor(color);
                                            vo.setPtrefference(parentsTask.getCode());
                                            vo.setTcode(parentsTask.getReftcode());
                                            vo.setUid(UserInfo.getUid());
                                        }
                                        taskEventListener.privateTaskCreate(vo);
                                    }
                                }
                            }
                        };
                new DialogTaskParameter(context)
                        .setOnClickListener(listener)
                        .setParentsTask(t).init().show();
            }
        };
        showTaskPicker(isPublic, taskListener);
    }







    /*
     * Show View
     * */

    void showTaskPicker(boolean isPublic, DialogTaskPicker.OnPositiveClick listener) {
        if (isPublic) {
            // 공용 업무만을 들고온다.
            DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, project.getPcode());
            dialogTaskPicker.setOnPositiveClick(listener);
            dialogTaskPicker.setBanner("현재 업무는 최상단(Activity) 입니다.").show();
        } else {
            // 개인업무를 포함한 전부를 들고온다.
            DialogTaskPicker dialogTaskPicker = new DialogTaskPicker(context, project.getPcode(), true);
            dialogTaskPicker.setOnPositiveClick(listener);
            dialogTaskPicker.show();
        }
    }

    void showDatePicker(Button sDate, TextView banner, Button eDate, String sdate, String edate) {
        List<Calendar> selectedDates = null;

        OnSelectDateListener listener = new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendar) {
                Calendar start = calendar.get(0);
                Calendar end = calendar.get(calendar.size() - 1);

                int sY = start.get(Calendar.YEAR);
                int sM = start.get(Calendar.MONTH) + 1;
                int sD = start.get(Calendar.DATE);

                int eY = end.get(Calendar.YEAR);
                int eM = end.get(Calendar.MONTH) + 1;
                int eD = end.get(Calendar.DATE);

                // 선택된 날짜
                sDate.setText(sY + "-" + sM + "-" + sD);
                eDate.setText(eY + "-" + eM + "-" + eD);
                banner.setText("~");
            }
        };

        String[] s = sdate.split("-");
        String[] e = edate.split("-");

        Calendar sC = Calendar.getInstance();
        Calendar eC = Calendar.getInstance();

        sC.set(Integer.parseInt(s[0]), Integer.parseInt(s[1]) - 1, Integer.parseInt(s[2]) - 1);
        eC.set(Integer.parseInt(e[0]), Integer.parseInt(e[1]) - 1, Integer.parseInt(e[2]));

        DatePickerBuilder builder = new DatePickerBuilder(context, listener)
                .pickerType(CalendarView.RANGE_PICKER).headerColor(R.color.colorPrimary);

        builder.minimumDate(sC);
        builder.maximumDate(eC);
        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    void showColorPiccker(Button banner) {
        ColorPicker colorPicker = new ColorPicker(getActivity());


        if (!banner.getText().toString().equals("") && banner.getText().toString().length() > 6){
            int color = 0xff000000;
            color += Integer.parseInt(banner.getText().toString().substring(2), 16);
            colorPicker.setDefaultColorButton(color);
        }

        colorPicker.show();

        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                // put code
                banner.setText(Integer.toHexString(color).substring(2));
                banner.setBackgroundColor(color);
            }

            @Override
            public void onCancel() {
                // put code
            }
        });
    }





    /*
     * RefData
     * */
    String permision;

    Project project;

    Activity activity;

    public String getPermision() {
        return permision;
    }

    public CreateTaskDialog setPermision(String permision) {
        this.permision = permision;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public CreateTaskDialog setProject(Project project) {
        this.project = project;
        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    public CreateTaskDialog setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    /*
     * Listener
     * */
    TaskEventListener taskEventListener;

    public TaskEventListener getTaskEventListener() {
        return taskEventListener;
    }

    public CreateTaskDialog setTaskEventListener(TaskEventListener taskEventListener) {
        this.taskEventListener = taskEventListener;
        return this;
    }

    public interface TaskEventListener {
        void publicTaskCreate(PublicTaskVO vo);

        void privateTaskCreate(PrivateTaskVO vo);

        void methodListCreate(List<Task> vos);
    }

}
