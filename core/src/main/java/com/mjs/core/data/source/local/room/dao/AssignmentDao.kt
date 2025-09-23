package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mjs.core.data.source.local.entity.AssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments WHERE course_id = :courseId")
    fun getAssignmentsByCourse(courseId: String): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Query(
        "SELECT assignment_id FROM assignments WHERE assignment_title = :assignmentTitle AND " +
            "course_id = :courseId ORDER BY assignment_id DESC LIMIT 1",
    )
    suspend fun getAssignmentIdByTitleAndCourseId(
        assignmentTitle: String,
        courseId: String,
    ): Long?

    @Query(
        """
        SELECT a.* FROM assignments a
        LEFT JOIN submissions s ON a.assignment_id = s.assignment_id AND s.student_id = :studentId
        WHERE a.course_id = :courseId AND s.submission_id IS NULL AND a.deadlineDate >= :currentDate
    """,
    )
    fun getPendingAssignmentsForStudent(
        studentId: Long,
        courseId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query(
        """
        SELECT a.* FROM assignments a
        LEFT JOIN submissions s ON a.assignment_id = s.assignment_id AND s.student_id = :studentId
        WHERE a.course_id = :courseId AND s.submission_id IS NULL AND a.deadlineDate < :currentDate
    """,
    )
    fun getMissedAssignmentsForStudent(
        studentId: Long,
        courseId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query(
        """
        SELECT * FROM assignments
        WHERE course_id IN (:courseIds) AND deadlineDate >= :currentDate
    """,
    )
    fun getActiveAssignmentsByCourses(
        courseIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query(
        """
        SELECT * FROM assignments
        WHERE course_id IN (:courseIds) AND deadlineDate < :currentDate
    """,
    )
    fun getPastAssignmentsByCourses(
        courseIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE assignment_id = :assignmentId")
    fun getAssignmentById(assignmentId: Long): Flow<AssignmentEntity?>

    @Query("DELETE FROM assignments WHERE assignment_id = :assignmentId")
    suspend fun deleteAssignmentById(assignmentId: Long)
}
