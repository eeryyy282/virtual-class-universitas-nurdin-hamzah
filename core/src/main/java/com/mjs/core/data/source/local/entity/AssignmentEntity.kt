package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "assignments",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long = 0L,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "assignment_title")
    val assignmentTitle: String,
    val description: String,
    val startDate: Date,
    val deadlineDate: Date,
    @ColumnInfo(name = "attachment")
    val attachment: String? = null,
)
