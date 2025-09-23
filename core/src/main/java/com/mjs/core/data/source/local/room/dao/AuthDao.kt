package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mjs.core.data.source.local.entity.LecturerEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerLecturer(lecturer: LecturerEntity)

    @Query("SELECT * FROM student WHERE student_id = :studentId")
    fun getStudentById(studentId: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM lecturer WHERE lecturer_id = :lecturerId")
    fun getLecturerById(lecturerId: Long): Flow<LecturerEntity?>

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Update
    suspend fun updateLecturer(lecturer: LecturerEntity)
}
