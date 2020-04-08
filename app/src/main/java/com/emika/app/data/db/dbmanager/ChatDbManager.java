package com.emika.app.data.db.dbmanager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.callback.chat.MessagesDbCallback;
import com.emika.app.data.db.dao.EpicLinksDao;
import com.emika.app.data.db.dao.MessagesDao;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MessageEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatDbManager {
    private static final String TAG = "ChatDbManager";
    private AppDatabase db;
    private MessagesDao messagesDao;

    public ChatDbManager() {
        db = EmikaApplication.getInstance().getDatabase();
        messagesDao = db.messagesDao();
    }

    @SuppressLint("CheckResult")
    public void getAllMessages(MessagesDbCallback callback) {
        db.messagesDao().getAllMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((callback::onMessagesLoaded));
    }

    public void insertMessages(List<MessageEntity> messageEntities, MessagesDbCallback callback) {
        Completable.fromAction(() -> db.messagesDao().insert(messageEntities)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
//                callback.onMessagesLoaded(new ArrayList<>());
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
//                callback.onEpicLinksLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }
    public void insertMessage(MessageEntity messageEntity, MessagesDbCallback callback) {
        Completable.fromAction(() -> db.messagesDao().insert(messageEntity)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
//                callback.onMessagesLoaded(new ArrayList<>());
                Log.d(TAG, "onComplete: ");
            }

            @Override
            public void onError(Throwable e) {
//                callback.onEpicLinksLoaded(null);
                Log.d(TAG, "onError: "+ e.toString());
            }
        });
    }

    public void deleteAllMessages() {
        Completable.fromAction(() -> db.messagesDao().deleteAll()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
