package com.emika.app.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.db.entity.MemberEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface EpicLinksDao {
    @Query("SELECT * FROM `Epic links` WHERE status !='deleted'")
    Maybe<List<EpicLinksEntity>> getAllEpicLinks();

    @Query("SELECT * FROM `Epic links` WHERE projectId = :projectId AND status !='deleted'")
    LiveData<List<EpicLinksEntity>> getLiveDataEpicLinksByProjectId(String projectId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<EpicLinksEntity> epicLinksEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EpicLinksEntity epicLinksEntity);

    @Update
    void update(EpicLinksEntity epicLinksEntity);

    @Delete
    void delete(EpicLinksEntity epicLinksEntity);

    @Query("DELETE FROM `Epic links`")
    void deleteAll();
}
