package com.service.taskdoc.service.network.restful.crud;

import com.service.taskdoc.database.transfer.DocumentVO;
import com.service.taskdoc.database.transfer.MemoVO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DocumentCRUD {
    @GET("/document/task/{tcode}")
    Call<List<DocumentVO>> getDocumentTaskList(@Path("tcode") int tcode);

    @GET("/document/room/{crcode}")
    Call<List<DocumentVO>> getDocumentChatList(@Path("crcode") int crcode);

    @GET("/document/user/{uid}")
    Call<List<DocumentVO>> getDocumentUserList(@Path("uid") String uid);

    @Multipart
    @POST("/document/upload/doc")
    Call<DocumentVO> uploadDocument(@Part MultipartBody.Part[] multipart,
                                 @Part("dmtitle") RequestBody dmtitle,
                                 @Part("dmcontents") RequestBody dmcontents,
                                 @Part("uid") RequestBody uid,
                                 @Part("tcode") RequestBody tcode,
                                 @Part("crcode") RequestBody crcode);

    @PUT("/document/move") // 파일도 이동된다.
    Call<Integer> moveDocument(@Body DocumentVO documentVO);

    @PUT("/document/copy") // tcode, uid(복사를 원하는자의 id) 를 수정 전송
    Call<DocumentVO> copyDocument(@Body DocumentVO documentVO);

    @PUT("/document") // Document의 제목이나 내용 변경
    Call<Integer> updateDocument(@Body DocumentVO documentVO);

    @DELETE("/document/{dmcode}") // 파일도 같이 삭제
    Call<Integer> deleteDelete(@Path("dmcode") int dmcode);
}
