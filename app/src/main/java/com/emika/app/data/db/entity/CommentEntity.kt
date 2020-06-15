package com.emika.app.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Comments")
class CommentEntity(
        @NonNull
        @PrimaryKey
        val id: String,
        val companyId: String,
        val createdAt: String,
        val createdBy: String,
        val taskId: String,
        val text: String,
        val updatedAt: String
)