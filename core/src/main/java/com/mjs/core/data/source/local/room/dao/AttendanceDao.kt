package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.AttendanceStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE nim = :nim AND kelas_id = :kelasId")
    fun getAttendanceHistory(
        nim: String,
        kelasId: Int,
    ): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAttendanceStreak(streak: AttendanceStreakEntity)

    @Query("SELECT * FROM attendance_streak WHERE nim = :nim AND kelas_id = :kelasId")
    fun getAttendanceStreak(
        nim: String,
        kelasId: Int,
    ): Flow<AttendanceStreakEntity?>
}
