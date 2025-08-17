package com.mjs.core.data.source.local

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.AttendanceStreakEntity
import com.mjs.core.data.source.local.entity.DosenEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.KelasEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.data.source.local.room.dao.AttendanceDao
import com.mjs.core.data.source.local.room.dao.AuthDao
import com.mjs.core.data.source.local.room.dao.ClassroomDao
import com.mjs.core.data.source.local.room.dao.ForumDao
import com.mjs.core.data.source.local.room.dao.TaskDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val appPreference: AppPreference,
    private val authDao: AuthDao,
    private val classroomDao: ClassroomDao,
    private val taskDao: TaskDao,
    private val forumDao: ForumDao,
    private val attendanceDao: AttendanceDao,
) {
    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        appPreference.saveThemeSetting(isDarkModeActive)
    }

    fun getThemeSetting(): Flow<Boolean> = appPreference.getThemeSetting()

    fun getLoginStatus(): Flow<Boolean> = appPreference.getLoginStatus()

    suspend fun saveLoginSession(
        userId: Int,
        userType: String,
    ) {
        appPreference.saveLoginSession(userId, userType)
    }

    suspend fun clearLoginSession() {
        appPreference.clearLoginSession()
    }

    fun getLoggedInUserId(): Flow<Int?> = appPreference.getLoggedInUserId()

    fun getLoggedInUserType(): Flow<String?> = appPreference.getLoggedInUserType()

    suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity) = authDao.registerMahasiswa(mahasiswa)

    fun getMahasiswaByNim(nim: Int): Flow<MahasiswaEntity?> = authDao.getMahasiswaByNim(nim)

    suspend fun updateMahasiswa(mahasiswa: MahasiswaEntity) = authDao.updateMahasiswa(mahasiswa)

    fun getDosenByNidn(nidn: Int): Flow<DosenEntity?> = authDao.getDosenByNidn(nidn)

    suspend fun updateDosen(dosen: DosenEntity) = authDao.updateDosen(dosen)

    fun getAllKelas(): Flow<List<KelasEntity>> = classroomDao.getAllKelas()

    fun getAllKelasByJurusan(jurusan: String): Flow<List<KelasEntity>> = classroomDao.getAllKelasByJurusan(jurusan)

    fun getKelasById(kelasId: String): Flow<KelasEntity?> = classroomDao.getKelasById(kelasId)

    suspend fun insertKelas(kelas: KelasEntity) = classroomDao.insertKelas(kelas)

    suspend fun insertEnrollment(enrollment: EnrollmentEntity) = classroomDao.insertEnrollment(enrollment)

    suspend fun deleteEnrollment(
        nim: Int,
        kelasId: String,
    ) = classroomDao.deleteEnrollment(nim, kelasId)

    fun getEnrolledClasses(nim: Int): Flow<List<EnrollmentEntity>> = classroomDao.getEnrolledClasses(nim)

    fun getEnrollmentByNimAndKelasId(
        nim: Int,
        kelasId: String,
    ): Flow<EnrollmentEntity?> = classroomDao.getEnrollmentByNimAndKelasId(nim, kelasId)

    fun getMaterialsByClass(kelasId: String): Flow<List<MaterialEntity>> = classroomDao.getMaterialsByClass(kelasId)

    suspend fun insertMaterial(material: MaterialEntity) = classroomDao.insertMaterial(material)

    fun getAssignmentsByClass(kelasId: String): Flow<List<AssignmentEntity>> = taskDao.getAssignmentsByClass(kelasId)

    suspend fun insertAssignment(assignment: AssignmentEntity) = taskDao.insertAssignment(assignment)

    suspend fun insertSubmission(submission: SubmissionEntity) = taskDao.insertSubmission(submission)

    fun getSubmissionsByAssignment(assignmentId: Int): Flow<List<SubmissionEntity>> = taskDao.getSubmissionsByAssignment(assignmentId)

    fun getForumsByClass(kelasId: String): Flow<List<ForumEntity>> = forumDao.getForumsByClass(kelasId)

    suspend fun insertForum(forum: ForumEntity) = forumDao.insertForum(forum)

    fun getPostsByForum(forumId: Int): Flow<List<PostEntity>> = forumDao.getPostsByForum(forumId)

    suspend fun insertPost(post: PostEntity) = forumDao.insertPost(post)

    suspend fun insertAttendance(attendance: AttendanceEntity) = attendanceDao.insertAttendance(attendance)

    fun getAttendanceHistory(
        nim: Int,
        kelasId: String,
    ): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceHistory(nim, kelasId)

    suspend fun updateAttendanceStreak(streak: AttendanceStreakEntity) = attendanceDao.updateAttendanceStreak(streak)

    fun getAttendanceStreak(
        nim: Int,
        kelasId: String,
    ): Flow<AttendanceStreakEntity?> = attendanceDao.getAttendanceStreak(nim, kelasId)

    fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = taskDao.getNotFinishedTasks(nim, kelasId, currentDate)

    fun getLateTasks(
        nim: Int,
        kelasId: String,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = taskDao.getLateTasks(nim, kelasId, currentDate)

    fun getAssignmentsByKelasIdsAndFutureDeadline(
        kelasIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = taskDao.getAssignmentsByKelasIdsAndFutureDeadline(kelasIds, currentDate)

    fun getAssignmentsByKelasIdsAndPastDeadline(
        kelasIds: List<String>,
        currentDate: String,
    ): Flow<List<AssignmentEntity>> = taskDao.getAssignmentsByKelasIdsAndPastDeadline(kelasIds, currentDate)

    fun getMahasiswaByKelasId(kelasId: String): Flow<List<MahasiswaEntity>> = classroomDao.getMahasiswaByKelasId(kelasId)

    fun getMahasiswaCountByKelasId(kelasId: String): Flow<Int> = classroomDao.getMahasiswaCountByKelasId(kelasId)

    fun getAllSchedulesByNim(nim: Int): Flow<List<KelasEntity>> = classroomDao.getAllSchedulesByNim(nim)
}
