package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.ActualDurationDbCallback;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.entity.ActualDurationEntity;
import com.emika.app.data.db.entity.SectionEntity;
import com.emika.app.data.network.pojo.durationActualLog.PayloadDurationActual;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActualDurationDbManager {
    private AppDatabase db;
    private EmikaApplication app = EmikaApplication.getInstance();
    private static final String TAG = "ActualDurationDb";

    public ActualDurationDbManager() {
        db = app.getDatabase();
    }



    @SuppressLint("CheckResult")
    public void getAllDurations(ActualDurationDbCallback callback) {
        db.actualDurationDao().getAllDuration()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<ActualDurationEntity>>) callback::onActualDurationLoaded);
    }

    @SuppressLint("CheckResult")
    public void getDurationsByAssignee(ActualDurationDbCallback callback, String assigneeId, String date) {
        db.actualDurationDao().getAllDurationByAssigneeDate(assigneeId, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<ActualDurationEntity>>) callback::onActualDurationLoaded);
    }


    public void addAllDurations(List<ActualDurationEntity> actualDurationEntities, ActualDurationDbCallback callback) {
        Completable.fromAction(() -> db.actualDurationDao().insert(actualDurationEntities)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
//                callback.onActualDurationLoaded(null);
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }
    public void addDuration(ActualDurationEntity actualDurationEntity) {
        Completable.fromAction(() -> db.actualDurationDao().insert(actualDurationEntity)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
//                callback.onActualDurationLoaded(null);
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void insertDurations(ActualDurationEntity actualDurationEntity, ActualDurationDbCallback callback) {
        Completable.fromAction(() -> db.actualDurationDao().insert(actualDurationEntity)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
//                callback.onActualDurationLoaded(null);
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
//                callback.onMembersLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void deleteAllDurations() {
        Completable.fromAction(() -> db.actualDurationDao().deleteAll()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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

    public void updateDbDuration(ActualDurationEntity actualDurationEntity) {
        Completable.fromAction(() -> db.actualDurationDao().update(actualDurationEntity)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
//                callback.onActualDurationLoaded(null);
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
//                callback.onMembersLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void deleteDuration(ActualDurationEntity durationActual) {
        Completable.fromAction(() -> db.actualDurationDao().delete(durationActual)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
