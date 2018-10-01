package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.MemoVO;
import com.service.taskdoc.service.network.restful.crud.DocumentCRUD;
import com.service.taskdoc.service.network.restful.crud.MemoCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class DocumentService {

    private final String TAG = "Document";

    private List<DocumentVO> documentList;


    private NetworkSuccessWork networkSuccessWork;
    private DocumentCRUD service;

    private int crcode;

    public DocumentService(int crcode) {
        this.crcode = crcode;
        service = RequestBuilder.createService(DocumentCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork) {
        this.networkSuccessWork = networkSuccessWork;
    }

    public void setDocumentList(List<DocumentVO> documentList){
        this.documentList = documentList;
    }

    public void taskList(int tcode) {
        Call<List<DocumentVO>> request = service.getDocumentTaskList(tcode);
        request.enqueue(new Callback<List<DocumentVO>>() {
            @Override
            public void onResponse(Call<List<DocumentVO>> call, Response<List<DocumentVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    documentList.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<DocumentVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void roomList() {
        Call<List<DocumentVO>> request = service.getDocumentChatList(crcode);
        request.enqueue(new Callback<List<DocumentVO>>() {
            @Override
            public void onResponse(Call<List<DocumentVO>> call, Response<List<DocumentVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    documentList.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<DocumentVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void userList() {
        Call<List<DocumentVO>> request = service.getDocumentUserList(UserInfo.getUid());
        request.enqueue(new Callback<List<DocumentVO>>() {
            @Override
            public void onResponse(Call<List<DocumentVO>> call, Response<List<DocumentVO>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    documentList.addAll(response.body());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<List<DocumentVO>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void insert(RequestBody[] file, DocumentVO vo) {
        Call<Integer> request = service.uploadDocument(file, vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                if (result != -1) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(DocumentVO vo) {
        Call<Integer> request = service.updateDocument(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                if (result == 1) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void move(DocumentVO vo) {
        Call<Integer> request = service.moveDocument(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                if (result == 1) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void copy(DocumentVO vo) {
        Call<Integer> request = service.copyDocument(vo);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();
                if (result == 1) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(int dmcode) {
        Call<Integer> request = service.deleteDelete(dmcode);
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

}
