package com.service.taskdoc.service.network.restful.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.taskdoc.database.business.Projects;
import com.service.taskdoc.database.business.UserInfo;
import com.service.taskdoc.database.business.transfer.Project;
import com.service.taskdoc.database.business.transfer.UserInfos;
import com.service.taskdoc.database.transfer.ProjectJoinVO;
import com.service.taskdoc.database.transfer.ProjectVO;
import com.service.taskdoc.database.transfer.UserInfoVO;
import com.service.taskdoc.service.network.restful.crud.ProjectJoinCRUD;
import com.service.taskdoc.service.system.support.listener.NetworkSuccessWork;
import com.service.taskdoc.service.system.support.RequestBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectJoinService {

    private final String TAG = "ProjectjoinService";

    private ProjectJoinCRUD service;
    private NetworkSuccessWork networkSuccessWork;

    private List<UserInfos> userInfosList;

    public ProjectJoinService() {
        this.service = RequestBuilder.createService(ProjectJoinCRUD.class);
    }

    public List<UserInfos> getUserInfosList() {
        return userInfosList;
    }

    public void setUserInfosList(List<UserInfos> userInfosList) {
        this.userInfosList = userInfosList;
    }

    public void work(NetworkSuccessWork networkSuccessWork){
        this.networkSuccessWork = networkSuccessWork;
    }

    public void insert(ProjectJoinVO projectJoinVO){
        Call<Integer> request = service.setProjectJoin(projectJoinVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void update(Project project) {
        ProjectJoinVO projectJoinVO = new ProjectJoinVO();

        projectJoinVO.setPcode(project.getPcode());
        projectJoinVO.setPinvite(project.getPinvite());
        projectJoinVO.setPpermission(project.getPpermission());
        projectJoinVO.setUid(UserInfo.getUid());

        Call<Integer> request = service.putProjectJoin(projectJoinVO);
        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    Projects.removeProject(project);
                    Projects.setProject(project);
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

        Call<Integer> request = null;

        ProjectJoinVO projectJoinVO = new ProjectJoinVO();
        projectJoinVO.setPcode(project.getPcode());
        projectJoinVO.setPinvite(project.getPinvite());
        projectJoinVO.setPpermission(project.getPpermission());
        projectJoinVO.setUid(UserInfo.getUid());

        request = service.delProjectJoin(projectJoinVO);
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

    public void selectProject(){

        Projects.clear();

        Call<Map<String, Object>> result = service.getProjects(UserInfo.getUid());
        result.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();

                Type projectType = new TypeToken<ArrayList<ProjectVO>>(){}.getType();
                Type projectJoinType = new TypeToken<ArrayList<ProjectJoinVO>>(){}.getType();

                Gson gson = new Gson();

                List<ProjectVO> projectVO = gson.fromJson(gson.toJson(map.get("projectList")), projectType);
                List<ProjectJoinVO> projectJoinVO = gson.fromJson(gson.toJson(map.get("projectJoinList")), projectJoinType);

                List<Project> projectList = new ArrayList<>();
                List<Project> projectInviteList = new ArrayList<>();

                for(int i=0;i<projectVO.size();i++){

                    ProjectVO project = projectVO.get(i);

                    Project p = new Project();
                    p.setPcode(project.getPcode());
                    p.setPtitle(project.getPtitle());
                    p.setPsubtitle(project.getPsubtitle());
                    p.setPsdate(project.getPsdate());
                    p.setPedate(project.getPedate());

                    ProjectJoinVO projectJoin = projectJoinVO.get(i);
                    p.setPpermission(projectJoin.getPpermission());
                    p.setPinvite(projectJoin.getPinvite());

                    if (p.getPinvite() == 1){
                        projectList.add(p);
                    }else{
                        projectInviteList.add(p);
                    }

                }

                Projects.setProjectList(projectList);
                Projects.setProjectInviteList(projectInviteList);

                networkSuccessWork.work();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.d(TAG,  t.getMessage());
            }
        });
    }


    public void selectProjectJoinUsers(int pcode){
        Call<Map<String, Object>> request = service.getUsers(pcode);
        request.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.body() != null && response.body().size() != 0){

                    Map<String, Object> map = (Map<String, Object>) response.body();

                    Type userInfoType = new TypeToken<ArrayList<UserInfoVO>>(){}.getType();
                    Type projectJoinType = new TypeToken<ArrayList<ProjectJoinVO>>(){}.getType();

                    Gson gson = new Gson();

                    List<UserInfoVO> userInfoVos = gson.fromJson(gson.toJson(map.get("userInfoList")), userInfoType);
                    List<ProjectJoinVO> projectJoinVos = gson.fromJson(gson.toJson(map.get("projectJoinList")), projectJoinType);

                    for (ProjectJoinVO vo : projectJoinVos){
                        for(UserInfoVO v : userInfoVos){
                            if(vo.getUid().equals(v.getUid())){
                                UserInfos userInfos = new UserInfos();

                                userInfos.setId(v.getUid());
                                userInfos.setName(v.getUname());
                                userInfos.setInvite(vo.getPinvite());
                                userInfos.setPermission(vo.getPpermission());
                                userInfos.setPhone(v.getUphone());
                                userInfos.setState(v.getUstate());

                                userInfosList.add(userInfos);
                            }
                        }
                    }
                    networkSuccessWork.work();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }
}
