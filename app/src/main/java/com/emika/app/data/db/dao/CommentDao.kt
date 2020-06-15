package com.emika.app.data.db.dao

import androidx.room.*
import com.emika.app.data.db.entity.CommentEntity
import com.emika.app.data.db.entity.EpicLinksEntity
import com.emika.app.data.network.pojo.subTask.Comment
import io.reactivex.Maybe

@Dao
interface CommentDao {
    @Query("SELECT * FROM `Comments`")
    fun getAllEpicLinks(): Maybe<List<CommentEntity?>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comments: List<CommentEntity?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comments: CommentEntity?)

    @Update
    fun update(comment: CommentEntity?)

    @Delete
    fun delete(comments: CommentEntity?)

    @Query("DELETE FROM `Comments`")
    fun deleteAll()
}