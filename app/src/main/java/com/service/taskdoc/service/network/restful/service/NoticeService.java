package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.NoticeVO;
import com.service.taskdoc.service.network.restful.crud.NoticeCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeService {

    private final String TAG = "Notice";

    private NetworkSuccessWork networkSuccessWork;
    private NoticeCRUD service;

    private int pcode;

    private List<NoticeVO> noticeList;

    public NoticeService(int pcode) {
        this.pcode = pcode;
        service = RequestBuilder.createService(NoticeCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public List<NoticeVO> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(List<NoticeVO> noticeList) {
        this.noticeList = noticeList;
    }

    public void list(){
        Call<List<NoticeVO>> request = service.getNoticeList(pcode);

        request.enqueue(new Callback<List<NoticeVO>>() {
            @Override
            public void onResponse(Call<List<NoticeVO>> call, Response<List<NoticeVO>> response) {
                if(response.body() != null && response.body().size()>0){
                    noticeList.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<NoticeVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
   }

    public void view(int ncode){
        Call<NoticeVO> request = service.getNotice(ncode);

        request.enqueue(new Callback<NoticeVO>() {
            @Override
            public void onResponse(Call<NoticeVO> call, Response<NoticeVO> response) {
                if(response.body()!=null){
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<NoticeVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    public void create(NoticeVO noticeVO){
        Call<NoticeVO> request = service.createNotice(noticeVO);
        request.enqueue(new Callback<NoticeVO>() {
            @Override
            public void onResponse(Call<NoticeVO> call, Response<NoticeVO> response) {
                if(response.body() != null){
                    networkSuccessWork.work(response.body());
                }
            }

            @Override
            public void onFailure(Call<NoticeVO> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(NoticeVO noticeVO){
        Call<Integer> request = service.updateNotice(noticeVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() == 1){
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int ncode){
        Call<Integer> request = service.deleteNotice(ncode);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() == 1){
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
