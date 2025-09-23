package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity): Long

    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId")
    fun getSubmissionsByAssignment(assignmentId: Long): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE submission_id = :submissionId")
    fun getSubmissionById(submissionId: Long): Flow<SubmissionEntity?>

    @Query("UPDATE submissions SET grade = :grade, note = :note WHERE submission_id = :submissionId")
    suspend fun updateSubmissionGradeAndNote(
        submissionId: Long,
        grade: Int?,
        note: String?,
    )
}
