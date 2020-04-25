package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.ProjectEntity;
import com.emika.app.data.db.entity.SectionEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface SectionDao {
    @Query("SELECT * FROM Section")
    Maybe<List<SectionEntity>> getAllSection();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<SectionEntity> sectionEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SectionEntity sectionEntity);


    @Update
    void update(SectionEntity sectionEntity);

    @Delete
    void delete(SectionEntity sectionEntity);

    @Query("DELETE FROM Section")
    void deleteAll();
}
