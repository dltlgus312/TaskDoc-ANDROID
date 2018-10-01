package com.service.taskdoc.database.business;

import com.service.taskdoc.database.transfer.MethodListVO;

import java.util.ArrayList;
import java.util.List;

public class Methods {
    private static List<MethodListVO> methodLists = new ArrayList<>();

    public static List<MethodListVO> getMethodLists() {
        return methodLists;
    }

    public static void setMethodLists(List<MethodListVO> methodLists) {
        Methods.methodLists = methodLists;
    }
}
