package com.service.taskdoc.service.system.support;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestBuilder {

//    public static final String URI = "192.168.219.124:8080";
    public static final String URI = "172.20.10.3:8080";
//    public static final String URI = "54.180.26.251";

    public static final String URL = "http://" + URI;

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        return retrofit.create(serviceClass);
    }
}
