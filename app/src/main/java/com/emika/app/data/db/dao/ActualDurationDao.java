package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.ActualDurationEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface ActualDurationDao {
    @Query("SELECT * FROM `Actual duration`")
    Maybe<List<ActualDurationEntity>> getAllDuration();

    @Insert
    void insert(List<ActualDurationEntity> actualDurationEntities);

    @Insert
    void insert(ActualDurationEntity actualDurationEntity);

    @Update
    void update(ActualDurationEntity actualDurationEntity);

    @Delete
    void delete(ActualDurationEntity actualDurationEntity);

    @Query("DELETE FROM `Actual duration`")
    void deleteAll();
}
