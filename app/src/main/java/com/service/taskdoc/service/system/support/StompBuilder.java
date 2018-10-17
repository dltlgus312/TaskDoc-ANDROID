package com.service.taskdoc.service.system.support;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.database.transfer.ChatContentsVO;
import com.service.taskdoc.database.transfer.NoticeVO;
import com.service.taskdoc.database.transfer.ProjectVO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class StompBuilder{

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
    public final static String PUBLICTASKS = "publictasks";
    public final static String USERINFO = "userinfovo";
    public final static String VOTER = "votervo";
    public final static String PROJECT = "projectvo";
    public final static String PROJECTJOIN = "projectjoinvo";

    private StompClient mStompClient;

    private SubscribeListener subscribeListener;

    private int pcode;

    private final String MESSAGE = "message";
    private final String TYPE = "type";
    private final String OBJECT = "object";

    public StompBuilder(int pcode){

        this.pcode = pcode;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.JWS, "ws://" + RequestBuilder.URI + "/goStomp/websocket");
        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            break;
                        case ERROR:
                            Log.e("TEST", "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                    }
                });

        mStompClient.topic("/project/" + pcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Map<String, Object> map = new Gson().fromJson(topicMessage.getPayload(), Map.class);

                    String message = (String) map.get(MESSAGE);
                    String type = (String) map.get(TYPE);
                    String object = new Gson().toJson(map.get(OBJECT));
                    if (subscribeListener != null ) subscribeListener.topic(message, type, object);

                    Log.d("TEST", topicMessage.getPayload());
                });

    }

    public void connect(){
        mStompClient.connect();
    }

    public void sendMessage(String msg, String type, Object object){
        Map<String, Object> map = new HashMap<>();
        map.put(MESSAGE, msg);
        map.put(TYPE, type);
        map.put(OBJECT, object);
        String serialize = new Gson().toJson(map);
        mStompClient.send("/app/project/"+pcode, serialize)
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d("TEST", "STOMP echo send successfully");
                }, throwable -> {
                    Log.e("TEST", "Error send STOMP echo", throwable);
                });
    }

    public void disconnect(){
        mStompClient.disconnect();
    }


    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public SubscribeListener getSubscribeListener() {
        return subscribeListener;
    }

    public void setSubscribeListener(SubscribeListener subscribeListener) {
        this.subscribeListener = subscribeListener;
    }

    public int getPcode() {
        return pcode;
    }

    public void setPcode(int pcode) {
        this.pcode = pcode;
    }

    public interface SubscribeListener{
        void topic(String msg, String type, String bject);
    }
}
