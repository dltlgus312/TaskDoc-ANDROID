package com.service.taskdoc.database.business;

import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;

import java.util.ArrayList;
import java.util.Iterator;
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

    public void addSort(Task t) {
        if (t.getReftcode() == 0) {
            // 공용업무 정렬
            if (t.getCode() != t.getRefference()) {
                Task max = null;

                for (Task tt : getPublicTasks()) {
                    if (tt.getRefference() == t.getRefference()) {
                        if (tt.getCode() != tt.getRefference() && max == null) max = tt;
                        else if (max != null && max.getSequence() < tt.getSequence()) max = tt;
                    }
                }

                if (max == null) {
                    for (Task tt : getPublicTasks()) {
                        if (t.getRefference() == tt.getCode()) {
                            getPublicTasks().add(getPublicTasks().indexOf(tt) + 1, t);
                            return;
                        }
                    }
                } else getPublicTasks().add(getPublicTasks().indexOf(max) + 1, t);
            } else {
                getPublicTasks().add(t);
            }
        } else {
            // 개인업무 정렬
            getPrivateTasks().add(t);
        }
    }

}
