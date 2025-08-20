package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM assignments WHERE kelas_id = :kelasId")
    fun getAssignmentsByClass(kelasId: String): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateTask(assignment: AssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity)

    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId")
    fun getSubmissionsByAssignment(assignmentId: Int): Flow<List<SubmissionEntity>>

    @Query("SELECT assignment_id FROM assignments WHERE judul_tugas = :judul AND kelas_id = :kelasId ORDER BY assignment_id DESC LIMIT 1")
    suspend fun getAssignmentIdByTitleAndClassId(
        judul: String,
        kelasId: String,
    ): Int?

    @Query(
        """
        SELECT a.* FROM assignments a
        LEFT JOIN submissions s ON a.assignment_id = s.assignment_id AND s.nim = :nim
        WHERE a.kelas_id = :kelasId AND s.submission_id IS NULL AND a.tanggalSelesai >= :currentDate
    """,
    )
    fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query(
        """
        SELECT a.* FROM assignments a
        LEFT JOIN submissions s ON a.assignment_id = s.assignment_id AND s.nim = :nim
        WHERE a.kelas_id = :kelasId AND s.submission_id IS NULL AND a.tanggalSelesai < :currentDate
    """,
    )
    fun getLateTasks(
        nim: Int,
        kelasId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query(
        """
        SELECT * FROM assignments
        WHERE kelas_id IN (:kelasIds) AND tanggalSelesai >= :currentDate
    """,
    )
    fun getAssignmentsByKelasIdsAndFutureDeadline(
        kelasIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query(
        """
        SELECT * FROM assignments
        WHERE kelas_id IN (:kelasIds) AND tanggalSelesai < :currentDate
    """,
    )
    fun getAssignmentsByKelasIdsAndPastDeadline(
        kelasIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE assignment_id = :assignmentId")
    fun getAssignmentById(assignmentId: Int): Flow<AssignmentEntity?>
}
