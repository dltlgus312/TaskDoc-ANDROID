package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.VoterVO;
import com.service.taskdoc.service.network.restful.crud.VoterCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoterService {

    private final String TAG = "Voter";

    private NetworkSuccessWork networkSuccessWork;
    private VoterCRUD service;

    public VoterService() {
        service = RequestBuilder.createService(VoterCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public void list(int dsicode) {
        Call<List<VoterVO>> request = service.getList(dsicode);
        request.enqueue(new Callback<List<VoterVO>>() {
            @Override
            public void onResponse(Call<List<VoterVO>> call, Response<List<VoterVO>> response) {

                if (response.body().size() > 0) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<VoterVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(VoterVO oldVo, VoterVO newVo) {
        Map<String, VoterVO> map = new HashMap<>();
        map.put("oldVo", oldVo);
        map.put("newVo", newVo);

        Call<Integer> request = service.update(map);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(VoterVO vo){
        Call<Integer> request = service.insert(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() != null)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
