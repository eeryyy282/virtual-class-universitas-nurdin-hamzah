package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "forums",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ForumEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "forum_id")
    val forumId: Long = 0L,
    @ColumnInfo(name = "course_id", index = true)
    val courseId: String,
    @ColumnInfo(name = "forum_title")
    val forumTitle: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "created_date")
    val createdDate: Date,
)
