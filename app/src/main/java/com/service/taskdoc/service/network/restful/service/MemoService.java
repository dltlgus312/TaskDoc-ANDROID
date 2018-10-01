package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.service.network.restful.crud.MemoCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.nio.file.Path;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemoService {

    private final String TAG = "Memo";

    private NetworkSuccessWork networkSuccessWork;
    private MemoCRUD service;

    public MemoService() {
        service = RequestBuilder.createService(MemoCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public void list(int ptcode) {
        Call<List<MemoVO>> request = service.getMemoList(ptcode);
        request.enqueue(new Callback<List<MemoVO>>() {
            @Override
            public void onResponse(Call<List<MemoVO>> call, Response<List<MemoVO>> response) {
                List<MemoVO> result = response.body();

                if (result.size() > 0) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<List<MemoVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(MemoVO vo) {
        Call<Integer> request = service.createMemo(vo);
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

    public void update(MemoVO vo, String newText) {
        String oldText = vo.getMcontents();
        vo.setMcontents(newText);
        Call<Integer> request = service.updateMemo(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();

                if (result == 1) {

                } else {
                    vo.setMcontents(oldText);
                    networkSuccessWork.work(result);
                }
                networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(List<MemoVO> memos, MemoVO memo) {
        Call<Integer> request = service.deleteMemo(memo.getMcode());
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                if (result == 1) {
                    memos.remove(memo);
                    networkSuccessWork.work();
                }else {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
