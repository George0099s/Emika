package com.emika.app.data.network.networkManager.calendar;

import android.util.Log;

import com.emika.app.data.network.api.CalendarApi;
import com.emika.app.data.network.callback.AuthCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.pojo.singIn.ModelAuth;
import com.emika.app.data.network.pojo.task.Model;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.data.network.pojo.user.Payload;
import com.emika.app.presentation.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarNetworkManager {
    private static final String TAG = "CalendarNetworkManager";
    private String token;

    public CalendarNetworkManager(String token) {
        this.token = token;
    }

    public void getAllTask(TaskCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<Model> call = service.fetchAllTasks(token);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
                if (response.body() != null) {
                    Model model = response.body();

                    List<PayloadTask> taskList = model.getPayloadTask();
                    if (taskList != null)
                    callback.setTask(taskList);
                    else callback.setTask(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void addTask(TaskCallback callback, JSONObject task) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<Model> call = service.addTask(token, task);
        Log.d(TAG, "addTask: " + call.request().url());
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.body() != null) {
//                    Model model = response.body();
//
//                    List<PayloadTask> taskList = model.getPayloadTask();
//                    if (taskList != null)
//                        callback.setTask(taskList);
//                    else callback.setTask(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
//    public void updateTask(PayloadTask task) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASIC_URL) // Адрес сервера
//                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
//                .build();
//
//        CalendarApi service = retrofit.create(CalendarApi.class);
//        Call<Model> call = service.updateTask(token, task);
//        Log.d(TAG, "getAllTask: " + call.request());
//        call.enqueue(new Callback<Model>() {
//            @Override
//            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
//                Log.d(TAG, "onResponse: " + response.body());
//                if (response.body() != null) {
//
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
//                Log.d(TAG, t.getMessage().toString());
//            }
//        });
//    }
}
