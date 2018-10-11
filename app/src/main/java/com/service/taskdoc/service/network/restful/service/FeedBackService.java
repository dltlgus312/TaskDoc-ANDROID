package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.FeedBackVO;
import com.service.taskdoc.service.network.restful.crud.FeedBackCRUD;
import com.service.taskdoc.service.system.support.RequestBuilder;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackService {

    private final String TAG = "FeedBack";

    private NetworkSuccessWork networkSuccessWork;
    private FeedBackCRUD service;

    private List<FeedBackVO> list;

    public FeedBackService() {
        service = RequestBuilder.createService(FeedBackCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public List<FeedBackVO> getList() {
        return list;
    }

    public void setList(List<FeedBackVO> list) {
        this.list = list;
    }

    public void list(int dmcode){
        Call<List<FeedBackVO>> request = service.getList(dmcode);

        request.enqueue(new Callback<List<FeedBackVO>>() {
            @Override
            public void onResponse(Call<List<FeedBackVO>> call, Response<List<FeedBackVO>> response) {
                if(response.body() != null && response.body().size()>0){
                    list.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<FeedBackVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
   }

    public void insert(FeedBackVO vo){
        Call<FeedBackVO> request = service.create(vo);

        request.enqueue(new Callback<FeedBackVO>() {
            @Override
            public void onResponse(Call<FeedBackVO> call, Response<FeedBackVO> response) {
                if(response.body()!=null){
                    list.add(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<FeedBackVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    public void update(FeedBackVO vo){
        Call<Integer> request = service.update(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() != null){
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(FeedBackVO vo){
        Call<Integer> request = service.delete(vo.getFbcode());
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() == 1){
                    list.remove(vo);
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
