package com.service.taskdoc.database.business;

import com.service.taskdoc.database.business.transfer.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Projects {

    private static List<Project> projectList = new ArrayList<>();
    private static List<Project> projectInviteList  = new ArrayList<>();

    public static String OWNER = "OWNER";
    public static String MEMBER = "MEMBER";
    public static String PROJECTLIST = "projectList";
    public static String PROJECTINVITELIST = "projectInviteList";

    public static void setProject(Project project){
        projectList.add(project);
    }

    public static Project getProject(int i){
        return getProjectList().get(i);
    }

    public static void setProjectInvite(Project project){
        projectInviteList.add(project);
    }

    public static Project getProjectInvite(int i){
        return getProjectInviteList().get(i);
    }

    public static List<Project> getProjectList() {
        return projectList;
    }

    public static void setProjectList(List<Project> projectList) {
        Projects.projectList.addAll(projectList);
    }

    public static List<Project> getProjectInviteList() {
        return projectInviteList;
    }

    public static void setProjectInviteList(List<Project> projectInviteList) {
        Projects.projectInviteList.addAll(projectInviteList);
    }

    public static Map<String, List<Project>> getAllProjectList(){
        Map<String, List<Project>> map = new HashMap<>();

        map.put(PROJECTLIST,getProjectList());
        map.put(PROJECTINVITELIST,getProjectInviteList());
        return map;
    }

    public static void clear(){
        getProjectList().clear();
        getProjectInviteList().clear();
    }

    public static void removeProject(Project project){
        projectList.remove(project);
        projectInviteList.remove(project);
    }
}
