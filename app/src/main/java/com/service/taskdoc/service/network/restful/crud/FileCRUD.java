package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.FileVO;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FileCRUD {
    @GET("/file/{dmcode}")
    Call<List<FileVO>> getFileList(@Path("dmcode") int dmcode);

    @DELETE("/file/{fcode}") // 파일도 같이 삭제
    Call<Integer> deleteFile(@Path("fcode") int fcode);

    @POST("/file/download/{fcode}")
    Call<ResponseBody> downloadFile(@Path("fcode") int fcode);
}
