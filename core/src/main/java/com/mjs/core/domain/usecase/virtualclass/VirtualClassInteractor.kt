package com.mjs.core.domain.usecase.virtualclass

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.model.Attendance
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.model.Material
import com.mjs.core.domain.model.Post
import com.mjs.core.domain.model.Student
import com.mjs.core.domain.repository.IVirtualClassRepository
import com.mjs.core.ui.submission.SubmissionListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VirtualClassInteractor(
    private val virtualClassRepository: IVirtualClassRepository,
) : VirtualClassUseCase {
    override fun getThemeSetting(): Flow<Boolean> = virtualClassRepository.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) = virtualClassRepository.saveThemeSetting(isDarkModeActive)

    override fun getLoggedInUserId(): Flow<Long?> = virtualClassRepository.getLoggedInUserId()

    override fun getLoggedInUserType(): Flow<String?> = virtualClassRepository.getLoggedInUserType()

    override fun getLoginStatus(): Flow<Boolean> = virtualClassRepository.getLoginStatus()

    override suspend fun saveLoginSession(
        userId: Long,
        userType: String,
    ) = virtualClassRepository.saveLoginSession(userId, userType)

    override suspend fun clearLoginSession() = virtualClassRepository.clearLoginSession()

    override fun loginLecturer(
        lecturerId: Long,
        password: String,
    ): Flow<Resource<Lecturer>> = virtualClassRepository.loginLecturer(lecturerId, password)

    override fun loginStudent(
        studentId: Long,
        password: String,
    ): Flow<Resource<Student>> = virtualClassRepository.loginStudent(studentId, password)

    override fun getStudentByStudentId(studentId: Long): Flow<Resource<Student>> = virtualClassRepository.getStudentByStudentId(studentId)

    override fun getLecturerByLecturerId(lecturerId: Long): Flow<Resource<Lecturer>> =
        virtualClassRepository.getLecturerByLecturerId(lecturerId)

    override suspend fun registerStudent(student: StudentEntity): Flow<Resource<String>> = virtualClassRepository.registerStudent(student)

    override suspend fun updateStudentProfile(student: Student): Flow<Resource<String>> =
        virtualClassRepository.updateStudentProfile(student)

    override suspend fun updateLecturerProfile(lecturer: Lecturer): Flow<Resource<String>> =
        virtualClassRepository.updateLecturerProfile(lecturer)

    override fun getAllCourse(): Flow<Resource<List<Course>>> = virtualClassRepository.getAllCourse()

    override fun getAllCourseByDepartment(department: String): Flow<Resource<List<Course>>> =
        virtualClassRepository.getAllCourseByDepartment(department)

    override fun getCourseById(courseId: String): Flow<Resource<Course>> = virtualClassRepository.getCourseById(courseId)

    override fun getEnrolledCourse(studentId: Long): Flow<Resource<List<EnrollmentEntity>>> =
        virtualClassRepository.getEnrolledCourse(studentId)

    override fun getEnrollmentByStudentIdAndCourseId(
        studentId: Long,
        courseId: String,
    ): Flow<EnrollmentEntity?> = virtualClassRepository.getEnrollmentByStudentIdAndCourseId(studentId, courseId)

    override suspend fun enrollToCourse(enrollment: EnrollmentEntity): Flow<Resource<String>> =
        virtualClassRepository.enrollToCourse(enrollment)

    override suspend fun leaveCourse(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>> = virtualClassRepository.leaveCourse(studentId, courseId)

    override suspend fun acceptEnrollment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>> = virtualClassRepository.updateEnrollmentStatus(studentId, courseId, "approved")

    override suspend fun rejectEnrollment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>> = virtualClassRepository.updateEnrollmentStatus(studentId, courseId, "rejected")

    override fun getMaterialsByCourse(courseId: String): Flow<Resource<List<Material>>> =
        virtualClassRepository.getMaterialsByCourse(courseId)

    override fun getAssignmentsByCourse(courseId: String): Flow<Resource<List<Assignment>>> =
        virtualClassRepository.getAssignmentsByCourse(courseId)

    override fun getAssignmentById(assignmentId: Long): Flow<Resource<Assignment?>> = virtualClassRepository.getAssignmentById(assignmentId)

    override suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>> =
        virtualClassRepository.insertAssignment(assignment)

    override suspend fun updateAssignment(assignment: Assignment): Flow<Resource<String>> =
        virtualClassRepository.updateAssignment(assignment)

    override suspend fun deleteAssignmentById(assignmentId: Long): Flow<Resource<String>> =
        virtualClassRepository.deleteAssignmentById(assignmentId)

    override suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>> =
        virtualClassRepository.insertSubmission(submission)

    override fun getSubmissionListItemsByAssignment(assignmentId: Long): Flow<Resource<List<SubmissionListItem>>> =
        virtualClassRepository.getSubmissionListItemsByAssignment(assignmentId)

    override fun getSubmissionDetailById(submissionId: Long): Flow<Resource<SubmissionListItem?>> =
        virtualClassRepository.getSubmissionDetailById(submissionId)

    override suspend fun updateSubmissionGradeAndNote(
        submissionId: Long,
        grade: Int?,
        note: String?,
    ): Flow<Resource<String>> = virtualClassRepository.updateSubmissionGradeAndNote(submissionId, grade, note)

    override fun getNotFinishedAssignment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Assignment>>> = virtualClassRepository.getNotFinishedAssignment(studentId, courseId)

    override fun getLateAssignment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Assignment>>> = virtualClassRepository.getLateAssignment(studentId, courseId)

    override fun getActiveAssignmentsForLecturer(lecturerId: Long): Flow<Resource<List<Assignment>>> =
        virtualClassRepository.getActiveAssignmentsForLecturer(lecturerId)

    override fun getPastDeadlineAssignmentsForLecturer(lecturerId: Long): Flow<Resource<List<Assignment>>> =
        virtualClassRepository.getPastDeadlineAssignmentsForLecturer(lecturerId)

    override fun getForumsByCourse(courseId: String): Flow<Resource<List<Forum>>> = virtualClassRepository.getForumsByCourse(courseId)

    override fun getPostsByForum(forumId: Long): Flow<Resource<List<Post>>> = virtualClassRepository.getPostsByForum(forumId)

    override suspend fun insertPost(post: PostEntity): Flow<Resource<String>> = virtualClassRepository.insertPost(post)

    override fun getAttendanceHistory(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Attendance>>> =
        virtualClassRepository.getAttendanceHistory(studentId, courseId)

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<Long>> =
        virtualClassRepository.insertAttendance(attendance).map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val attendanceId = resource.data
                    if (attendanceId != null) {
                        Resource.Success(attendanceId)
                    } else {
                        Resource.Error("Failed to record attendance: Received null ID from repository")
                    }
                }

                is Resource.Error ->
                    Resource.Error(resource.message ?: "Failed to record attendance")

                is Resource.Loading ->
                    Resource.Loading()
            }
        }

    override fun getTodaySchedule(studentId: Long): Flow<Resource<List<Course>>> =
        virtualClassRepository.getTodaySchedule(studentId)

    override fun getTodayScheduleLecturer(lecturerId: Long): Flow<Resource<List<Course>>> =
        virtualClassRepository.getTodayScheduleLecturer(lecturerId)

    override fun getAllSchedulesByStudentId(studentId: Long): Flow<Resource<List<Course>>> =
        virtualClassRepository.getAllSchedulesByStudentId(studentId)

    override fun getAllSchedulesByLecturerId(lecturerId: Long): Flow<Resource<List<Course>>> =
        virtualClassRepository.getAllSchedulesByLecturerId(lecturerId)

    override fun getAttendanceStreak(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<Int>> = virtualClassRepository.getAttendanceStreak(studentId, courseId)

    override fun getStudentByCourseId(courseId: String): Flow<Resource<List<Student>>> =
        virtualClassRepository.getStudentByCourseId(courseId)

    override fun getStudentCountByCourseId(courseId: String): Flow<Resource<Int>> =
        virtualClassRepository.getStudentCountByCourseId(courseId)

    override fun getPendingEnrollmentRequests(courseId: String): Flow<Resource<List<Student>>> =
        virtualClassRepository.getPendingEnrollmentRequests(courseId)

    override fun getPendingEnrollmentRequestCount(courseId: String): Flow<Resource<Int>> =
        virtualClassRepository.getPendingEnrollmentRequestCount(courseId)

    override fun getPendingEnrollmentRequestCountForLecturer(lecturerId: Long): Flow<Resource<Int>> =
        virtualClassRepository.getPendingEnrollmentRequestCountForLecturer(lecturerId)

    override fun getLastNotifiedPendingCount(): Flow<Int> = virtualClassRepository.getLastNotifiedPendingCount()

    override suspend fun saveLastNotifiedPendingCount(count: Int) = virtualClassRepository.saveLastNotifiedPendingCount(count)
}
