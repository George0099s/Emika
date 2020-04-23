package com.emika.app.data;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.emika.app.data.db.AppDatabase;
import com.emika.app.data.db.migration.Migration;
import com.emika.app.di.DaggerUserComponent;
import com.emika.app.di.UserComponent;
import com.emika.app.di.UserModule;
import com.emika.app.presentation.utils.Constants;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URI;
import java.net.URISyntaxException;

public class EmikaApplication extends Application {
    public static EmikaApplication instance;
    private SharedPreferences sharedPreferences;
    private AppDatabase database;
    private UserComponent component;
    private Manager manager;

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;
    {
        try {
            manager = new Manager(new URI(Constants.BASIC_URL));
        } catch (
                URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static EmikaApplication getInstance() {
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
        database = Room.databaseBuilder(this, AppDatabase.class, "emika_db")

                .addMigrations(Migration.MIGRATION_1_2)
                .addMigrations(Migration.MIGRATION_2_3)
                .addMigrations(Migration.MIGRATION_3_4)
                .addMigrations(Migration.MIGRATION_4_5)
                .addMigrations(Migration.MIGRATION_5_6)
                .addMigrations(Migration.MIGRATION_6_7)
                .addMigrations(Migration.MIGRATION_7_8)
                .addMigrations(Migration.MIGRATION_8_9)
                .addMigrations(Migration.MIGRATION_9_10)
                .addMigrations(Migration.MIGRATION_10_11)
//                .fallbackToDestructiveMigration()
                .build();
        component = DaggerUserComponent
                .builder()
                .userModule(new UserModule())
                .build();
        socket = getManager().socket("/all");
        socket.connect();

    }

    public AppDatabase getDatabase() {
        return database;
    }

    public Manager getManager() {
        return manager;
    }

    public UserComponent getComponent() {
        return component;
    }
}
