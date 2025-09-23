package com.mjs.core.domain.usecase.virtualclass

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.model.Attendance
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.model.Material
import com.mjs.core.domain.model.Post
import com.mjs.core.domain.model.Student
import com.mjs.core.ui.submission.SubmissionListItem
import kotlinx.coroutines.flow.Flow

interface VirtualClassUseCase {
    fun getThemeSetting(): Flow<Boolean>

    suspend fun saveThemeSetting(isDarkModeActive: Boolean)

    fun getLoggedInUserId(): Flow<Long?>

    fun getLoggedInUserType(): Flow<String?>

    fun getLoginStatus(): Flow<Boolean>

    suspend fun saveLoginSession(
        userId: Long,
        userType: String,
    )

    suspend fun clearLoginSession()

    fun loginLecturer(
        lecturerId: Long,
        password: String,
    ): Flow<Resource<Lecturer>>

    fun loginStudent(
        studentId: Long,
        password: String,
    ): Flow<Resource<Student>>

    fun getStudentByStudentId(studentId: Long): Flow<Resource<Student>>

    fun getLecturerByLecturerId(lecturerId: Long): Flow<Resource<Lecturer>>

    suspend fun registerStudent(student: StudentEntity): Flow<Resource<String>>

    suspend fun updateStudentProfile(student: Student): Flow<Resource<String>>

    suspend fun updateLecturerProfile(lecturer: Lecturer): Flow<Resource<String>>

    fun getAllCourse(): Flow<Resource<List<Course>>>

    fun getAllCourseByDepartment(department: String): Flow<Resource<List<Course>>>

    fun getCourseById(courseId: String): Flow<Resource<Course>>

    fun getEnrolledCourse(studentId: Long): Flow<Resource<List<EnrollmentEntity>>>

    fun getEnrollmentByStudentIdAndCourseId(
        studentId: Long,
        courseId: String,
    ): Flow<EnrollmentEntity?>

    suspend fun enrollToCourse(enrollment: EnrollmentEntity): Flow<Resource<String>>

    suspend fun leaveCourse(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>>

    fun getMaterialsByCourse(courseId: String): Flow<Resource<List<Material>>>

    fun getAssignmentsByCourse(courseId: String): Flow<Resource<List<Assignment>>>

    fun getAssignmentById(assignmentId: Long): Flow<Resource<Assignment?>>

    suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>>

    suspend fun updateAssignment(assignment: Assignment): Flow<Resource<String>>

    suspend fun deleteAssignmentById(assignmentId: Long): Flow<Resource<String>>

    suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>>

    fun getSubmissionListItemsByAssignment(assignmentId: Long): Flow<Resource<List<SubmissionListItem>>>

    fun getSubmissionDetailById(submissionId: Long): Flow<Resource<SubmissionListItem?>>

    suspend fun updateSubmissionGradeAndNote(
        submissionId: Long,
        grade: Int?,
        note: String?,
    ): Flow<Resource<String>>

    fun getNotFinishedAssignment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Assignment>>>

    fun getLateAssignment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Assignment>>>

    fun getActiveAssignmentsForLecturer(lecturerId: Long): Flow<Resource<List<Assignment>>>

    fun getPastDeadlineAssignmentsForLecturer(lecturerId: Long): Flow<Resource<List<Assignment>>>

    fun getForumsByCourse(courseId: String): Flow<Resource<List<Forum>>>

    fun getPostsByForum(forumId: Long): Flow<Resource<List<Post>>>

    suspend fun insertPost(post: PostEntity): Flow<Resource<String>>

    fun getAttendanceHistory(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Attendance>>>

    suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<Long>>

    fun getTodaySchedule(studentId: Long): Flow<Resource<List<Course>>>

    fun getTodayScheduleLecturer(lecturerId: Long): Flow<Resource<List<Course>>>

    fun getAllSchedulesByStudentId(studentId: Long): Flow<Resource<List<Course>>>

    fun getAllSchedulesByLecturerId(lecturerId: Long): Flow<Resource<List<Course>>>

    fun getAttendanceStreak(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<Int>>

    fun getStudentByCourseId(courseId: String): Flow<Resource<List<Student>>>

    fun getStudentCountByCourseId(courseId: String): Flow<Resource<Int>>

    fun getPendingEnrollmentRequests(courseId: String): Flow<Resource<List<Student>>>

    fun getPendingEnrollmentRequestCount(courseId: String): Flow<Resource<Int>>

    fun getPendingEnrollmentRequestCountForLecturer(lecturerId: Long): Flow<Resource<Int>>

    suspend fun acceptEnrollment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>>

    suspend fun rejectEnrollment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>>

    fun getLastNotifiedPendingCount(): Flow<Int>

    suspend fun saveLastNotifiedPendingCount(count: Int)

    companion object {
        const val USER_TYPE_STUDENT = AppPreference.USER_TYPE_STUDENT
        const val USER_TYPE_LECTURER = AppPreference.USER_TYPE_LECTURER
    }
}
