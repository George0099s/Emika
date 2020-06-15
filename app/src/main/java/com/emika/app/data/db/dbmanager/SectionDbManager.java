package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.SectionDbCallback;
import com.emika.app.data.db.dao.SectionDao;
import com.emika.app.data.db.entity.SectionEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SectionDbManager {
    private AppDatabase db;
    private EmikaApplication app = EmikaApplication.instance;
    private static final String TAG = "SectionDbManager";
    private SectionDao sectionDao;

    public SectionDbManager() {
        db = app.getDatabase();
        sectionDao = db.sectionDao();
    }



    @SuppressLint("CheckResult")
    public void getAllSections(SectionDbCallback callback) {
        db.sectionDao().getAllSection()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((io.reactivex.functions.Consumer<? super List<SectionEntity>>) callback::onSectionLoaded);
    }

    public void addAllSections(List<SectionEntity> sectionEntities, SectionDbCallback callback) {
        Completable.fromAction(() -> db.sectionDao().insert(sectionEntities)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

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

    public void deleteAllSections() {
        Completable.fromAction(() -> db.sectionDao().deleteAll()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
