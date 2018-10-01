package com.service.taskdoc.service.network.restful.service;


import android.util.Log;

import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.network.restful.crud.UserInfoCRUD;
import com.service.taskdoc.service.system.support.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoService {

    private final String TAG = "UserinfoService";

    private UserInfoCRUD service;

    private NetworkSuccessWork networkSuccessWork;

    public UserInfoService(){
        this.service = RequestBuilder.createService(UserInfoCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork){
        this.networkSuccessWork = networkSuccessWork;
    }

    public void getInfo(String uid){
        Call<UserInfoVO> request = service.getUserinfo(uid);
        request.enqueue(new Callback<UserInfoVO>() {
            @Override
            public void onResponse(Call<UserInfoVO> call, Response<UserInfoVO> response) {
                networkSuccessWork.work(response.body());
            }

            @Override
            public void onFailure(Call<UserInfoVO> call, Throwable t) {
                networkSuccessWork.work(null);
                Log.d(TAG,  t.getMessage());
            }
        });
    }

    public void login(UserInfoVO userInfoVO){
        Call<Integer> request = service.userLogin(userInfoVO);

        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                int result = response.body();

                if(response.body() == 1){
                    Call<UserInfoVO> request = service.getUserinfo(userInfoVO.getUid());
                    request.enqueue(new Callback<UserInfoVO>() {
                        @Override
                        public void onResponse(Call<UserInfoVO> call, Response<UserInfoVO> response) {
                            UserInfoVO userInfoVO = response.body();
                            UserInfo.setUid(userInfoVO.getUid());
                            UserInfo.setUpasswd(userInfoVO.getUpasswd());
                            UserInfo.setUname(userInfoVO.getUname());
                            UserInfo.setUstate(userInfoVO.getUstate());
                            UserInfo.setUphone(userInfoVO.getUphone());
                            networkSuccessWork.work(result);
                        }
                        @Override
                        public void onFailure(Call<UserInfoVO> call, Throwable t) {
                            Log.d(TAG,  t.getMessage());
                        }
                    });
                }else {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG,  t.getMessage());
            }
        });
    }

    public void create(UserInfoVO userInfoVO){
        Call<Integer> request = service.createUser(userInfoVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result = response.body();
                networkSuccessWork.work(result);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(UserInfoVO userInfoVO){
        Call<Integer> request = service.updateUser(userInfoVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result = response.body();

                if(result == 1) {
                    networkSuccessWork.work(result);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
}
