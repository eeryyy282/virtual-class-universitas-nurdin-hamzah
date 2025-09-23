package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND course_id = :courseId")
    fun getAttendanceHistory(
        studentId: Long,
        courseId: String,
    ): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceStreak(streak: AttendanceStreakEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAttendanceStreak(streak: AttendanceStreakEntity)

    @Query("SELECT * FROM attendance_streak WHERE student_id = :studentId AND course_id = :courseId")
    fun getAttendanceStreak(
        studentId: Long,
        courseId: String,
    ): Flow<AttendanceStreakEntity?>
}
