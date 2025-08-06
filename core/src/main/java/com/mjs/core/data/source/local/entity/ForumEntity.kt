package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "forums",
    foreignKeys = [
        ForeignKey(
            entity = KelasEntity::class,
            parentColumns = ["kelas_id"],
            childColumns = ["kelas_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ForumEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "forum_id")
    val forumId: Int = 0,
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int,
    @ColumnInfo(name = "judul_forum")
    val judulForum: String,
    val deskripsi: String,
)
