package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecturer")
data class LecturerEntity(
    @PrimaryKey
    @ColumnInfo(name = "lecturer_id")
    val lecturerId: Long,
    val name: String,
    val email: String,
    val password: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String? = null,
)
