package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.service.network.restful.crud.DecisionCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DecisionService {

    private final String TAG = "Decision";

    private NetworkSuccessWork networkSuccessWork;
    private DecisionCRUD service;

    private int crcode;
    private List<DecisionVO> decisionList;

    private InsertListener insertListener;

    public DecisionService(int crcode) {
        this.crcode = crcode;
        service = RequestBuilder.createService(DecisionCRUD.class);
    }

    public void setDecisionList(List<DecisionVO> decisionList){
        this.decisionList = decisionList;
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public InsertListener getInsertListener() {
        return insertListener;
    }

    public void setInsertListener(InsertListener insertListener) {
        this.insertListener = insertListener;
    }





    public void taskList(int tcode) {
        Call<List<DecisionVO>> request = service.getTaskList(tcode);
        request.enqueue(new Callback<List<DecisionVO>>() {
            @Override
            public void onResponse(Call<List<DecisionVO>> call, Response<List<DecisionVO>> response) {
                List<DecisionVO> result = response.body();

                if (result.size() > 0) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<List<DecisionVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void roomList() {
        Call<List<DecisionVO>> request = service.getChatList(crcode);
        request.enqueue(new Callback<List<DecisionVO>>() {
            @Override
            public void onResponse(Call<List<DecisionVO>> call, Response<List<DecisionVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    decisionList.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<DecisionVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(DecisionVO vo) {
        Call<DecisionVO> request = service.createDecision(vo);
        request.enqueue(new Callback<DecisionVO>() {
            @Override
            public void onResponse(Call<DecisionVO> call, Response<DecisionVO> response) {
                if (response.body() != null) {
                    if (insertListener != null) insertListener.success(response.body());
                }
            }

            @Override
            public void onFailure(Call<DecisionVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(DecisionVO vo) {
        Call<Integer> request = service.updateDecision(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int dscode) {
        Call<Integer> request = service.deleteDecision(dscode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                if (result == 1) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public interface InsertListener{
        void success(DecisionVO vo);
    }

}
