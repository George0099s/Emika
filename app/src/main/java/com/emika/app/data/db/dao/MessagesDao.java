package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.MessageEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface MessagesDao {
    @Query("SELECT * FROM Messages")
    Maybe<List<MessageEntity>> getAllMessages();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MessageEntity> messageEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity messageEntity);

    @Update
    void update(MessageEntity messageEntity);

    @Delete
    void delete(MessageEntity messageEntity);

    @Query("DELETE FROM Messages")
    void deleteAll();
}

