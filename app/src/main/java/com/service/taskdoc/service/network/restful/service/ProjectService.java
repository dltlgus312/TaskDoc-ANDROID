package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.transfer.ProjectVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.network.restful.crud.ProjectCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectService {

    private final String TAG = "ProjectService";

    private ProjectCRUD service;
    private NetworkSuccessWork networkSuccessWork;

    public ProjectService(){
        this.service = RequestBuilder.createService(ProjectCRUD.class);
    }

    public void work(NetworkSuccessWork networkSuccessWork){
        this.networkSuccessWork = networkSuccessWork;
    }

    public void create(ProjectVO projectVO) {
        Map<String, Object> data = new HashMap<>();

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUid(UserInfo.getUid());

        data.put("project", projectVO);
        data.put("userInfo", userInfoVO);

        Call<Integer> request = service.setProject(data);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body() != -1){
                    projectVO.setPcode(response.body());

                    Project p = new Project();

                    p.setPinvite(1);
                    p.setPpermission(Projects.OWNER);
                    p.setPcode(projectVO.getPcode());
                    p.setPtitle(projectVO.getPtitle());
                    p.setPsubtitle(projectVO.getPsubtitle());
                    p.setPsdate(projectVO.getPsdate());
                    p.setPedate(projectVO.getPedate());
                    Projects.setProject(p);
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(Project project, ProjectVO modifyVO) {
        Call<Integer> request = service.putProject(modifyVO);

        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    project.setPtitle(modifyVO.getPtitle());
                    project.setPsubtitle(modifyVO.getPsubtitle());
                    project.setPsdate(modifyVO.getPsdate());
                    project.setPedate(modifyVO.getPedate());
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(Project project) {
        Call<Integer> request = service.delProject(project.getPcode());
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    Projects.removeProject(project);
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
