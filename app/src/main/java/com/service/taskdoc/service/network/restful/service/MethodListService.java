package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.business.Methods;
import com.service.taskdoc.database.transfer.MethodListVO;
import com.service.taskdoc.service.network.restful.crud.MethodListCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MethodListService {

    private final String TAG = "MethodListService";

    private NetworkSuccessWork networkSuccessWork;

    private MethodListCRUD service;

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public MethodListService() {
        this.service = RequestBuilder.createService(MethodListCRUD.class);
    }

    public void getList(String uid) {
        Call<List<MethodListVO>> request = service.getMethodList(uid);
        request.enqueue(new Callback<List<MethodListVO>>() {
            @Override
            public void onResponse(Call<List<MethodListVO>> call, Response<List<MethodListVO>> response) {
                List<MethodListVO> list = response.body();
                if (list.size() > 0) {
                    Methods.getMethodLists().clear();
                    Methods.getMethodLists().addAll(list);
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<MethodListVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(MethodListVO vo) {
        Call<Integer> request = service.createMethod(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                networkSuccessWork.work(result);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(MethodListVO oldVo, MethodListVO newVo) {
        Call<Integer> request = service.updateMethod(newVo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    oldVo.setMltitle(newVo.getMltitle());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(MethodListVO vo) {
        Call<Integer> request = service.deleteMethod(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    Methods.getMethodLists().remove(vo);
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
