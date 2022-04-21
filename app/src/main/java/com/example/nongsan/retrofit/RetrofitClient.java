package com.example.nongsan.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit instance;
    public static Retrofit getInstance(String baseUrl) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);
        if (instance == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
}
