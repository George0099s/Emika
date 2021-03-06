package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.TokenEntity;
import com.emika.app.data.db.entity.UserEntity;

@Dao
public interface TokenDao {
    @Query("SELECT token FROM TokenEntity")
    TokenEntity getToken();


    @Insert
    void insert(TokenEntity token);

    @Update
    void update(TokenEntity token);

    @Delete
    void delete(TokenEntity token);

    @Query("DELETE FROM TokenEntity")
    void deleteAll();
}
