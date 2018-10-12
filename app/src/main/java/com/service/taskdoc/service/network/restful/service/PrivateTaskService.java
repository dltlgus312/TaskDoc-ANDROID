package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.business.Tasks;
import com.service.taskdoc.database.business.transfer.Task;
import com.service.taskdoc.database.transfer.PrivateTaskVO;
import com.service.taskdoc.service.network.restful.crud.PrivateTaskCRUD;
import com.service.taskdoc.service.system.support.RequestBuilder;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivateTaskService {

    private final String TAG = "PrivateTaskService";

    private PrivateTaskCRUD service;

    private NetworkSuccessWork networkSuccessWork;

    Tasks tasks;

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public void work(NetworkSuccessWork networkSuccessWork){
        this.networkSuccessWork = networkSuccessWork;
    }

    public PrivateTaskService(){
        service = RequestBuilder.createService(PrivateTaskCRUD.class);
    }

    public void list(int tcode){
        Call<List<PrivateTaskVO>> request = service.getPrivateTaskList(tcode);
        request.enqueue(new Callback<List<PrivateTaskVO>>() {
            @Override
            public void onResponse(Call<List<PrivateTaskVO>> call, Response<List<PrivateTaskVO>> response) {
                List<PrivateTaskVO> result = response.body();
                if(result.size() > 0){
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<List<PrivateTaskVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void list(String uid){
        Call<List<PrivateTaskVO>> request = service.getPrivateTaskList(uid);
        request.enqueue(new Callback<List<PrivateTaskVO>>() {
            @Override
            public void onResponse(Call<List<PrivateTaskVO>> call, Response<List<PrivateTaskVO>> response) {
                if(response.body() != null && response.body().size() > 0){
                    List<PrivateTaskVO> result = response.body();

                    for (PrivateTaskVO vo : result) {
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
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<PrivateTaskVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void create(PrivateTaskVO vo){
        Call<Integer> request = service.createPrivateTask(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() != -1){
                    Call<PrivateTaskVO> requestView = service.getPrivateTaskView(response.body());
                    requestView.enqueue(new Callback<PrivateTaskVO>() {
                        @Override
                        public void onResponse(Call<PrivateTaskVO> call, Response<PrivateTaskVO> response) {
                            if (response.body() != null){
                                Task t = new Task();

                                PrivateTaskVO privateTaskVO = response.body();

                                t.setCode(privateTaskVO.getPtcode());
                                t.setTitle(privateTaskVO.getPttitle());
                                t.setColor(privateTaskVO.getPtcolor());
                                t.setSdate(privateTaskVO.getPtsdate());
                                t.setEdate(privateTaskVO.getPtedate());
                                t.setPercent(privateTaskVO.getPtpercent());
                                t.setRefference(privateTaskVO.getPtrefference());
                                t.setSequence(privateTaskVO.getPtsequence());
                                t.setReftcode(privateTaskVO.getTcode());

                                tasks.addSort(t);
                                networkSuccessWork.work();
                            }
                        }

                        @Override
                        public void onFailure(Call<PrivateTaskVO> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(PrivateTaskVO privateTaskVO){
        Call<Integer> request = service.updatePrivateTask(privateTaskVO);
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

    public void delete(int ptcode){
        Call<Integer> request = service.deletePrivateTask(ptcode);
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
