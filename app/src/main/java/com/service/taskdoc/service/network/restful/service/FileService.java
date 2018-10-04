package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.transfer.FileVO;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.service.network.restful.crud.FileCRUD;
import com.service.taskdoc.service.network.restful.crud.MemoCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileService {

    private final String TAG = "File";

    private NetworkSuccessWork networkSuccessWork;
    private FileCRUD service;

    public FileService() {
        service = RequestBuilder.createService(FileCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public void list(int dmcode) {
        Call<List<FileVO>> request = service.getFileList(dmcode);
        request.enqueue(new Callback<List<FileVO>>() {
            @Override
            public void onResponse(Call<List<FileVO>> call, Response<List<FileVO>> response) {

                if (response.body().size() > 0) {
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<FileVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int fcode) {
        Call<Integer> request = service.deleteFile(fcode);
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

    public void download(int fcode){
        Call<ResponseBody> request = service.downloadFile(fcode);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null)
                    networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
