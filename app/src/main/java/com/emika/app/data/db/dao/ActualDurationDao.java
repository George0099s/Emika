package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.ActualDurationEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface ActualDurationDao {
    @Query("SELECT * FROM `Actual duration`")
    Maybe<List<ActualDurationEntity>> getAllDuration();

    @Query("SELECT * FROM `Actual duration` WHERE createdBy = :assignee AND date = :date")
    Maybe<List<ActualDurationEntity>> getAllDurationByAssigneeDate(String assignee, String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ActualDurationEntity> actualDurationEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActualDurationEntity actualDurationEntity);

    @Update
    void update(ActualDurationEntity actualDurationEntity);

    @Delete
    void delete(ActualDurationEntity actualDurationEntity);

    @Query("DELETE FROM `Actual duration`")
    void deleteAll();
}
