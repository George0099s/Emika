package com.emika.app.data.db.dbmanager;

import android.media.session.MediaSession;
import android.util.Log;

import com.emika.app.data.EmikaApplication;
import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.dao.TokenDao;
import com.emika.app.data.db.entity.TokenEntity;
import com.emika.app.data.network.callback.TokenCallback;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TokenDbManager {
    private static final String TAG = "TokenDbManager";
    private AppDatabase db;
    private String token;
    private EmikaApplication emikaApplication = EmikaApplication.getInstance();
    private TokenDao tokenDao;
    private TokenCallback tokenCallback;
    public TokenDbManager(){

    }

    public void getToken(TokenCallback tokenCallback){
        Observable.fromCallable((new CallableGetToken(tokenCallback)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
    private String getDBToken(TokenCallback callback){
        db = emikaApplication.getDatabase();
        TokenDao tokenDao;
        tokenDao = db.tokenDao();
        this.token = tokenDao.getToken().getToken();
        if (token != null && !token.isEmpty())
            Log.d(TAG, "getDBToken: " + token);
        callback.getToken(token);
        return token;
    }

    private Boolean deleteDbToken(){
        db = emikaApplication.getDatabase();
        TokenDao tokenDao;
        tokenDao = db.tokenDao();
        tokenDao.deleteAll();
        Log.d(TAG, "deleteDbToken: " + tokenDao.getToken().getToken());
        return true;
    }
    public void deleteAll() {
        Log.d(TAG, "deleteAll: ");
        Observable.fromCallable((new CallableDeleteToken()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private class CallableGetToken implements Callable<String> {
    private TokenCallback tokenCallback;
        public CallableGetToken(TokenCallback tokenCallback) {
            this.tokenCallback = tokenCallback;
        }
        @Override
        public String call() throws Exception {
            return getDBToken(tokenCallback);
        }

    }
    private class CallableDeleteToken implements Callable<Boolean> {
        public CallableDeleteToken() {

        }
        @Override
        public Boolean call() throws Exception {
            return deleteDbToken();
        }
    }
    public void insertToken(String token){
        Observable.fromCallable((new CallableInsertToken(token)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
    private String insertDBToken(String token){
        db = emikaApplication.getDatabase();
        TokenDao tokenDao;
        tokenDao = db.tokenDao();
        tokenDao.deleteAll();
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenDao.insert(tokenEntity);
        return token;
    }
    private class CallableInsertToken implements Callable<String> {
        private String token;
        public CallableInsertToken(String token) {
            this.token = token;
        }
        @Override
        public String call() throws Exception {
            return insertDBToken(token);
        }

    }
}
