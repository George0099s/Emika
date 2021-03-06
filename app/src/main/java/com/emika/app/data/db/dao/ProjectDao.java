package com.emika.app.data.db.dao;

import android.net.sip.SipSession;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.di.Project;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public  interface ProjectDao {
    @Query("SELECT * FROM Project")
    Maybe<List<ProjectEntity>> getAllProjects();

    @Insert
    void insert(List<ProjectEntity> projectEntities);

  @Insert
    void insert(ProjectEntity projectEntity);


    @Update
    void update(ProjectEntity projectEntity);

    @Delete
    void delete(ProjectEntity projectEntity);

    @Query("DELETE FROM Project")
    void deleteAll();

}
