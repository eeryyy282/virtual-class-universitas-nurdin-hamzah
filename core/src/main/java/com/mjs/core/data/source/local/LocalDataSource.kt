package com.mjs.core.data.source.local

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.LecturerEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceStreakEntity
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.data.source.local.room.dao.AssignmentDao
import com.mjs.core.data.source.local.room.dao.AttendanceDao
import com.mjs.core.data.source.local.room.dao.AuthDao
import com.mjs.core.data.source.local.room.dao.CourseDao
import com.mjs.core.data.source.local.room.dao.ForumDao
import com.mjs.core.data.source.local.room.dao.SubmissionDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val appPreference: AppPreference,
    private val authDao: AuthDao,
    private val courseDao: CourseDao,
    private val assignmentDao: AssignmentDao,
    private val submissionDao: SubmissionDao,
    private val forumDao: ForumDao,
    private val attendanceDao: AttendanceDao,
) {
    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        appPreference.saveThemeSetting(isDarkModeActive)
    }

    fun getThemeSetting(): Flow<Boolean> = appPreference.getThemeSetting()

    fun getLoginStatus(): Flow<Boolean> = appPreference.getLoginStatus()

    suspend fun saveLoginSession(
        userId: Long,
        userType: String,
    ) {
        appPreference.saveLoginSession(userId, userType)
    }

    suspend fun clearLoginSession() {
        appPreference.clearLoginSession()
    }

    fun getLoggedInUserId(): Flow<Long?> = appPreference.getLoggedInUserId()

    fun getLoggedInUserType(): Flow<String?> = appPreference.getLoggedInUserType()

    suspend fun registerStudent(student: StudentEntity) = authDao.registerStudent(student)

    fun getStudentById(studentId: Long): Flow<StudentEntity?> = authDao.getStudentById(studentId)

    suspend fun updateStudent(student: StudentEntity) = authDao.updateStudent(student)

    fun getLecturerById(lecturerId: Long): Flow<LecturerEntity?> = authDao.getLecturerById(lecturerId)

    suspend fun updateLecturer(lecturer: LecturerEntity) = authDao.updateLecturer(lecturer)

    fun getAllCourses(): Flow<List<CourseEntity>> = courseDao.getAllCourses()

    fun getAllCoursesByDepartment(department: String): Flow<List<CourseEntity>> = courseDao.getAllCoursesByDepartment(department)

    fun getCourseById(courseId: String): Flow<CourseEntity?> = courseDao.getCourseById(courseId)

    suspend fun insertEnrollment(enrollment: EnrollmentEntity) = courseDao.insertEnrollment(enrollment)

    suspend fun deleteEnrollment(
        studentId: Long,
        courseId: String,
    ) = courseDao.deleteEnrollment(studentId, courseId)

    fun getEnrollmentsByStudentId(studentId: Long): Flow<List<EnrollmentEntity>> = courseDao.getEnrollmentsByStudentId(studentId)

    fun getEnrollmentByStudentIdAndCourseId(
        studentId: Long,
        courseId: String,
    ): Flow<EnrollmentEntity?> = courseDao.getEnrollmentByStudentIdAndCourseId(studentId, courseId)

    fun getMaterialsByCourseId(courseId: String): Flow<List<MaterialEntity>> = courseDao.getMaterialsByCourseId(courseId)

    fun getAssignmentsByCourse(courseId: String): Flow<List<AssignmentEntity>> = assignmentDao.getAssignmentsByCourse(courseId)

    suspend fun insertAssignment(assignment: AssignmentEntity): Long = assignmentDao.insertAssignment(assignment)

    suspend fun updateAssignment(assignment: AssignmentEntity) = assignmentDao.updateAssignment(assignment)

    fun getAssignmentById(assignmentId: Long): Flow<AssignmentEntity?> = assignmentDao.getAssignmentById(assignmentId)

    suspend fun deleteAssignmentById(assignmentId: Long) = assignmentDao.deleteAssignmentById(assignmentId)

    suspend fun insertSubmission(submission: SubmissionEntity): Long = submissionDao.insertSubmission(submission)

    fun getSubmissionsByAssignment(assignmentId: Long): Flow<List<SubmissionEntity>> =
        submissionDao.getSubmissionsByAssignment(assignmentId)

    fun getSubmissionById(submissionId: Long): Flow<SubmissionEntity?> = submissionDao.getSubmissionById(submissionId)

    suspend fun updateSubmissionGradeAndNote(
        submissionId: Long,
        grade: Int?,
        note: String?,
    ) = submissionDao.updateSubmissionGradeAndNote(submissionId, grade, note)

    fun getForumsByCourse(courseId: String): Flow<List<ForumEntity>> = forumDao.getForumsByCourse(courseId)

    fun getPostsByForumId(forumId: Long): Flow<List<PostEntity>> = forumDao.getPostsByForumId(forumId)

    suspend fun insertPost(post: PostEntity) = forumDao.insertPost(post)

    suspend fun insertAttendance(attendance: AttendanceEntity): Long = attendanceDao.insertAttendance(attendance)

    fun getAttendanceHistory(
        studentId: Long,
        courseId: String,
    ): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceHistory(studentId, courseId)

    fun getAttendanceStreak(
        studentId: Long,
        courseId: String,
    ): Flow<AttendanceStreakEntity?> = attendanceDao.getAttendanceStreak(studentId, courseId)

    fun getNotFinishedAssignments(
        studentId: Long,
        courseId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = assignmentDao.getPendingAssignmentsForStudent(studentId, courseId, currentDate)

    fun getLateAssignments(
        studentId: Long,
        courseId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = assignmentDao.getMissedAssignmentsForStudent(studentId, courseId, currentDate)

    fun getAssignmentsByCourseIdsAndFutureDeadline(
        courseIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = assignmentDao.getActiveAssignmentsByCourses(courseIds, currentDate)

    fun getAssignmentsByCourseIdsAndPastDeadline(
        courseIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = assignmentDao.getPastAssignmentsByCourses(courseIds, currentDate)

    fun getStudentsByCourseId(courseId: String): Flow<List<StudentEntity>> = courseDao.getStudentsByCourseId(courseId)

    fun getStudentCountByCourseId(courseId: String): Flow<Int> = courseDao.getStudentCountByCourseId(courseId)

    fun getAllCoursesByStudentId(studentId: Long): Flow<List<CourseEntity>> = courseDao.getAllCoursesByStudentId(studentId)

    fun getPendingEnrollmentRequests(courseId: String): Flow<List<StudentEntity>> = courseDao.getPendingEnrollmentRequests(courseId)

    fun getPendingEnrollmentRequestCount(courseId: String): Flow<Int> = courseDao.getPendingEnrollmentRequestCount(courseId)

    suspend fun updateEnrollmentStatus(
        studentId: Long,
        courseId: String,
        newStatus: String,
    ) = courseDao.updateEnrollmentStatus(studentId, courseId, newStatus)

    fun getLastNotifiedPendingCount(): Flow<Int> = appPreference.getLastNotifiedPendingCount()

    suspend fun saveLastNotifiedPendingCount(count: Int) = appPreference.saveLastNotifiedPendingCount(count)
}
