package com.mjs.core.domain.usecase.virtualclass

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
import com.mjs.core.domain.repository.IVirtualClassRepository
import kotlinx.coroutines.flow.Flow

class VirtualClassInteractor(
    private val virtualClassRepository: IVirtualClassRepository,
) : VirtualClassUseCase {
    override fun getThemeSetting(): Flow<Boolean> = virtualClassRepository.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        virtualClassRepository.saveThemeSetting(isDarkModeActive)
    }

    override fun getMahasiswaByNim(nim: Int): Flow<Resource<Mahasiswa>> = virtualClassRepository.getMahasiswaByNim(nim) // Changed to Int

    override fun getDosenByNidn(nidn: Int): Flow<Resource<Dosen>> = virtualClassRepository.getDosenByNidn(nidn) // Changed to Int

    override suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity): Flow<Resource<String>> =
        virtualClassRepository.registerMahasiswa(mahasiswa)

    override fun getAllKelas(): Flow<Resource<List<Kelas>>> = virtualClassRepository.getAllKelas()

    override fun getEnrolledClasses(nim: Int): Flow<Resource<List<EnrollmentEntity>>> = virtualClassRepository.getEnrolledClasses(nim)

    override suspend fun enrollToClass(enrollment: EnrollmentEntity): Flow<Resource<String>> =
        virtualClassRepository.enrollToClass(enrollment)

    override fun getMaterialsByClass(kelasId: String): Flow<Resource<List<Materi>>> = virtualClassRepository.getMaterialsByClass(kelasId)

    override fun getAssignmentsByClass(kelasId: String): Flow<Resource<List<Tugas>>> = virtualClassRepository.getAssignmentsByClass(kelasId)

    override suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>> =
        virtualClassRepository.insertAssignment(assignment)

    override suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>> =
        virtualClassRepository.insertSubmission(submission)

    override fun getForumsByClass(kelasId: String): Flow<Resource<List<Forum>>> = virtualClassRepository.getForumsByClass(kelasId)

    override fun getPostsByForum(forumId: Int): Flow<Resource<List<Postingan>>> = virtualClassRepository.getPostsByForum(forumId)

    override suspend fun insertPost(post: PostEntity): Flow<Resource<String>> = virtualClassRepository.insertPost(post)

    override fun getAttendanceHistory(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Kehadiran>>> = virtualClassRepository.getAttendanceHistory(nim, kelasId)

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<String>> =
        virtualClassRepository.insertAttendance(attendance)

    override fun getTodaySchedule(nim: Int): Flow<Resource<List<Kelas>>> = virtualClassRepository.getTodaySchedule(nim) // Changed to Int

    override fun getTodayScheduleDosen(nidn: Int): Flow<Resource<List<Kelas>>> = virtualClassRepository.getTodayScheduleDosen(nidn)

    override fun getAllSchedulesByNim(nim: Int): Flow<Resource<List<Kelas>>> = virtualClassRepository.getAllSchedulesByNim(nim)

    override fun getAllSchedulesByNidn(nidn: Int): Flow<Resource<List<Kelas>>> = virtualClassRepository.getAllSchedulesByNidn(nidn)

    override fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> = virtualClassRepository.getNotFinishedTasks(nim, kelasId)

    override fun getLateTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> = virtualClassRepository.getLateTasks(nim, kelasId)
}
