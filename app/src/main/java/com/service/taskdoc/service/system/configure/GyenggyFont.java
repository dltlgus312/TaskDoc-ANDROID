package com.service.taskdoc.service.system.configure;

import android.app.Application;

public class GyenggyFont extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/gynggy.ttf");
    }
}
