package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.dao.EpicLinksDao;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.di.User;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EpicLinksDbManager {
    private static final String TAG = "EpicLinksDbManager";
    private AppDatabase db;
    private EpicLinksDao epicLinksDao;
    public EpicLinksDbManager() {
        db = EmikaApplication.getInstance().getDatabase();
        epicLinksDao = db.epicLinksDao();
    }

    @SuppressLint("CheckResult")
    public void getAllMembers(EpicLinksDbCallback callback) {
        db.epicLinksDao().getAllEpicLinks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((callback::onEpicLinksLoaded));
    }

    public void insertAllEpicLinks(List<EpicLinksEntity> epicLinksEntities, EpicLinksDbCallback callback) {
        Completable.fromAction(() -> db.epicLinksDao().insert(epicLinksEntities)).observeOn(AndroidSchedulers.mainThread())
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
//                callback.onEpicLinksLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void deleteAllEpicLinks() {
        Completable.fromAction(() -> db.epicLinksDao().deleteAll()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
