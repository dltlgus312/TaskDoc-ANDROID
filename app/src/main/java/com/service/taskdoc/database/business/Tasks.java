package com.service.taskdoc.database.business;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;

import java.util.ArrayList;
import java.util.List;

public class Tasks {

    public static String PUBLIC = "PUBLIC";
    public static String PRIVATE = "PRIVATE";
    public static String OUTPUT = "OUTPUT";

    private List<Task> publicTasks;
    private List<Task> privateTasks;

    private boolean isPubilc = true;

    public Tasks() {
        this.publicTasks = new ArrayList<>();
        this.privateTasks = new ArrayList<>();
    }

    public static boolean isPublic(Task task) {
        boolean isPublic = false;

        if (task.getRefpcode() != 0) {
            isPublic = true;
        }

        return isPublic;
    }

    public static PublicTaskVO publicConverter(Task task) {
        PublicTaskVO vo = new PublicTaskVO();

        vo.setPcode(task.getRefpcode());
        vo.setTcode(task.getCode());
        vo.setTcolor(task.getColor());
        vo.setTsdate(task.getSdate());
        vo.setTedate(task.getEdate());
        vo.setTpercent(task.getPercent());
        vo.setTrefference(task.getRefference());
        vo.setTsequence(task.getSequence());
        vo.setTtitle(task.getTitle());

        return vo;
    }

    public static PrivateTaskVO privateConverter(Task task) {
        PrivateTaskVO vo = new PrivateTaskVO();

        vo.setTcode(task.getReftcode());
        vo.setPtcode(task.getCode());
        vo.setPtcolor(task.getColor());
        vo.setPtsdate(task.getSdate());
        vo.setPtedate(task.getEdate());
        vo.setPtpercent(task.getPercent());
        vo.setPtrefference(task.getRefference());
        vo.setPtsequence(task.getSequence());
        vo.setPttitle(task.getTitle());
        vo.setUid(UserInfo.getUid());

        return vo;
    }

    public List<Task> getPublicTasks() {
        return publicTasks;
    }

    public void setPublicTasks(List<Task> publicTasks) {
        this.publicTasks = publicTasks;
    }

    public List<Task> getPrivateTasks() {
        return privateTasks;
    }

    public void setPrivateTasks(List<Task> privateTasks) {
        this.privateTasks = privateTasks;
    }

    public boolean isPubilc() {
        return isPubilc;
    }

    public void setPubilc(boolean pubilc) {
        isPubilc = pubilc;
    }

    public boolean isPublic() {
        return isPubilc;
    }



    /*
    * Service
    * */
    public void addSort(Task task) {
        if (task.getReftcode() == 0) {
            addPositionTask(task, getPublicTasks());
        } else {
            addPositionTask(task, getPrivateTasks());
        }
    }

    public void addPositionTask(Task task, List<Task> tasks){
        // 최상단 이라면 그냥 추가 하고 리턴..
        if (task.getCode() == task.getRefference()) {
            tasks.add(task);
            return;
        }

        // 같은걸 참조하는 동급 업무중 순서도(시퀀스) 가 제일큰 업무 찾기
        Task max = null;
        for (Task t : tasks) {
            if (t.getRefference() == task.getRefference()) {
                max = t;
            }
        }

        // 찾지 못했다면 부모를 찾아서 부모 바로 아래에 추가..
        if (max == null) {
            for (Task t : tasks) {
                if (t.getCode() == task.getRefference()) {
                    tasks.add(tasks.indexOf(t) + 1, task);
                    return;
                }
            }
        }

        // 찾았다면 그의 자식이 있는지 재귀함수로 찾는다..
        else {
            Task chMax = findMaxTask(max, tasks);
            if (chMax == null) tasks.add(tasks.indexOf(max) + 1, task);
            else tasks.add(tasks.indexOf(chMax) + 1, task);
        }
    }

    public Task findMaxTask(Task task, List<Task> tasks) {
        Task max = null;
        Task findChMax = null;

        for (Task c : tasks) {
            if (task != c && task.getCode() == c.getRefference()) {
                max = c;
            }
        }

        if (max != null) {
            findChMax = findMaxTask(max, tasks);

            if (findChMax == null) return max;
            else return findChMax;
        }
        return null;
    }

}
