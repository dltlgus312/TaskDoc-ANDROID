package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.MethodBoardVO;
import com.service.taskdoc.service.network.restful.crud.MethodBoardCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MethodBoardService {

    private final String TAG = "MethodBoard";

    private NetworkSuccessWork networkSuccessWork;
    private MethodBoardCRUD service;

    public MethodBoardService(){
        service = RequestBuilder.createService(MethodBoardCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork){
        this.networkSuccessWork = networkSuccessWork;
    }

    public void list(){
        Call<List<MethodBoardVO>> request = service.getMethodBoardList();
        request.enqueue(new Callback<List<MethodBoardVO>>() {
            @Override
            public void onResponse(Call<List<MethodBoardVO>> call, Response<List<MethodBoardVO>> response) {
                networkSuccessWork.work(response.body());
            }

            @Override
            public void onFailure(Call<List<MethodBoardVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void myList(String uid){
        Call<List<MethodBoardVO>> request = service.getMethodBoardMyList(uid);
        request.enqueue(new Callback<List<MethodBoardVO>>() {
            @Override
            public void onResponse(Call<List<MethodBoardVO>> call, Response<List<MethodBoardVO>> response) {
                networkSuccessWork.work(response.body());
            }

            @Override
            public void onFailure(Call<List<MethodBoardVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void view(int mbcode){
        Call<MethodBoardVO> request = service.getMethodBoard(mbcode);
        request.enqueue(new Callback<MethodBoardVO>() {
            @Override
            public void onResponse(Call<MethodBoardVO> call, Response<MethodBoardVO> response) {
                networkSuccessWork.work(response.body());
            }

            @Override
            public void onFailure(Call<MethodBoardVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(MethodBoardVO vo){
        Call<Integer> request = service.createMethodBoard(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();

                if(result != -1){
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(MethodBoardVO vo){
        Call<Integer> request = service.updateMethodBoard(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();

                if(result == 1){
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int mbcode){
        Call<Integer> request = service.deleteMethodBoard(mbcode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();

                if(result == 1){
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
}
