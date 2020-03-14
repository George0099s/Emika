package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.TokenEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.model.User;

@Dao
public interface UserDao {

//    @Query("SELECT id FROM User")
//    String getUserId();
//
//
//    @Insert
//    void insertUser(UserEntity user);
//
//    @Update
//    void update(UserEntity user);
//
//    @Delete
//    void delete(UserEntity user);
//
//    @Query("DELETE FROM User")
//    void deleteAll();
}
