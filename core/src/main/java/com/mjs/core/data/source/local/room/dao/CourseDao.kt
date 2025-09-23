package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE course_id = :courseId")
    fun getCourseById(courseId: String): Flow<CourseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Query("DELETE FROM enrollments WHERE student_id = :studentId AND course_id = :courseId")
    suspend fun deleteEnrollment(
        studentId: Long,
        courseId: String,
    )

    @Query("SELECT * FROM enrollments WHERE student_id = :studentId")
    fun getEnrollmentsByStudentId(studentId: Long): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE student_id = :studentId AND course_id = :courseId")
    fun getEnrollmentByStudentIdAndCourseId(
        studentId: Long,
        courseId: String,
    ): Flow<EnrollmentEntity?>

    @Query("SELECT * FROM materials WHERE course_id = :courseId")
    fun getMaterialsByCourseId(courseId: String): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("SELECT course_id FROM courses WHERE course_name = :courseName AND lecturer_id = :lecturerId ORDER BY course_id DESC LIMIT 1")
    suspend fun getCourseIdByNameAndLecturerId(
        courseName: String,
        lecturerId: Long,
    ): String?

    @Query("SELECT * FROM courses WHERE department = :department")
    fun getAllCoursesByDepartment(department: String): Flow<List<CourseEntity>>

    @Query(
        "SELECT m.* FROM student m INNER JOIN enrollments e ON m.student_id = e.student_id WHERE e.course_id = :courseId AND e.status = 'approved'",
    )
    fun getStudentsByCourseId(courseId: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(e.student_id) FROM enrollments e WHERE e.course_id = :courseId AND e.status = 'approved'")
    fun getStudentCountByCourseId(courseId: String): Flow<Int>

    @Query(
        "SELECT c.* FROM courses c INNER JOIN enrollments e ON c.course_id = e.course_id WHERE e.student_id = :studentId AND e.status = 'approved'",
    )
    fun getAllCoursesByStudentId(studentId: Long): Flow<List<CourseEntity>>

    @Query(
        "SELECT m.* FROM student m INNER JOIN enrollments e ON m.student_id = e.student_id WHERE e.course_id = :courseId AND e.status = 'pending'",
    )
    fun getPendingEnrollmentRequests(courseId: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(e.student_id) FROM enrollments e WHERE e.course_id = :courseId AND e.status = 'pending'")
    fun getPendingEnrollmentRequestCount(courseId: String): Flow<Int>

    @Query("UPDATE enrollments SET status = :newStatus WHERE student_id = :studentId AND course_id = :courseId")
    suspend fun updateEnrollmentStatus(
        studentId: Long,
        courseId: String,
        newStatus: String,
    )
}
