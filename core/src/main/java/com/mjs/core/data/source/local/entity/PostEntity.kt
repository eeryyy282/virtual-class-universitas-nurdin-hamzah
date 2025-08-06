package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val postId: Int = 0,
    @ColumnInfo(name = "forum_id")
    val forumId: Int,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "user_role")
    val userRole: String,
    @ColumnInfo(name = "isi_post")
    val isiPost: String,
    @ColumnInfo(name = "tanggal_post")
    val tanggalPost: String,
)
