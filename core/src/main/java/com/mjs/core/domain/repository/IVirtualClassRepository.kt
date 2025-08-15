package com.mjs.core.domain.repository

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Kehadiran
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.model.Materi
import com.mjs.core.domain.model.Postingan
import com.mjs.core.domain.model.Tugas
import kotlinx.coroutines.flow.Flow

interface IVirtualClassRepository {
    // Theme
    fun getThemeSetting(): Flow<Boolean>

    suspend fun saveThemeSetting(isDarkModeActive: Boolean)

    // Session Management & User Info
    fun getLoginStatus(): Flow<Boolean> // Baru

    suspend fun saveLoginSession(userId: Int, userType: String) // Baru

    suspend fun clearLoginSession() // Baru

    fun getLoggedInUserId(): Flow<Int?>

    fun getLoggedInUserType(): Flow<String?>

    // Authentication
    fun loginDosen(nidn: String, password: String): Flow<Resource<Dosen>> // Baru

    fun loginMahasiswa(nim: String, password: String): Flow<Resource<Mahasiswa>> // Baru

    // User Profile
    fun getMahasiswaByNim(nim: Int): Flow<Resource<Mahasiswa>>

    fun getDosenByNidn(nidn: Int): Flow<Resource<Dosen>>

    suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity): Flow<Resource<String>>

    suspend fun updateMahasiswaProfile(mahasiswa: Mahasiswa): Flow<Resource<String>>

    suspend fun updateDosenProfile(dosen: Dosen): Flow<Resource<String>>

    // Class Management
    fun getAllKelas(): Flow<Resource<List<Kelas>>>

    fun getEnrolledClasses(nim: Int): Flow<Resource<List<EnrollmentEntity>>>

    suspend fun enrollToClass(enrollment: EnrollmentEntity): Flow<Resource<String>>

    suspend fun leaveClass(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<String>>

    // Materials
    fun getMaterialsByClass(kelasId: String): Flow<Resource<List<Materi>>>

    // Assignments & Submissions
    fun getAssignmentsByClass(kelasId: String): Flow<Resource<List<Tugas>>>

    suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>>

    suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>>

    fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>>

    fun getLateTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>>

    fun getActiveAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>>

    fun getPastDeadlineAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>>

    // Forum & Posts
    fun getForumsByClass(kelasId: String): Flow<Resource<List<Forum>>>

    fun getPostsByForum(forumId: Int): Flow<Resource<List<Postingan>>>

    suspend fun insertPost(post: PostEntity): Flow<Resource<String>>

    // Attendance & Streak
    fun getAttendanceHistory(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Kehadiran>>>

    suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<String>>

    fun getAttendanceStreak(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<Int>>

    // Schedules
    fun getTodaySchedule(nim: Int): Flow<Resource<List<Kelas>>>

    fun getTodayScheduleDosen(nidn: Int): Flow<Resource<List<Kelas>>>

    fun getAllSchedulesByNim(nim: Int): Flow<Resource<List<Kelas>>>

    fun getAllSchedulesByNidn(nidn: Int): Flow<Resource<List<Kelas>>>
}
