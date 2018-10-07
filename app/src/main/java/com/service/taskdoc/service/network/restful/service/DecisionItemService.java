package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.DecisionItemVO;
import com.service.taskdoc.service.network.restful.crud.DecisionItemCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DecisionItemService {

    private final String TAG = "DecisionItem";

    private NetworkSuccessWork networkSuccessWork;
    private DecisionItemCRUD service;

    private List<DecisionItemVO> list;

    public DecisionItemService() {
        service = RequestBuilder.createService(DecisionItemCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public List<DecisionItemVO> getList() {
        return list;
    }

    public void setList(List<DecisionItemVO> list) {
        this.list = list;
    }








    public void list(int dscode) {
        Call<List<DecisionItemVO>> request = service.getItemList(dscode);
        request.enqueue(new Callback<List<DecisionItemVO>>() {
            @Override
            public void onResponse(Call<List<DecisionItemVO>> call, Response<List<DecisionItemVO>> response) {
                if (response.body().size() > 0 && list != null && networkSuccessWork != null) {
                    list.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<DecisionItemVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(List<DecisionItemVO> vos) {
        Call<List<DecisionItemVO>> request = service.insertItem(vos);
        request.enqueue(new Callback<List<DecisionItemVO>>() {
            @Override
            public void onResponse(Call<List<DecisionItemVO>> call, Response<List<DecisionItemVO>> response) {
                if (response.body() != null && response.body().size() > 0)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<List<DecisionItemVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(DecisionItemVO vo) {
        Call<Integer> request = service.updateItem(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int dsicode) {
        Call<Integer> request = service.deleteItem(dsicode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
