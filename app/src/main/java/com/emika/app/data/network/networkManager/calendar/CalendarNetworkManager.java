package com.emika.app.data.network.networkManager.calendar;

import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.CalendarApi;
import com.emika.app.data.network.api.EpicLinksApi;
import com.emika.app.data.network.api.MemberApi;
import com.emika.app.data.network.api.ProjectApi;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.pojo.epiclinks.ModelEpicLinks;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.ModelShortMember;
import com.emika.app.data.network.pojo.member.PayloadMember;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.ModelProject;
import com.emika.app.data.network.pojo.project.ModelSection;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.task.Model;
import com.emika.app.data.network.pojo.task.ModelTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.presentation.utils.Constants;

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
    private NetworkService networkService = NetworkService.getInstance();
    public CalendarNetworkManager(String token) {
        this.token = token;
    }

    public void getAllTask(TaskListCallback callback) {
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
                        callback.setTaskList(taskList);
                    else callback.setTaskList(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void addTask(TaskCallback callback, PayloadTask task) {
        Retrofit retrofit = networkService.getRetrofit();

        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelTask> call = service.addTask(token, task.getName(), task.getProjectId(), task.getPlanDate(), task.getDeadlineDate(), task.getAssignee(), String.valueOf(task.getDuration()),
                task.getDescription(), task.getPriority(), task.getSectionId());

        call.enqueue(new Callback<ModelTask>() {
            @Override
            public void onResponse(retrofit2.Call<ModelTask> call, Response<ModelTask> response) {
                if (response.body() != null) {
                    ModelTask model = response.body();
                    PayloadTask task = model.getPayloadTask();
                    if (task != null)
                        callback.getAddedTask(task);
                    else
                        callback.getAddedTask(new PayloadTask());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelTask> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void updateTask(PayloadTask task)  {
        Retrofit retrofit = networkService.getRetrofit();
        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelTask> call = service.updateTask(task.getId(), token, task.getPlanDate(), task.getName(),  task.getProjectId(), task.getDeadlineDate(), task.getDuration(),
                task.getDurationActual(), task.getStatus(), task.getDescription(), task.getPriority());
        Log.d(TAG, "updateTask: " + call.request().url());
        call.enqueue(new Callback<ModelTask>() {
            @Override
            public void onResponse(retrofit2.Call<ModelTask> call, Response<ModelTask> response) {
                if (response.body() != null) {
                    ModelTask model = response.body();
                    Log.d(TAG, "onResponse: " + model.getOk());
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelTask> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void getAllMembers(ShortMemberCallback callback)  {
        Retrofit retrofit = networkService.getRetrofit();
        MemberApi service = retrofit.create(MemberApi.class);
        Call<ModelShortMember> call = service.getAllMembers(token);
        call.enqueue(new Callback<ModelShortMember>() {
            @Override
            public void onResponse(retrofit2.Call<ModelShortMember> call, Response<ModelShortMember> response) {
                if (response.body() != null) {
                    ModelShortMember model = response.body();
                    List<PayloadShortMember> members = model.getPayload();
                    if (members != null)
                    callback.allMembers(members);
                    else callback.allMembers(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelShortMember> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void getAllProjects(ProjectsCallback callback)  {
        Retrofit retrofit = networkService.getRetrofit();
        ProjectApi service = retrofit.create(ProjectApi.class);
        Call<ModelProject> call = service.getAllProjects(token);
        call.enqueue(new Callback<ModelProject>() {
            @Override
            public void onResponse(retrofit2.Call<ModelProject> call, Response<ModelProject> response) {
                if (response.body() != null) {
                    ModelProject model = response.body();
                    List<PayloadProject> projects = model.getPayload();
                    if (projects != null)
                        callback.getProjects(projects);
                    else callback.getProjects(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelProject> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void getAllSections(ProjectsCallback callback)  {
        Retrofit retrofit = networkService.getRetrofit();
        ProjectApi service = retrofit.create(ProjectApi.class);
        Call<ModelSection> call = service.getAllSections(token);
        Log.d(TAG, "getAllSections: " + call.request().url());
        call.enqueue(new Callback<ModelSection>() {
            @Override
            public void onResponse(retrofit2.Call<ModelSection> call, Response<ModelSection> response) {
                if (response.body() != null) {
                    ModelSection model = response.body();
                    List<PayloadSection> sections = model.getPayload();
                    if (sections != null)
                        callback.getSections(sections);
                    else callback.getSections(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelSection> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
    public void getAllEpicLinks(EpicLinksCallback callback)  {
        Retrofit retrofit = networkService.getRetrofit();
        EpicLinksApi service = retrofit.create(EpicLinksApi.class);
        Call<ModelEpicLinks> call = service.getAllEpicLinks(token);
        Log.d(TAG, "getAllSections: " + call.request().url());
        call.enqueue(new Callback<ModelEpicLinks>() {
            @Override
            public void onResponse(retrofit2.Call<ModelEpicLinks> call, Response<ModelEpicLinks> response) {
                if (response.body() != null) {
                    ModelEpicLinks model = response.body();
                    List<PayloadEpicLinks> epicLinks = model.getPayload();
                    if (epicLinks != null)
                        callback.onEpicLinksLoaded(epicLinks);
                    else callback.onEpicLinksLoaded(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelEpicLinks> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

}
