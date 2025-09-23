package com.mjs.core.data.source.local.entity.attendance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import java.util.Date

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["student_id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "attendance_id")
    val attendanceId: Long = 0L,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "attendance_date")
    val attendanceDate: Date,
    val status: String,
    val description: String? = null,
)
