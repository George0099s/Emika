package com.emika.app.data.db.dbmanager;

import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.dao.TaskDao;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TaskDbManager {
    private static final String TAG = "TaskDbManager";
    private AppDatabase db;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    private TaskDao taskDao;
    private List<TaskEntity> payloadTaskList = new ArrayList<>();
    private Converter converter;



    public TaskDbManager() {
        db = emikaApplication.getDatabase();
        taskDao = db.taskDao();
        converter = new Converter();
    }

    public void addDbTask(TaskDbCallback taskCallbackCallback){
//        Observable.fromCallable((new CallableAddTask(taskCallbackCallback)))
//                .subscribeOn(Schedulers.io())
//                .subscribe();
    }

    private Boolean addTask(String name, String description, String planDate, String deadlineDate, String priority, String epicLinks, String estimatedTime){
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(name);
        taskEntity.setPlanDate(planDate);
        taskEntity.setDeadlineDate(deadlineDate);
        taskEntity.setPriority(priority);
//        taskEntity.setEpicLinksEmika(epicLinks);
        taskEntity.setDuration(Integer.parseInt(estimatedTime)*60);
        taskEntity.setDescription(description);
//        taskDao.insert();
        return true;
    }

    public void getAllTask(TaskDbCallback taskCallbackCallback) {
        Observable.fromCallable((new CallableGetAllTask(taskCallbackCallback)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Boolean getDBTask(TaskDbCallback taskCallbackCallback) {
        payloadTaskList = taskDao.getAllTask();
        if (payloadTaskList != null && !payloadTaskList.isEmpty()) {
            taskCallbackCallback.setDbTask(payloadTaskList);
        }
        return true;
    }


    private Boolean deleteDbAllTask() {
        taskDao.deleteAll();
        return true;
    }

    public void deleteAll() {
        Observable.fromCallable((new CallableDeleteAllTask()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
    }

    public void updateTask(TaskEntity task) {
        Observable.fromCallable((new CallableUpdateTask(task)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private Boolean updateDBTask(TaskEntity task) {
        taskDao.update(task);
        return true;
    }

    public void insertDbAllTask(List<TaskEntity> payloadTaskList) {
        Observable.fromCallable((new CallableInsertAllTask(payloadTaskList)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private Boolean insertDBAllTask(List<TaskEntity> payloadTaskList) {
        if (payloadTaskList != null)
            taskDao.deleteAll();
            taskDao.insert(payloadTaskList);
        return true;
    }

    private class CallableUpdateTask implements Callable<Boolean> {
        private TaskEntity taskEntity;

        public CallableUpdateTask(TaskEntity taskEntity) {
            this.taskEntity = taskEntity;
        }

        @Override
        public Boolean call() throws Exception {
            return updateDBTask(taskEntity);
        }
    }

    private class CallableGetAllTask implements Callable<Boolean> {
        private TaskDbCallback taskCallback;

        public CallableGetAllTask(TaskDbCallback taskCallback) {
            this.taskCallback = taskCallback;
        }

        @Override
        public Boolean call() throws Exception {
            return getDBTask(taskCallback);
        }
    }

    private class CallableDeleteAllTask implements Callable<Boolean> {
        public CallableDeleteAllTask() {

        }

        @Override
        public Boolean call() throws Exception {
            return deleteDbAllTask();
        }
    }

    private class CallableInsertAllTask implements Callable<Boolean> {
        private List<TaskEntity> payloadTaskList;

        public CallableInsertAllTask(List<TaskEntity> payloadTaskList) {
            this.payloadTaskList = payloadTaskList;
        }

        @Override
        public Boolean call() throws Exception {
            return insertDBAllTask(payloadTaskList);
        }

    }
}
