package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "enrollments",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["student_id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class EnrollmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "enroll_id")
    val enrollId: Long = 0L,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "enrollment_date")
    val enrollmentDate: Date,
    val status: String,
)
