package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.MemberDbCallback;
import com.emika.app.data.db.callback.calendar.ProjectDbCallback;
import com.emika.app.data.db.dao.MemberDao;
import com.emika.app.data.db.dao.ProjectDao;
import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.model.Member;
import com.emika.app.di.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class MemberDbManager {
    private AppDatabase db;
    private EmikaApplication app = EmikaApplication.getInstance();
    private static final String TAG = "MemberDbManager";
    private MemberDao memberDao;
    public MemberDbManager() {
        db = app.getDatabase();
        memberDao = db.memberDao();
    }



    @SuppressLint("CheckResult")
    public void getAllMembers(MemberDbCallback callback) {
        db.memberDao().getAllMembers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<MemberEntity>>) callback::onMembersLoaded);
    }

    public void addAllMembers(List<MemberEntity> memberEntityList, MemberDbCallback callback) {
        Completable.fromAction(() -> db.memberDao().insert(memberEntityList)).observeOn(AndroidSchedulers.mainThread())
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
//                callback.onMembersLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void deleteAllMembers() {
        Completable.fromAction(() -> db.memberDao().deleteAll()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
