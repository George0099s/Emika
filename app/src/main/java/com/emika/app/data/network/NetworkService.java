package com.emika.app.data.network;

import com.emika.app.presentation.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static NetworkService networkService;
    private static final String BASE_URL = Constants.BASIC_URL;
    private Retrofit retrofit;

    private NetworkService(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance(){
        if (networkService == null){
            networkService = new NetworkService();
        }
        return networkService;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
