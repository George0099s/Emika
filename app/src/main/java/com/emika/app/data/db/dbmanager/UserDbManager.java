package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.UserDbCallback;
import com.emika.app.data.db.dao.UserDao;
import com.emika.app.data.db.entity.UserEntity;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

public class UserDbManager {
    private AppDatabase db;
    private EmikaApplication app = EmikaApplication.instance;
    private static final String TAG = "ProjectDbManager";
    private UserDao userDao;
    public UserDbManager() {
        db = app.getDatabase();
        userDao = db.userDao();
    }

    @SuppressLint("CheckResult")
    public void getUser(UserDbCallback callback) {
        db.userDao().getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super UserEntity>) callback::onUserLoaded);
    }

    public void addUser(UserEntity user) {
        Completable.fromAction(() -> db.userDao().insertUser(user)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }


    public void dropAllTable() {
        Observable.fromCallable((new ClearAllTables()))
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe();
    }

    public Boolean dropDB(){
        db.clearAllTables();
    return true;
    }

    private class ClearAllTables implements Callable<Boolean> {
        public ClearAllTables() {

        }

        @Override
        public Boolean call() throws Exception {
            return dropDB();
        }
    }}
