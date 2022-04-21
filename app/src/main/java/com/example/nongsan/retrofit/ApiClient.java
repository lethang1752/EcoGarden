package com.example.nongsan.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BaseUrl = "http://ecogarden.ml/";
    private static Retrofit retrofit;

    public static Retrofit getApiClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);
        if(retrofit==null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder().baseUrl(BaseUrl).
                    addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return  retrofit;
    }
}
