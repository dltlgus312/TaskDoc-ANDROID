package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.database.transfer.PublicTaskVO;
import com.service.taskdoc.service.network.restful.crud.PublicTaskCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicTaskService {

    private final String TAG = "PublicTaskService";

    private PublicTaskCRUD service;

    private NetworkSuccessWork networkSuccessWork;

    private Tasks tasks;

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public void work(NetworkSuccessWork networkSuccessWork){
        this.networkSuccessWork = networkSuccessWork;
    }

    public PublicTaskService(){
        service = RequestBuilder.createService(PublicTaskCRUD.class);
    }

    public void list(int pcode){
        Call<List<PublicTaskVO>> request = service.getPublictaskList(pcode);
        request.enqueue(new Callback<List<PublicTaskVO>>() {
            @Override
            public void onResponse(Call<List<PublicTaskVO>> call, Response<List<PublicTaskVO>> response) {
                if(response.body().size() > 0){

                    for (PublicTaskVO vo : response.body()) {
                        Task t = new Task();

                        PublicTaskVO v = vo;
                        t.setCode(v.getTcode());
                        t.setTitle(v.getTtitle());
                        t.setColor(v.getTcolor());
                        t.setSdate(v.getTsdate());
                        t.setEdate(v.getTedate());
                        t.setPercent(v.getTpercent());
                        t.setRefference(v.getTrefference());
                        t.setSequence(v.getTsequence());
                        t.setRefpcode(v.getPcode());

                        tasks.addSort(t);
                    }
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<PublicTaskVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void listAll(int pcode){
        Call<Map<String, Object>> request = service.getListAll(pcode, UserInfo.getUid());
        request.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if(response.body() != null){

                    Map<String, Object> map = response.body();

                    Type publicType = new TypeToken<ArrayList<PublicTaskVO>>() {
                    }.getType();
                    Type privateType = new TypeToken<ArrayList<ArrayList<PrivateTaskVO>>>() {
                    }.getType();

                    Gson gson = new Gson();

                    List<PublicTaskVO> publicVos = gson.fromJson(gson.toJson(map.get("publicTaskList")), publicType);
                    List<List<PrivateTaskVO>> privateVos = gson.fromJson(gson.toJson(map.get("privateTaskList")), privateType);

                    for (PublicTaskVO vo : publicVos) {
                        Task t = new Task();

                        PublicTaskVO v = (PublicTaskVO) vo;
                        t.setCode(v.getTcode());
                        t.setTitle(v.getTtitle());
                        t.setColor(v.getTcolor());
                        t.setSdate(v.getTsdate());
                        t.setEdate(v.getTedate());
                        t.setPercent(v.getTpercent());
                        t.setRefference(v.getTrefference());
                        t.setSequence(v.getTsequence());
                        t.setRefpcode(v.getPcode());

                        tasks.addSort(t);
                    }

                    for (List<PrivateTaskVO> list : privateVos) {

                        for (PrivateTaskVO vo : list){
                            Task t = new Task();

                            PrivateTaskVO v = (PrivateTaskVO) vo;
                            t.setCode(v.getPtcode());
                            t.setTitle(v.getPttitle());
                            t.setColor(v.getPtcolor());
                            t.setSdate(v.getPtsdate());
                            t.setEdate(v.getPtedate());
                            t.setPercent(v.getPtpercent());
                            t.setRefference(v.getPtrefference());
                            t.setSequence(v.getPtsequence());
                            t.setReftcode(v.getTcode());

                            tasks.getPrivateTasks().add(t);
                        }

                    }
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void create(PublicTaskVO publicTaskVO){
        Call<Integer> request = service.createPublicTask(publicTaskVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result = response.body();
                networkSuccessWork.work(result);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(PublicTaskVO publicTaskVO){
        Call<Integer> request = service.updatePublicTask(publicTaskVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result = response.body();
                networkSuccessWork.work(result);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int tcode){
        Call<Integer> request = service.deletePublicTask(tcode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result = response.body();
                networkSuccessWork.work(result);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
