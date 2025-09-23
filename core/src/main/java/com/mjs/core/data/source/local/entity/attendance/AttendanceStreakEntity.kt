package com.mjs.core.data.source.local.entity.attendance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import java.util.Date

@Entity(
    tableName = "attendance_streak",
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
data class AttendanceStreakEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "streak_id")
    val streakId: Long = 0L,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "course_id")
    val courseId: String,
    @ColumnInfo(name = "current_streak")
    val currentStreak: Int,
    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int,
    @ColumnInfo(name = "last_attended_date")
    val lastAttendedDate: Date,
)
