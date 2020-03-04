package com.emika.app.data;

import android.app.Application;

import androidx.room.Room;

import com.emika.app.data.db.AppDatabase;

public class EmikaApplication extends Application {
    public static EmikaApplication instance;

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "emika_db")
                .build();
    }

    public static EmikaApplication getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
