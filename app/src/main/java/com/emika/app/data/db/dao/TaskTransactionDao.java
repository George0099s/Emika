package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.emika.app.data.db.entity.TaskEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public abstract class TaskTransactionDao {

    @Query("SELECT * FROM Task")
   public abstract Maybe<List<TaskEntity>> getAllTask();

    @Query("SELECT * FROM Task WHERE assignee = :assignee")
    public abstract Maybe<List<TaskEntity>> getAllTaskByAssignee(String assignee);
    @Query("SELECT * FROM Task WHERE id = :id")
    public abstract Maybe<TaskEntity> getTaskById(String id);

    @Query("SELECT * FROM Task WHERE assignee = :assignee AND planDate = :planDate")
    public abstract Maybe<List<TaskEntity>> getAllTaskByDateAssignee(String assignee, String planDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<TaskEntity> taskEntityList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(TaskEntity taskEntity);



    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(TaskEntity task);

    @Delete
    public abstract void delete(TaskEntity task);

    @Query("DELETE FROM Task")
    public abstract void deleteAll();
        @Transaction
        public void insertAndDeleteInTransaction(List<TaskEntity> taskEntityList) {
            deleteAll();
            insert(taskEntityList);
        }
}
