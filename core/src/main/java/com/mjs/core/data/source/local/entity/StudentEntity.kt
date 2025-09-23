package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class StudentEntity(
    @PrimaryKey
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    val name: String,
    val email: String,
    val password: String,
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String? = null,
    val supervisor: String,
    val department: String,
)
