package com.service.taskdoc.service.network.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.service.taskdoc.service.system.support.StompBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StompService extends Service implements StompBuilder.SubscribeListener {

    public final static String INSERT = "insert";
    public final static String UPDATE = "update";
    public final static String DELETE = "delete";

    public final static String CHATCONTENTS = "chatcontentsvo";
    public final static String CHATROOMJOIN = "chatroomjoinvo";
    public final static String CHATROOM = "chatroomvo";
    public final static String DECISIONITEM = "decisionitemvo";
    public final static String DECISION = "decisionvo";
    public final static String DOCUMENT = "documentvo";
    public final static String FEEDBACK = "feedbackvo";
    public final static String FILE = "filevo";
    public final static String MEMO = "memovo";
    public final static String METHODBOARD = "methodboardvo";
    public final static String METHODITEM = "methoditemvo";
    public final static String METHODLIST = "methodlistvo";
    public final static String METHOD = "methodvo";
    public final static String NOTICE = "noticevo";
    public final static String PRIVATETASK = "privatetaskvo";
    public final static String PUBLICTASK = "publictaskvo";
    public final static String USERINFO = "userinfovo";
    public final static String VOTER = "votervo";

    private IBinder binder = new MyBinder();
    private Map<Integer, StompBuilder> stompList = new HashMap<>();

    public class MyBinder extends Binder{
        public StompService getMyService(){
            return StompService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String extra = intent.getStringExtra("pcodes");
        List<Integer> pcodes = new Gson().fromJson(extra, List.class);

        for (int i : pcodes){
//            stompList.put(i, new StompBuilder(i, this));
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("FFF", i+"");
                }
            });
            t.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public StompBuilder getStomp(int pcode){
        return stompList.get(pcode);
    }

    public void remove(int pcode){
        StompBuilder stompBuilder = stompList.get(pcode);
        stompBuilder.disconnect();
        stompList.remove(pcode);
    }

    @Override
    public void topic(int pcode, String msg, String type, String json) {

    }
}
