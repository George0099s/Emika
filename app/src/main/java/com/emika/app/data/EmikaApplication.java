package com.emika.app.data;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.migration.Migration;
import com.emika.app.presentation.utils.Constants;

public class EmikaApplication extends Application {
    public static EmikaApplication instance;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private SharedPreferences sharedPreferences;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
        database = Room.databaseBuilder(this, AppDatabase.class, "emika_db")
                .addMigrations(Migration.MIGRATION_3_4)
                .build();
    }

    public static EmikaApplication getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
