package com.emika.app.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.emika.app.data.db.entity.TaskEntity;
import com.emika.app.data.db.entity.UserEntity;
import com.emika.app.data.network.pojo.task.PayloadTask;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    Maybe<List<TaskEntity>> getAllTask();

    @Query("SELECT * FROM Task WHERE assignee = :assignee ")
    Maybe<List<TaskEntity>> getAllTaskByAssignee(String assignee);

    @Query("SELECT * FROM Task WHERE id = :id")
    Maybe<TaskEntity> getTaskById(String id);

    @Query("SELECT * FROM Task")
    LiveData<List<TaskEntity>> getAllTaskLiveData();

    @Query("SELECT * FROM Task WHERE assignee = :assignee")
    LiveData<List<TaskEntity>> getAllTaskLiveDataByAssignee(String assignee);


    @Query("SELECT * FROM Task WHERE assignee = :assignee AND planDate = :planDate")
    Maybe<List<TaskEntity>> getAllTaskByDateAssignee(String assignee, String planDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TaskEntity> taskEntityList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEntity taskEntity);



    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TaskEntity task);

    @Delete
    void delete(TaskEntity task);

    @Query("DELETE FROM Task")
    void deleteAll();
}
