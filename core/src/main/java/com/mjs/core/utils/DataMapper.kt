package com.mjs.core.utils

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.LecturerEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.domain.model.Assignment
import com.mjs.core.domain.model.Attendance
import com.mjs.core.domain.model.Course
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Lecturer
import com.mjs.core.domain.model.Material
import com.mjs.core.domain.model.Post
import com.mjs.core.domain.model.Student
import java.util.Calendar

object DataMapper {
    fun mapStudentEntityToStudentDomain(input: StudentEntity): Student =
        Student(
            studentId = input.studentId,
            name = input.name,
            email = input.email,
            profilePicture = input.profilePicture,
            supervisor = input.supervisor,
            department = input.department,
        )

    fun mapStudentsEntitiesToStudentsDomains(input: List<StudentEntity>): List<Student> = input.map { mapStudentEntityToStudentDomain(it) }

    fun mapStudentDomainToStudentEntity(input: Student): StudentEntity =
        StudentEntity(
            studentId = input.studentId,
            name = input.name,
            email = input.email,
            profilePicture = input.profilePicture,
            supervisor = input.supervisor,
            department = input.department,
            password = "",
        )

    fun mapLecturerEntityToLecturerDomain(input: LecturerEntity): Lecturer =
        Lecturer(
            lecturerId = input.lecturerId,
            name = input.name,
            email = input.email,
            profilePicture = input.profilePicture,
        )

    fun mapLecturerDomainToLecturerEntity(input: Lecturer): LecturerEntity =
        LecturerEntity(
            lecturerId = input.lecturerId,
            name = input.name,
            email = input.email,
            profilePicture = input.profilePicture,
            password = "",
        )

    fun mapCourseEntityToCourseDomain(input: CourseEntity): Course =
        Course(
            courseId = input.courseId,
            courseName = input.courseName,
            description = input.description,
            lecturerId = input.lecturerId,
            schedule = input.schedule,
            semester = input.semester,
            credit = input.credit,
            department = input.department,
            courseImage = input.courseImage,
            room = input.room,
        )

    fun mapCourseEntitiesToCourseDomains(input: List<CourseEntity>): List<Course> = input.map { mapCourseEntityToCourseDomain(it) }

    fun mapAssignmentEntityToAssignmentDomain(input: AssignmentEntity): Assignment =
        Assignment(
            assignmentId = input.assignmentId,
            courseId = input.courseId,
            assignmentTitle = input.assignmentTitle,
            description = input.description,
            startDate = input.startDate,
            deadlineDate = input.deadlineDate,
            attachment = input.attachment,
        )

    fun mapAssignmentEntitiesToAssignmentDomains(input: List<AssignmentEntity>): List<Assignment> =
        input.map { mapAssignmentEntityToAssignmentDomain(it) }

    fun mapAssignmentDomainToAssignmentEntity(input: Assignment): AssignmentEntity =
        AssignmentEntity(
            assignmentId = input.assignmentId,
            courseId = input.courseId,
            assignmentTitle = input.assignmentTitle,
            description = input.description,
            startDate = input.startDate,
            deadlineDate = input.deadlineDate,
            attachment = input.attachment,
        )

    fun mapMaterialEntityToMaterialDomain(input: MaterialEntity): Material =
        Material(
            materialId = input.materialId,
            courseId = input.courseId,
            courseTitle = input.materialTitle,
            description = input.description,
            attachment = input.attachment,
            uploadDate = input.uploadDate,
            type = input.type,
        )

    fun mapMaterialEntitiesToMaterialDomains(input: List<MaterialEntity>): List<Material> =
        input.map { mapMaterialEntityToMaterialDomain(it) }

    fun mapForumEntityToDomain(input: ForumEntity): Forum =
        Forum(
            forumId = input.forumId,
            courseId = input.courseId,
            forumTitle = input.forumTitle,
            description = input.description,
        )

    fun mapForumEntitiesToDomains(input: List<ForumEntity>): List<Forum> = input.map { mapForumEntityToDomain(it) }

    fun mapPostEntityToPostDomain(input: PostEntity): Post =
        Post(
            postId = input.postId,
            forumId = input.forumId,
            userId = input.userId,
            userRole = input.userRole,
            postContent = input.postContent,
            postDate = input.postDate,
        )

    fun mapPostEntitiesToPostDomains(input: List<PostEntity>): List<Post> = input.map { mapPostEntityToPostDomain(it) }

    fun mapAttendanceEntityToAttendanceDomain(input: AttendanceEntity): Attendance =
        Attendance(
            attendanceId = input.attendanceId,
            courseId = input.courseId,
            studentId = input.studentId,
            attendanceDate = input.attendanceDate,
            status = input.status,
            description = input.description,
        )

    fun mapAttendanceEntitiesToAttendanceDomains(input: List<AttendanceEntity>): List<Attendance> =
        input.map { mapAttendanceEntityToAttendanceDomain(it) }

    fun mapDayOfWeekToDayName(dayOfWeek: Int): String =
        when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> ""
        }
}
