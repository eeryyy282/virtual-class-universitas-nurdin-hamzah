package com.mjs.core.data.repository

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceStreakEntity
import com.mjs.core.data.source.local.pref.AppPreference
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
import com.mjs.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class VirtualClassRepository(
    private val localDataSource: LocalDataSource,
) : IVirtualClassRepository {
    private fun getCurrentDate(): Date = Date()

    override fun getLoginStatus(): Flow<Boolean> = localDataSource.getLoginStatus()

    override suspend fun saveLoginSession(
        userId: Long,
        userType: String,
    ) {
        localDataSource.saveLoginSession(userId, userType)
    }

    override suspend fun clearLoginSession() {
        localDataSource.clearLoginSession()
    }

    override fun loginLecturer(
        lecturerId: Long,
        password: String,
    ): Flow<Resource<Lecturer>> =
        flow {
            emit(Resource.Loading())
            try {
                val lecturerEntity = localDataSource.getLecturerById(lecturerId).first()
                if (lecturerEntity != null) {
                    if (lecturerEntity.password == password) {
                        saveLoginSession(
                            lecturerEntity.lecturerId,
                            AppPreference.USER_TYPE_LECTURER,
                        )
                        emit(
                            Resource.Success(
                                DataMapper.mapLecturerEntityToLecturerDomain(
                                    lecturerEntity,
                                ),
                            ),
                        )
                    } else {
                        emit(Resource.Error("Incorrect password."))
                    }
                } else {
                    emit(Resource.Error("Lecturer with ID $lecturerId not found."))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred during lecturer login."))
            }
        }

    override fun loginStudent(
        studentId: Long,
        password: String,
    ): Flow<Resource<Student>> =
        flow {
            emit(Resource.Loading())
            try {
                val studentEntity = localDataSource.getStudentById(studentId).first()
                if (studentEntity != null) {
                    if (studentEntity.password == password) {
                        saveLoginSession(studentEntity.studentId, AppPreference.USER_TYPE_STUDENT)
                        emit(
                            Resource.Success(
                                DataMapper.mapStudentEntityToStudentDomain(
                                    studentEntity,
                                ),
                            ),
                        )
                    } else {
                        emit(Resource.Error("Incorrect password."))
                    }
                } else {
                    emit(Resource.Error("Student with ID $studentId not found."))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred during student login."))
            }
        }

    override fun getThemeSetting(): Flow<Boolean> = localDataSource.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) = localDataSource.saveThemeSetting(isDarkModeActive)

    override fun getLoggedInUserId(): Flow<Long?> = localDataSource.getLoggedInUserId()

    override fun getLoggedInUserType(): Flow<String?> = localDataSource.getLoggedInUserType()

    override fun getStudentByStudentId(studentId: Long): Flow<Resource<Student>> =
        localDataSource
            .getStudentById(studentId)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapStudentEntityToStudentDomain(entity))
                } else {
                    Resource.Error("Student with ID $studentId not found")
                }
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error("Failed to retrieve student data with ID $studentId: ${e.message}")) }

    override fun getLecturerByLecturerId(lecturerId: Long): Flow<Resource<Lecturer>> =
        localDataSource
            .getLecturerById(lecturerId)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapLecturerEntityToLecturerDomain(entity))
                } else {
                    Resource.Error("Lecturer with ID $lecturerId not found")
                }
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(Resource.Error("Failed to retrieve lecturer data with ID $lecturerId: ${e.message}"))
            }

    override suspend fun registerStudent(student: StudentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val existingUser = localDataSource.getStudentById(student.studentId).first()
                if (existingUser != null) {
                    emit(Resource.Error("Student ID already registered!"))
                } else {
                    localDataSource.registerStudent(student)
                    emit(Resource.Success("Registration successful! Please log in."))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred during registration"))
            }
        }

    override fun getAllCourse(): Flow<Resource<List<Course>>> =
        localDataSource
            .getAllCourses()
            .map<List<com.mjs.core.data.source.local.entity.CourseEntity>, Resource<List<Course>>> { courseEntities ->
                Resource.Success(DataMapper.mapCourseEntitiesToCourseDomains(courseEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to retrieve all courses")) }

    override fun getAllCourseByDepartment(department: String): Flow<Resource<List<Course>>> =
        localDataSource
            .getAllCoursesByDepartment(department)
            .map<List<com.mjs.core.data.source.local.entity.CourseEntity>, Resource<List<Course>>> { courseEntities ->
                Resource.Success(DataMapper.mapCourseEntitiesToCourseDomains(courseEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve courses by department",
                    ),
                )
            }

    override fun getCourseById(courseId: String): Flow<Resource<Course>> =
        localDataSource
            .getCourseById(courseId)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapCourseEntityToCourseDomain(entity))
                } else {
                    Resource.Error("Course with ID $courseId not found")
                }
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve course by ID",
                    ),
                )
            }

    override fun getEnrolledCourse(studentId: Long): Flow<Resource<List<EnrollmentEntity>>> =
        localDataSource
            .getEnrollmentsByStudentId(studentId)
            .map<List<EnrollmentEntity>, Resource<List<EnrollmentEntity>>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to retrieve enrolled courses")) }

    override fun getEnrollmentByStudentIdAndCourseId(
        studentId: Long,
        courseId: String,
    ): Flow<EnrollmentEntity?> = localDataSource.getEnrollmentByStudentIdAndCourseId(studentId, courseId)

    override suspend fun enrollToCourse(enrollment: EnrollmentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val existingEnrollment =
                    localDataSource
                        .getEnrollmentByStudentIdAndCourseId(
                            enrollment.studentId,
                            enrollment.courseId,
                        ).first()
                if (existingEnrollment != null) {
                    when (existingEnrollment.status) {
                        "approved" -> emit(Resource.Error("You are already enrolled in this course."))
                        "pending" -> emit(Resource.Error("Enrollment request for this course is still pending."))
                        else -> {
                            localDataSource.insertEnrollment(
                                enrollment.copy(
                                    enrollId = existingEnrollment.enrollId,
                                    status = "pending",
                                    enrollmentDate = getCurrentDate(),
                                ),
                            )
                            emit(Resource.Success("Enrollment request sent!"))
                        }
                    }
                } else {
                    localDataSource.insertEnrollment(
                        enrollment.copy(
                            status = "pending",
                            enrollmentDate = getCurrentDate(),
                        ),
                    )
                    emit(Resource.Success("Enrollment request sent!"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to send request"))
            }
        }

    override suspend fun leaveCourse(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.deleteEnrollment(studentId, courseId)
                emit(Resource.Success("Successfully left the course"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to leave the course"))
            }
        }

    override suspend fun updateEnrollmentStatus(
        studentId: Long,
        courseId: String,
        newStatus: String,
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.updateEnrollmentStatus(studentId, courseId, newStatus)
                val message =
                    if (newStatus == "approved") "Student successfully accepted" else "Student successfully rejected"
                emit(Resource.Success(message))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to update enrollment status"))
            }
        }

    override fun getMaterialsByCourse(courseId: String): Flow<Resource<List<Material>>> =
        localDataSource
            .getMaterialsByCourseId(courseId)
            .map<List<com.mjs.core.data.source.local.entity.MaterialEntity>, Resource<List<Material>>> { materialEntities ->
                Resource.Success(DataMapper.mapMaterialEntitiesToMaterialDomains(materialEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to retrieve course materials")) }

    override fun getAssignmentsByCourse(courseId: String): Flow<Resource<List<Assignment>>> =
        localDataSource
            .getAssignmentsByCourse(courseId)
            .map<List<AssignmentEntity>, Resource<List<Assignment>>> { assignmentEntities ->
                Resource.Success(
                    DataMapper.mapAssignmentEntitiesToAssignmentDomains(
                        assignmentEntities,
                    ),
                )
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve course assignments",
                    ),
                )
            }

    override fun getAssignmentById(assignmentId: Long): Flow<Resource<Assignment?>> =
        localDataSource
            .getAssignmentById(assignmentId)
            .map<AssignmentEntity?, Resource<Assignment?>> { entity ->
                Resource.Success(entity?.let { DataMapper.mapAssignmentEntityToAssignmentDomain(it) })
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve assignment by ID",
                    ),
                )
            }

    override suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertAssignment(assignment.copy(startDate = getCurrentDate()))
                emit(Resource.Success("Assignment successfully added"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to add assignment"))
            }
        }

    override suspend fun updateAssignment(assignment: Assignment): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val assignmentEntity = DataMapper.mapAssignmentDomainToAssignmentEntity(assignment)
                localDataSource.updateAssignment(assignmentEntity)
                emit(Resource.Success("Assignment successfully updated"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to update assignment"))
            }
        }

    override suspend fun deleteAssignmentById(assignmentId: Long): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.deleteAssignmentById(assignmentId)
                emit(Resource.Success("Assignment successfully deleted"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to delete assignment"))
            }
        }

    override suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertSubmission(submission.copy(submissionDate = getCurrentDate()))
                emit(Resource.Success("Assignment successfully submitted"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to submit assignment"))
            }
        }

    override fun getSubmissionListItemsByAssignment(assignmentId: Long): Flow<Resource<List<SubmissionListItem>>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.getSubmissionsByAssignment(assignmentId).collect { submissions ->
                    val submissionListItems =
                        submissions.mapNotNull { submissionEntity ->
                            val studentEntity =
                                localDataSource.getStudentById(submissionEntity.studentId).first()
                            studentEntity?.let {
                                SubmissionListItem(
                                    submissionEntity = submissionEntity,
                                    studentName = it.name,
                                    studentPhotoUrl = it.profilePicture,
                                )
                            }
                        }
                    emit(Resource.Success(submissionListItems))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to retrieve submission list"))
            }
        }

    override fun getSubmissionDetailById(submissionId: Long): Flow<Resource<SubmissionListItem?>> =
        flow {
            emit(Resource.Loading())
            try {
                val submissionEntity = localDataSource.getSubmissionById(submissionId).first()
                if (submissionEntity != null) {
                    val studentEntity =
                        localDataSource.getStudentById(submissionEntity.studentId).first()
                    val submissionListItem =
                        studentEntity?.let {
                            SubmissionListItem(
                                submissionEntity = submissionEntity,
                                studentName = it.name,
                                studentPhotoUrl = it.profilePicture,
                            )
                        }
                    emit(Resource.Success(submissionListItem))
                } else {
                    emit(Resource.Success(null))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to retrieve submission detail"))
            }
        }

    override suspend fun updateSubmissionGradeAndNote(
        submissionId: Long,
        grade: Int?,
        note: String?,
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.updateSubmissionGradeAndNote(submissionId, grade, note)
                emit(Resource.Success("Grade and note saved successfully"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to save grade and note"))
            }
        }

    override fun getNotFinishedAssignment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Assignment>>> =
        localDataSource
            .getNotFinishedAssignments(
                studentId,
                courseId,
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault(),
                ).format(getCurrentDate()),
            ).map<List<AssignmentEntity>, Resource<List<Assignment>>> { assignmentEntities ->
                Resource.Success(
                    DataMapper.mapAssignmentEntitiesToAssignmentDomains(
                        assignmentEntities,
                    ),
                )
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve unfinished assignments",
                    ),
                )
            }

    override fun getLateAssignment(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Assignment>>> =
        localDataSource
            .getLateAssignments(
                studentId,
                courseId,
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault(),
                ).format(getCurrentDate()),
            ).map<List<AssignmentEntity>, Resource<List<Assignment>>> { assignmentEntities ->
                Resource.Success(
                    DataMapper.mapAssignmentEntitiesToAssignmentDomains(
                        assignmentEntities,
                    ),
                )
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve late assignments",
                    ),
                )
            }

    override fun getActiveAssignmentsForLecturer(lecturerId: Long): Flow<Resource<List<Assignment>>> =
        flow {
            emit(Resource.Loading())
            val currentDateFormatted =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault(),
                ).format(getCurrentDate())
            localDataSource.getAllCourses().collect { allCourses ->

                val courseIds =
                    allCourses.filter { it.lecturerId == lecturerId }.map { it.courseId }
                if (courseIds.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                } else {
                    localDataSource
                        .getAssignmentsByCourseIdsAndFutureDeadline(courseIds, currentDateFormatted)
                        .map { DataMapper.mapAssignmentEntitiesToAssignmentDomains(it) }
                        .collect { emit(Resource.Success(it)) }
                }
            }
        }.catch { e ->
            emit(
                Resource.Error(
                    e.message ?: "Failed to retrieve active assignments for lecturer",
                ),
            )
        }

    override fun getPastDeadlineAssignmentsForLecturer(lecturerId: Long): Flow<Resource<List<Assignment>>> =
        flow {
            emit(Resource.Loading())
            val currentDateFormatted =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault(),
                ).format(getCurrentDate())
            localDataSource.getAllCourses().collect { allCourses ->

                val courseIds =
                    allCourses.filter { it.lecturerId == lecturerId }.map { it.courseId }
                if (courseIds.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                } else {
                    localDataSource
                        .getAssignmentsByCourseIdsAndPastDeadline(courseIds, currentDateFormatted)
                        .map { DataMapper.mapAssignmentEntitiesToAssignmentDomains(it) }
                        .collect { emit(Resource.Success(it)) }
                }
            }
        }.catch { e ->
            emit(
                Resource.Error(
                    e.message ?: "Failed to retrieve past assignments for lecturer",
                ),
            )
        }

    override fun getForumsByCourse(courseId: String): Flow<Resource<List<Forum>>> =
        localDataSource
            .getForumsByCourse(courseId)
            .map<List<com.mjs.core.data.source.local.entity.ForumEntity>, Resource<List<Forum>>> { forumEntities ->
                Resource.Success(DataMapper.mapForumEntitiesToDomains(forumEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to retrieve course forums")) }

    override fun getPostsByForum(forumId: Long): Flow<Resource<List<Post>>> =
        localDataSource
            .getPostsByForumId(forumId)
            .map<List<PostEntity>, Resource<List<Post>>> { postEntities ->
                Resource.Success(DataMapper.mapPostEntitiesToPostDomains(postEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to retrieve forum posts")) }

    override suspend fun insertPost(post: PostEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertPost(post.copy(postDate = getCurrentDate()))
                emit(Resource.Success("Post successfully added"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to add post"))
            }
        }

    override fun getAttendanceHistory(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<List<Attendance>>> =
        localDataSource
            .getAttendanceHistory(studentId, courseId)
            .map<List<AttendanceEntity>, Resource<List<Attendance>>> { attendanceEntities ->
                Resource.Success(
                    DataMapper.mapAttendanceEntitiesToAttendanceDomains(
                        attendanceEntities,
                    ),
                )
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve attendance history",
                    ),
                )
            }

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<Long>> =
        flow {
            emit(Resource.Loading())
            try {
                val attendanceToInsert =
                    attendance
                val insertedId = localDataSource.insertAttendance(attendanceToInsert)
                emit(Resource.Success(insertedId))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to record attendance"))
            }
        }

    override fun getTodaySchedule(studentId: Long): Flow<Resource<List<Course>>> =
        flow {
            emit(Resource.Loading())
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            localDataSource
                .getAllCoursesByStudentId(studentId)
                .map { courseEntities ->
                    DataMapper
                        .mapCourseEntitiesToCourseDomains(courseEntities)
                        .filter { course ->
                            val scheduleCalendar = Calendar.getInstance()
                            scheduleCalendar.time = course.schedule
                            scheduleCalendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
                        }
                }.collect { emit(Resource.Success(it)) }
        }.catch { e -> emit(Resource.Error(e.message ?: "Failed to retrieve today's schedule")) }

    override fun getTodayScheduleLecturer(lecturerId: Long): Flow<Resource<List<Course>>> =
        flow {
            emit(Resource.Loading())
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            localDataSource
                .getAllCourses()
                .map { courseEntities ->
                    courseEntities
                        .filter { it.lecturerId == lecturerId }
                        .map { DataMapper.mapCourseEntityToCourseDomain(it) }
                        .filter { course ->
                            val scheduleCalendar = Calendar.getInstance()
                            scheduleCalendar.time = course.schedule
                            scheduleCalendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
                        }
                }.collect { emit(Resource.Success(it)) }
        }.catch { e ->
            emit(
                Resource.Error(
                    e.message ?: "Failed to retrieve lecturer's schedule for today",
                ),
            )
        }

    override fun getAllSchedulesByStudentId(studentId: Long): Flow<Resource<List<Course>>> =
        localDataSource
            .getAllCoursesByStudentId(studentId)
            .map<List<com.mjs.core.data.source.local.entity.CourseEntity>, Resource<List<Course>>> { courseEntities ->
                Resource.Success(DataMapper.mapCourseEntitiesToCourseDomains(courseEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve all student schedules",
                    ),
                )
            }

    override fun getAllSchedulesByLecturerId(lecturerId: Long): Flow<Resource<List<Course>>> =
        localDataSource
            .getAllCourses()
            .map<List<com.mjs.core.data.source.local.entity.CourseEntity>, Resource<List<Course>>> { courseEntities ->
                Resource.Success(DataMapper.mapCourseEntitiesToCourseDomains(courseEntities.filter { it.lecturerId == lecturerId }))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve all lecturer schedules",
                    ),
                )
            }

    override fun getAttendanceStreak(
        studentId: Long,
        courseId: String,
    ): Flow<Resource<Int>> =
        localDataSource
            .getAttendanceStreak(studentId, courseId)
            .map<AttendanceStreakEntity?, Resource<Int>> { streakEntity ->
                Resource.Success(streakEntity?.currentStreak ?: 0)
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Failed to get attendance streak")) }

    override suspend fun updateStudentProfile(student: Student): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val currentEntity = localDataSource.getStudentById(student.studentId).first()
                if (currentEntity != null) {
                    val updatedEntity =
                        DataMapper
                            .mapStudentDomainToStudentEntity(student)
                            .copy(password = currentEntity.password)
                    localDataSource.updateStudent(updatedEntity)
                    emit(Resource.Success("Profile updated successfully"))
                } else {
                    emit(Resource.Error("Student not found for update"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to update profile"))
            }
        }

    override suspend fun updateLecturerProfile(lecturer: Lecturer): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val currentEntity = localDataSource.getLecturerById(lecturer.lecturerId).first()
                if (currentEntity != null) {
                    val updatedEntity =
                        DataMapper
                            .mapLecturerDomainToLecturerEntity(lecturer)
                            .copy(password = currentEntity.password)
                    localDataSource.updateLecturer(updatedEntity)
                    emit(Resource.Success("Profile updated successfully"))
                } else {
                    emit(Resource.Error("Lecturer not found for update"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to update profile"))
            }
        }

    override fun getStudentByCourseId(courseId: String): Flow<Resource<List<Student>>> =
        localDataSource
            .getStudentsByCourseId(courseId)
            .map<List<StudentEntity>, Resource<List<Student>>> { studentEntities ->
                Resource.Success(DataMapper.mapStudentsEntitiesToStudentsDomains(studentEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve student data by course",
                    ),
                )
            }

    override fun getStudentCountByCourseId(courseId: String): Flow<Resource<Int>> =
        localDataSource
            .getStudentCountByCourseId(courseId)
            .map<Int, Resource<Int>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve student count in course",
                    ),
                )
            }

    override fun getPendingEnrollmentRequests(courseId: String): Flow<Resource<List<Student>>> =
        localDataSource
            .getPendingEnrollmentRequests(courseId)
            .map<List<StudentEntity>, Resource<List<Student>>> { studentEntities ->
                Resource.Success(DataMapper.mapStudentsEntitiesToStudentsDomains(studentEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve pending enrollment requests",
                    ),
                )
            }

    override fun getPendingEnrollmentRequestCount(courseId: String): Flow<Resource<Int>> =
        localDataSource
            .getPendingEnrollmentRequestCount(courseId)
            .map<Int, Resource<Int>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Failed to retrieve pending enrollment request count",
                    ),
                )
            }

    override fun getPendingEnrollmentRequestCountForLecturer(lecturerId: Long): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.getAllCourses().collect { lecturerCourses ->

                    val lecturerCoursesFiltered =
                        lecturerCourses.filter { it.lecturerId == lecturerId }
                    if (lecturerCoursesFiltered.isEmpty()) {
                        emit(Resource.Success(0))
                    } else {
                        val countFlows =
                            lecturerCoursesFiltered.map { course ->
                                localDataSource.getPendingEnrollmentRequestCount(course.courseId)
                            }
                        kotlinx.coroutines.flow
                            .combine(countFlows) { counts ->
                                counts.sum()
                            }.collect { totalPendingRequests ->
                                emit(Resource.Success(totalPendingRequests))
                            }
                    }
                }
            } catch (e: Exception) {
                emit(
                    Resource.Error(
                        e.message
                            ?: "Failed to retrieve pending enrollment request count for lecturer",
                    ),
                )
            }
        }

    override fun getLastNotifiedPendingCount(): Flow<Int> = localDataSource.getLastNotifiedPendingCount()

    override suspend fun saveLastNotifiedPendingCount(count: Int) = localDataSource.saveLastNotifiedPendingCount(count)
}
