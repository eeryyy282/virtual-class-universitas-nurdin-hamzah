package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = ForumEntity::class,
            parentColumns = ["forum_id"],
            childColumns = ["forum_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "post_id")
    val postId: Long = 0L,
    @ColumnInfo(name = "forum_id")
    val forumId: Long,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "user_role")
    val userRole: String,
    @ColumnInfo(name = "post_content")
    val postContent: String,
    @ColumnInfo(name = "post_date")
    val postDate: Date,
)
