package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.dao.TaskDao;
import com.emika.app.data.db.dao.TaskTransactionDao;
import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.callback.calendar.TaskDbCallback;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.emika.app.presentation.utils.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TaskDbManager {
    private static final String TAG = "TaskDbManager";
    private AppDatabase db;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    private TaskDao taskDao;
    private TaskTransactionDao dao;
    private List<TaskEntity> payloadTaskList = new ArrayList<>();
    private Converter converter;



    public TaskDbManager() {
        db = emikaApplication.getDatabase();
        taskDao = db.taskDao();
        dao = db.taskTransactionDao();
        converter = new Converter();
    }

    public void firsInsert(List<TaskEntity> taskEntityList){
        db.taskTransactionDao().insertAndDeleteInTransaction(taskEntityList);
    }

    @SuppressLint("CheckResult")
    public void getAllDbTaskById(TaskDbCallback callback, String assignee) {
        db.taskDao().getAllTaskByAssignee(assignee)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<TaskEntity>>) callback::onTasksLoaded);
    }

    @SuppressLint("CheckResult")
    public void getTaskById(TaskDbCallback callback, String id) {
        db.taskDao().getTaskById(id)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super TaskEntity>) callback::onOneTaskLoaded);
    }
    @SuppressLint("CheckResult")
    public void getAllDbTaskByDateId(TaskDbCallback callback, String assignee, String planDate) {
        db.taskDao().getAllTaskByDateAssignee(assignee, planDate)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<TaskEntity>>) callback::onFilteredTasksLoaded);
    }

    @SuppressLint("CheckResult")
    public void getAllDbTask(TaskDbCallback callback) {
        db.taskDao().getAllTask()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<TaskEntity>>) callback::onTasksLoaded);
    }

    @SuppressLint("CheckResult")
    public void addTask(TaskEntity task) {
        Completable.fromAction(() -> db.taskDao().insert(task)).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
//                callback.onTasksLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void addAllProjects(List<TaskEntity> taskEntityList) {
        Completable.fromAction(() -> db.taskDao().insert(taskEntityList)).observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: " + taskEntityList.size());
            }

            @Override
            public void onError(Throwable e) {
//                callback.onTasksLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
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

    private class CallableDeleteAllTask implements Callable<Boolean> {
        public CallableDeleteAllTask() {

        }

        @Override
        public Boolean call() throws Exception {
            return deleteDbAllTask();
        }
    }

}
