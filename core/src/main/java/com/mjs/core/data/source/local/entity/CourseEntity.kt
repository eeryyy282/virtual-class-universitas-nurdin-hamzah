package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = LecturerEntity::class,
            parentColumns = ["lecturer_id"],
            childColumns = ["lecturer_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class CourseEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "course_name")
    val courseName: String,
    val description: String,
    @ColumnInfo(name = "lecturer_id")
    val lecturerId: Long,
    val schedule: Date,
    val semester: String,
    val credit: Int,
    val department: String,
    @ColumnInfo(name = "course_image")
    val courseImage: String? = null,
    val room: String,
)
