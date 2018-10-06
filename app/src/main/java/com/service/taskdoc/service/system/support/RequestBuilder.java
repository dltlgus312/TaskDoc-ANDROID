package com.service.taskdoc.service.system.support;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestBuilder {

//    public static final String URI = "192.168.219.124:8080";
    public static final String URI = "172.20.10.3:8080";
//    public static final String URI = "54.180.26.251";

    public static final String URL = "http://" + URI;

    public static <S> S createService(Class<S> serviceClass) {
        return new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(serviceClass);
    }

    public static <S> S getDownloadRetrofit(Class<S> serviceClass, OnAttachmentDownloadListener listener) {
        return new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpDownloadClientBuilder(listener).build())
                .build().create(serviceClass);

    }

    public static OkHttpClient.Builder getOkHttpDownloadClientBuilder(OnAttachmentDownloadListener progressListener) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // You might want to increase the timeout
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(0, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(5, TimeUnit.MINUTES);

        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if(progressListener == null) return chain.proceed(chain.request());

                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });

        return httpClientBuilder;
    }



}
