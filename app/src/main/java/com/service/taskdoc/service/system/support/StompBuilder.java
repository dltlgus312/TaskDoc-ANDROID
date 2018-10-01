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

    private StompClient mStompClient;

    private int pcode;

    public StompBuilder(int pcode, SubscribeListener subscribeListener){

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

                    String message = (String) map.get("message");
                    String type = (String) map.get("type");
                    String object = new Gson().toJson(map.get("object"));
                    subscribeListener.topic(pcode, message, type, object);

                    Log.d("TEST", topicMessage.getPayload());
                });

        mStompClient.connect();
    }

    public <T> void sendMessage(String msg, String type, T t){
        Map<String, Object> map = new HashMap<>();
        map.put("message", msg);
        map.put("type", type);
        map.put("object", t);
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

    public interface SubscribeListener{
        void topic(int pcode, String msg, String type, String json);
    }
}
