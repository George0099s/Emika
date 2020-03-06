package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.UserEntity;

@Dao
public interface UserDao {
    @Query("SELECT * FROM UserEntity")
    UserEntity getUser();

    @Query("SELECT token, id FROM UserEntity")
    UserEntity getToken();

    @Insert
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("DELETE FROM UserEntity")
    void deleteAll();
}
