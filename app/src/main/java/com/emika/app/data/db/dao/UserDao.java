package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.di.User;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface UserDao {


    @Query("SELECT * FROM User")
    Maybe<UserEntity> getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("DELETE FROM User")
    void deleteAll();
}
