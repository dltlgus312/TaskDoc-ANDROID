package com.service.taskdoc.database.business;

public class UserInfo {
    private static String uid;
    private static String upasswd;
    private static String uname;
    private static String ustate;
    private static String uphone;

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        UserInfo.uid = uid;
    }

    public static String getUpasswd() {
        return upasswd;
    }

    public static void setUpasswd(String upasswd) {
        UserInfo.upasswd = upasswd;
    }

    public static String getUname() {
        return uname;
    }

    public static void setUname(String uname) {
        UserInfo.uname = uname;
    }

    public static String getUstate() {
        return ustate;
    }

    public static void setUstate(String ustate) {
        UserInfo.ustate = ustate;
    }

    public static String getUphone() {
        return uphone;
    }

    public static void setUphone(String uphone) {
        UserInfo.uphone = uphone;
    }
}
