package com.emika.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.emika.app.data.db.entity.MemberEntity;
import com.emika.app.data.db.entity.TaskEntity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface MemberDao {
    @Query("SELECT * FROM Member")
    Maybe<List<MemberEntity>> getAllMembers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MemberEntity> memberEntityList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MemberEntity memberEntity);

    @Update
    void update(MemberEntity memberEntity);

    @Delete
    void delete(MemberEntity memberEntity);

    @Query("DELETE FROM Member")
    void deleteAll();
}
