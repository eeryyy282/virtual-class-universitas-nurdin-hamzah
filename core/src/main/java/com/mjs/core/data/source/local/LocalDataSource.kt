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

    suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity) = authDao.registerMahasiswa(mahasiswa)

    fun getMahasiswaByNim(nim: String): Flow<MahasiswaEntity?> = authDao.getMahasiswaByNim(nim)

    fun getDosenByNidn(nidn: String): Flow<DosenEntity?> = authDao.getDosenByNidn(nidn)

    fun getAllKelas(): Flow<List<KelasEntity>> = classroomDao.getAllKelas()

    suspend fun insertKelas(kelas: KelasEntity) = classroomDao.insertKelas(kelas)

    suspend fun insertEnrollment(enrollment: EnrollmentEntity) = classroomDao.insertEnrollment(enrollment)

    fun getEnrolledClasses(nim: String): Flow<List<EnrollmentEntity>> = classroomDao.getEnrolledClasses(nim)

    fun getMaterialsByClass(kelasId: Int): Flow<List<MaterialEntity>> = classroomDao.getMaterialsByClass(kelasId)

    suspend fun insertMaterial(material: MaterialEntity) = classroomDao.insertMaterial(material)

    fun getAssignmentsByClass(kelasId: Int): Flow<List<AssignmentEntity>> = taskDao.getAssignmentsByClass(kelasId)

    suspend fun insertAssignment(assignment: AssignmentEntity) = taskDao.insertAssignment(assignment)

    suspend fun insertSubmission(submission: SubmissionEntity) = taskDao.insertSubmission(submission)

    fun getSubmissionsByAssignment(assignmentId: Int): Flow<List<SubmissionEntity>> = taskDao.getSubmissionsByAssignment(assignmentId)

    fun getForumsByClass(kelasId: Int): Flow<List<ForumEntity>> = forumDao.getForumsByClass(kelasId)

    suspend fun insertForum(forum: ForumEntity) = forumDao.insertForum(forum)

    fun getPostsByForum(forumId: Int): Flow<List<PostEntity>> = forumDao.getPostsByForum(forumId)

    suspend fun insertPost(post: PostEntity) = forumDao.insertPost(post)

    suspend fun insertAttendance(attendance: AttendanceEntity) = attendanceDao.insertAttendance(attendance)

    fun getAttendanceHistory(
        nim: String,
        kelasId: Int,
    ): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceHistory(nim, kelasId)

    suspend fun updateAttendanceStreak(streak: AttendanceStreakEntity) = attendanceDao.updateAttendanceStreak(streak)

    fun getAttendanceStreak(
        nim: String,
        kelasId: Int,
    ): Flow<AttendanceStreakEntity?> = attendanceDao.getAttendanceStreak(nim, kelasId)
}
