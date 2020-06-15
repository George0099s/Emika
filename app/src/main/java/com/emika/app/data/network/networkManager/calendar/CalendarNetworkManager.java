package com.emika.app.data.network.networkManager.calendar;

import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.callback.calendar.SubTaskCallback;
import com.emika.app.data.network.NetworkService;
import com.emika.app.data.network.api.CalendarApi;
import com.emika.app.data.network.api.CompanyApi;
import com.emika.app.data.network.api.EpicLinksApi;
import com.emika.app.data.network.api.MemberApi;
import com.emika.app.data.network.api.ProjectApi;
import com.emika.app.data.network.callback.CompanyInfoCallback;
import com.emika.app.data.network.callback.calendar.DurationActualCallback;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.callback.calendar.ProjectsCallback;
import com.emika.app.data.network.callback.calendar.ShortMemberCallback;
import com.emika.app.data.network.callback.calendar.TaskCallback;
import com.emika.app.data.network.callback.calendar.TaskListCallback;
import com.emika.app.data.network.pojo.companyInfo.ModelCompanyInfo;
import com.emika.app.data.network.pojo.companyInfo.PayloadCompanyInfo;
import com.emika.app.data.network.pojo.durationActualLog.ModelDurationActual;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;
import com.emika.app.data.network.pojo.epiclinks.ModelEpicLinks;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.data.network.pojo.member.ModelShortMember;
import com.emika.app.data.network.pojo.member.PayloadShortMember;
import com.emika.app.data.network.pojo.project.ModelProject;
import com.emika.app.data.network.pojo.project.ModelSection;
import com.emika.app.data.network.pojo.project.PayloadProject;
import com.emika.app.data.network.pojo.project.PayloadSection;
import com.emika.app.data.network.pojo.subTask.ModelSubTask;
import com.emika.app.data.network.pojo.subTask.PayloadSubTask;
import com.emika.app.data.network.pojo.subTask.SubTask;
import com.emika.app.data.network.pojo.task.Model;
import com.emika.app.data.network.pojo.task.ModelTask;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.presentation.utils.Constants;
import com.emika.app.presentation.utils.Converter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
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
    private NetworkService networkService = NetworkService.getInstance();
    private Socket socket;
    private Converter converter;
    private JSONObject tokenJson;
    private Emitter.Listener onConnectSuccess = args -> Log.d(TAG, "call: succes" + args.length);
    private Emitter.Listener onConnectfailed = args -> Log.d(TAG, "call: faild" + args.length);

    public CalendarNetworkManager(String token) {
        this.token = token;
        tokenJson = new JSONObject();
        socket = EmikaApplication.instance.getSocket();
        socket.on("create_connection_successful", onConnectSuccess);
        socket.on("create_connection_failed", onConnectfailed);
        converter = new Converter();
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
                    else
                        callback.setTaskList(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void addTask(TaskCallback callback, PayloadTask task, JSONArray epicLinks, JSONArray subTasks) {
        Retrofit retrofit = networkService.getRetrofit();

        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelTask> call = service.addTask(token, task.getName(), task.getProjectId(), task.getPlanDate(), task.getDeadlineDate(), task.getAssignee(), String.valueOf(task.getDuration()),
                task.getDescription(), task.getPriority(), task.getSectionId(), epicLinks, subTasks);
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
                Log.d(TAG, t.getMessage());
            }
        });
    }

    /**
     * post request method, changed to WS 27.03.2020
     **/
//    public void updateTask(PayloadTask task)  {
//        Retrofit retrofit = networkService.getRetrofit();
//        CalendarApi service = retrofit.create(CalendarApi.class);
//        Call<ModelTask> call = service.updateTask(task.getId(), token, task.getPlanDate(), task.getName(),  task.getProjectId(), task.getDeadlineDate(), task.getDuration(),
//                task.getDurationActual(), task.getStatus(), task.getDescription(), task.getPriority());
//        call.enqueue(new Callback<ModelTask>() {
//            @Override
//            public void onResponse(retrofit2.Call<ModelTask> call, Response<ModelTask> response) {
//                if (response.body() != null) {
//
//                    ModelTask model = response.body();
//                    PayloadTask task1 = model.getPayloadTask();
//                    Log.d(TAG, "onResponse: " + task1.getDurationActual() /60+ " " + task1.getDuration() / 60);
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ModelTask> call, Throwable t) {
//                Log.d(TAG, t.getMessage().toString());
//            }
//        });
//    }
    public void updateTask(PayloadTask task) {
        JSONObject taskJSON = new JSONObject();
        JSONArray subTask = new JSONArray();
        if (task.getSubTaskList() != null)
            for (int i = 0; i < task.getSubTaskList().size(); i++) {
                subTask.put(task.getSubTaskList().get(i));
            }
        try {

            if (task.getParentTaskId() != null)
                taskJSON.put("parent_id", task.getParentTaskId());

            taskJSON.put("token", token);
            taskJSON.put("_id", task.getId());
            taskJSON.put("name", task.getName());
            taskJSON.put("duration", task.getDuration());
            taskJSON.put("duration_actual", task.getDurationActual());
            taskJSON.put("sub_tasks", subTask);
            if (task.getPlanDate() == null)
                taskJSON.put("plan_date", JSONObject.NULL);
            else
                taskJSON.put("plan_date", task.getPlanDate());
            if (task.getDeadlineDate() == null || task.getDeadlineDate().equals("null"))
                taskJSON.put("deadline_date", JSONObject.NULL);
            else
                taskJSON.put("deadline_date", task.getDeadlineDate());
            if (task.getPlanOrder() != null)
                taskJSON.put("plan_order", Integer.parseInt(task.getPlanOrder()));

            taskJSON.put("description", task.getDescription());
            taskJSON.put("status", task.getStatus());
            taskJSON.put("project_id", task.getProjectId());
            taskJSON.put("section_id", task.getSectionId());
            taskJSON.put("epic_links", converter.fromListToJSONArray(task.getEpicLinks()));
            taskJSON.put("priority", task.getPriority());
            taskJSON.put("assignee", task.getAssignee());
            socket.emit("server_update_task", taskJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAllShortMembers(ShortMemberCallback callback) {
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
                    else
                        callback.allMembers(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelShortMember> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void getAllProjects(ProjectsCallback callback) {
        Retrofit retrofit = networkService.getRetrofit();
        ProjectApi service = retrofit.create(ProjectApi.class);
        Call<ModelProject> call = service.getAllProjects(token);
        Log.d(TAG, "getAllProjects: " + call.request().url());
        call.enqueue(new Callback<ModelProject>() {
            @Override
            public void onResponse(retrofit2.Call<ModelProject> call, Response<ModelProject> response) {
                if (response.body() != null) {
                    ModelProject model = response.body();
                    List<PayloadProject> projects = model.getPayload();
//                    Log.d(TAG, "onResponse:123 " + projects.size());
                    if (projects != null)
                        callback.getProjects(projects);
                    else
                        callback.getProjects(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelProject> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void getAllSections(ProjectsCallback callback) {
        Retrofit retrofit = networkService.getRetrofit();
        ProjectApi service = retrofit.create(ProjectApi.class);
        Call<ModelSection> call = service.getAllSections(token);
        call.enqueue(new Callback<ModelSection>() {
            @Override
            public void onResponse(retrofit2.Call<ModelSection> call, Response<ModelSection> response) {
                if (response.body() != null) {
                    ModelSection model = response.body();
                    List<PayloadSection> sections = model.getPayload();
                    if (sections != null)
                        callback.getSections(sections);
                    else
                        callback.getSections(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelSection> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void getAllEpicLinks(EpicLinksCallback callback) {
        Retrofit retrofit = networkService.getRetrofit();
        EpicLinksApi service = retrofit.create(EpicLinksApi.class);
        Call<ModelEpicLinks> call = service.getAllEpicLinks(token);
        call.enqueue(new Callback<ModelEpicLinks>() {
            @Override
            public void onResponse(retrofit2.Call<ModelEpicLinks> call, Response<ModelEpicLinks> response) {
                if (response.body() != null) {
                    ModelEpicLinks model = response.body();
                    List<PayloadEpicLinks> epicLinks = model.getPayload();
                    if (epicLinks != null)
                        callback.onEpicLinksDownloaded(epicLinks);
                    else
                        callback.onEpicLinksDownloaded(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelEpicLinks> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void downLoadDurationLog(DurationActualCallback callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelDurationActual> call = service.fetchDurationLogs(token);
        call.enqueue(new Callback<ModelDurationActual>() {
            @Override
            public void onResponse(retrofit2.Call<ModelDurationActual> call, Response<ModelDurationActual> response) {
                if (response.body() != null) {
                    ModelDurationActual model = response.body();

                    List<PayloadDurationActual> durationActualList = model.getPayload();
                    if (durationActualList != null)
                        callback.onDurationLogDownloaded(durationActualList);
                    else
                        callback.onDurationLogDownloaded(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelDurationActual> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void downLoadCompanyInfo(CompanyInfoCallback callback) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();

        CompanyApi service = retrofit.create(CompanyApi.class);
        Call<ModelCompanyInfo> call = service.getCompanyInfo(token);
        Log.d(TAG, "downLoadCompanyInfo: " + call.request().url());
        call.enqueue(new Callback<ModelCompanyInfo>() {
            @Override
            public void onResponse(retrofit2.Call<ModelCompanyInfo> call, Response<ModelCompanyInfo> response) {
                if (response.body() != null) {
                    ModelCompanyInfo model = response.body();
                    PayloadCompanyInfo company = model.getPayload();
                    if (company != null)
                        callback.onCompanyInfoDownloaded(company);
                    else callback.onCompanyInfoDownloaded(new PayloadCompanyInfo());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelCompanyInfo> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void sendRegistrationKey(String key) {
        Retrofit retrofit = NetworkService.getInstance().getRetrofit();
        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelDurationActual> call = service.sendRegistrationKey(token, key);
        call.enqueue(new Callback<ModelDurationActual>() {
            @Override
            public void onResponse(retrofit2.Call<ModelDurationActual> call, Response<ModelDurationActual> response) {
                if (response.body() != null) {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelDurationActual> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void getSubTaskList(String taskId, SubTaskCallback callback) {
        Retrofit retrofit = networkService.getRetrofit();
        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelSubTask> call = service.getSubTask(taskId, token);
        call.enqueue(new Callback<ModelSubTask>() {
            @Override
            public void onResponse(retrofit2.Call<ModelSubTask> call, Response<ModelSubTask> response) {
                if (response.body() != null) {
                    ModelSubTask model = response.body();
                    PayloadSubTask payloadSubTask = model.getPayload();
                    List<SubTask> subTasks = payloadSubTask.getSubTasks();
                    if (subTasks != null)
                        callback.onSubTaskListLoaded(subTasks);
                    else
                        callback.onSubTaskListLoaded(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelSubTask> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }

    public void updateSubTask(SubTask task) {
        JSONObject taskJSON = new JSONObject();
        try {
            taskJSON.put("token", token);
            taskJSON.put("_id", task.getId());
            taskJSON.put("name", task.getName());
            taskJSON.put("parent_task_id", task.getParentTaskId());
            Log.d(TAG, "updateSubTask: " + task.getParentTaskId());
            taskJSON.put("status", task.getStatus());
            taskJSON.put("project_id", task.getProjectId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("server_update_task", taskJSON);
    }

    public void addSubTask(SubTask task, SubTaskCallback callback) {
        Retrofit retrofit = networkService.getRetrofit();
        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<ModelTask> call = service.addSubTask(token, task.getName(), task.getProjectId(), task.getParentTaskId());
        call.enqueue(new Callback<ModelTask>() {
            @Override
            public void onResponse(retrofit2.Call<ModelTask> call, Response<ModelTask> response) {
                if (response.body() != null) {
                    ModelTask model = response.body();
                    Log.d(TAG, "onResponse: " +  model.getError());

                    PayloadTask task = model.getPayloadTask();
                    if (task != null)
                        callback.onSubTaskLoaded(task.getId());
                    else
                        callback.onSubTaskLoaded(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ModelTask> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void downloadTaskByAssignee(TaskListCallback callback, String assignee) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASIC_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create()) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();

        CalendarApi service = retrofit.create(CalendarApi.class);
        Call<Model> call = service.fetchAllTasksByAssignee(token, assignee);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(retrofit2.Call<Model> call, Response<Model> response) {
                if (response.body() != null) {
                    Model model = response.body();

                    List<PayloadTask> taskList = model.getPayloadTask();

                    if (taskList != null)
                        callback.setTaskList(taskList);
                    else callback.setTaskList(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Model> call, Throwable t) {
                Log.d(TAG, t.getMessage().toString());
            }
        });
    }
}
