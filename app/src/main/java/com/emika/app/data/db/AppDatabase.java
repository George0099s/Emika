package com.emika.app.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.emika.app.data.db.dao.TokenDao;
import com.emika.app.data.db.dao.UserDao;
import com.emika.app.data.db.entity.TokenEntity;
import com.emika.app.data.db.entity.UserEntity;

@Database(entities = {UserEntity.class, TokenEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract TokenDao tokenDao();
}