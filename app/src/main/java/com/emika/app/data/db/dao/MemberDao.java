package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.TaskEntity;

import java.util.List;

@Dao
public interface MemberDao {
    @Query("SELECT * FROM Member")
    List<MemberEntity> getAllTask();

    @Insert
    void insert(List<MemberEntity> taskEntityList);

    @Insert
    void insert(MemberEntity taskEntity);

    @Update
    void update(MemberEntity task);

    @Delete
    void delete(MemberEntity task);

    @Query("DELETE FROM Member")
    void deleteAll();
}
