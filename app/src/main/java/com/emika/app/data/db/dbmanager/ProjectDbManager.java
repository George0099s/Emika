package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.dao.ProjectDao;
import com.emika.app.data.db.entity.ProjectEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProjectDbManager {
    private AppDatabase db;
    private EmikaApplication app = EmikaApplication.instance;
    private static final String TAG = "ProjectDbManager";
    private ProjectDao projectDao;
    public ProjectDbManager() {
        db = app.getDatabase();
        projectDao = db.projectDao();
    }

    @SuppressLint("CheckResult")
    public void getAllDbProjects(ProjectDbCallback callback) {
        db.projectDao().getAllProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<ProjectEntity>>) callback::onProjectLoaded);
    }

    public void addAllProjects(List<ProjectEntity> projects, ProjectDbCallback callback) {
        Completable.fromAction(() -> db.projectDao().insert(projects)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                callback.onProjectLoaded(new ArrayList<>());

                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
//                callback.onProjectLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }
    public void deleteAllProjects() {
        Completable.fromAction(() -> db.projectDao().deleteAll()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: deleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.toString());
            }
        });
    }
}
