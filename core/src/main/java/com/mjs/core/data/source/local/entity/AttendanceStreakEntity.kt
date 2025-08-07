package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_streak",
    foreignKeys = [
        ForeignKey(
            entity = MahasiswaEntity::class,
            parentColumns = ["nim"],
            childColumns = ["nim"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = KelasEntity::class,
            parentColumns = ["kelas_id"],
            childColumns = ["kelas_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AttendanceStreakEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "streak_id")
    val streakId: Int = 0,
    val nim: String,
    @ColumnInfo(name = "kelas_id")
    val kelasId: String,
    @ColumnInfo(name = "current_streak")
    val currentStreak: Int,
    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int,
    @ColumnInfo(name = "last_attended_date")
    val lastAttendedDate: String,
)
