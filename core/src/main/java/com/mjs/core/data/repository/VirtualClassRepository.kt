package com.mjs.core.data.repository

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.pref.AppPreference
import com.mjs.core.domain.model.Dosen
import com.mjs.core.domain.model.Forum
import com.mjs.core.domain.model.Kehadiran
import com.mjs.core.domain.model.Kelas
import com.mjs.core.domain.model.Mahasiswa
import com.mjs.core.domain.model.Materi
import com.mjs.core.domain.model.Postingan
import com.mjs.core.domain.model.Tugas
import com.mjs.core.domain.repository.IVirtualClassRepository
import com.mjs.core.ui.task.SubmissionListItem
import com.mjs.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class VirtualClassRepository(
    private val localDataSource: LocalDataSource,
) : IVirtualClassRepository {
    private fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun getLoginStatus(): Flow<Boolean> = localDataSource.getLoginStatus()

    override suspend fun saveLoginSession(
        userId: Int,
        userType: String,
    ) {
        localDataSource.saveLoginSession(userId, userType)
    }

    override suspend fun clearLoginSession() {
        localDataSource.clearLoginSession()
    }

    override fun loginDosen(
        nidn: String,
        password: String,
    ): Flow<Resource<Dosen>> =
        flow {
            emit(Resource.Loading())
            try {
                val nidnInt = nidn.toIntOrNull()
                if (nidnInt == null) {
                    emit(Resource.Error("Format NIDN tidak valid."))
                    return@flow
                }

                val dosenEntity = localDataSource.getDosenByNidn(nidnInt).first()
                if (dosenEntity != null) {
                    if (dosenEntity.password == password) {
                        saveLoginSession(dosenEntity.nidn, AppPreference.USER_TYPE_DOSEN)
                        emit(Resource.Success(DataMapper.mapDosenEntityToDomain(dosenEntity)))
                    } else {
                        emit(Resource.Error("Password salah."))
                    }
                } else {
                    emit(Resource.Error("Dosen dengan NIDN $nidn tidak ditemukan."))
                }
            } catch (_: NumberFormatException) {
                emit(Resource.Error("Format NIDN tidak valid."))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Terjadi kesalahan saat login dosen."))
            }
        }

    override fun loginMahasiswa(
        nim: String,
        password: String,
    ): Flow<Resource<Mahasiswa>> =
        flow {
            emit(Resource.Loading())
            try {
                val nimInt = nim.toIntOrNull()
                if (nimInt == null) {
                    emit(Resource.Error("Format NIM tidak valid."))
                    return@flow
                }

                val mahasiswaEntity = localDataSource.getMahasiswaByNim(nimInt).first()
                if (mahasiswaEntity != null) {
                    if (mahasiswaEntity.password == password) {
                        saveLoginSession(mahasiswaEntity.nim, AppPreference.USER_TYPE_MAHASISWA)
                        emit(Resource.Success(DataMapper.mapMahasiswaEntityToDomain(mahasiswaEntity)))
                    } else {
                        emit(Resource.Error("Password salah."))
                    }
                } else {
                    emit(Resource.Error("Mahasiswa dengan NIM $nim tidak ditemukan."))
                }
            } catch (_: NumberFormatException) {
                emit(Resource.Error("Format NIM tidak valid."))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Terjadi kesalahan saat login mahasiswa."))
            }
        }

    override fun getThemeSetting(): Flow<Boolean> = localDataSource.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) = localDataSource.saveThemeSetting(isDarkModeActive)

    override fun getLoggedInUserId(): Flow<Int?> = localDataSource.getLoggedInUserId()

    override fun getLoggedInUserType(): Flow<String?> = localDataSource.getLoggedInUserType()

    override fun getMahasiswaByNim(nim: Int): Flow<Resource<Mahasiswa>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getMahasiswaByNim(nim).collect { entity ->
                if (entity != null) {
                    emit(Resource.Success(DataMapper.mapMahasiswaEntityToDomain(entity)))
                } else {
                    emit(Resource.Error("Mahasiswa dengan NIM $nim tidak ditemukan"))
                }
            }
        }

    override fun getDosenByNidn(nidn: Int): Flow<Resource<Dosen>> =
        localDataSource
            .getDosenByNidn(nidn)
            .take(1)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapDosenEntityToDomain(entity))
                } else {
                    Resource.Error("Dosen dengan NIDN $nidn tidak ditemukan (data tidak ada)")
                }
            }.onEmpty {
                emit(Resource.Error("Dosen dengan NIDN $nidn tidak ditemukan (sumber data kosong)"))
            }.catch { e ->
                emit(Resource.Error("Kesalahan mengambil dosen NIDN $nidn: ${e.message}"))
            }.onStart { emit(Resource.Loading()) }

    override suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val existingUser = localDataSource.getMahasiswaByNim(mahasiswa.nim).first()
                if (existingUser != null) {
                    emit(Resource.Error("NIM sudah terdaftar!"))
                } else {
                    localDataSource.registerMahasiswa(mahasiswa)
                    emit(Resource.Success("Pendaftaran berhasil! Silakan login."))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Terjadi kesalahan saat pendaftaran"))
            }
        }

    override fun getAllKelas(): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllKelas()
                .map {
                    DataMapper.mapKelasEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getAllKelasByJurusan(jurusan: String): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource
                    .getAllKelasByJurusan(jurusan)
                    .map { kelasEntities ->
                        DataMapper.mapKelasEntitiesToDomains(kelasEntities)
                    }.collect { filteredKelasList ->
                        emit(Resource.Success(filteredKelasList))
                    }
            } catch (e: Exception) {
                emit(
                    Resource.Error(
                        e.message ?: "Terjadi kesalahan saat mengambil kelas berdasarkan jurusan.",
                    ),
                )
            }
        }

    override fun getKelasById(kelasId: String): Flow<Resource<Kelas>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getKelasById(kelasId).collect { entity ->
                if (entity != null) {
                    emit(Resource.Success(DataMapper.mapKelasEntityToDomain(entity)))
                } else {
                    emit(Resource.Error("Kelas dengan ID $kelasId tidak ditemukan"))
                }
            }
        }

    override fun getEnrolledClasses(nim: Int): Flow<Resource<List<EnrollmentEntity>>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getEnrolledClasses(nim).collect {
                emit(Resource.Success(it))
            }
        }

    override fun getEnrollmentByNimAndKelasId(
        nim: Int,
        kelasId: String,
    ): Flow<EnrollmentEntity?> = localDataSource.getEnrollmentByNimAndKelasId(nim, kelasId)

    override suspend fun enrollToClass(enrollment: EnrollmentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val existingEnrollment =
                    localDataSource
                        .getEnrollmentByNimAndKelasId(enrollment.nim, enrollment.kelasId)
                        .first()
                if (existingEnrollment != null) {
                    when (existingEnrollment.status) {
                        "approved" -> emit(Resource.Error("Anda sudah terdaftar di kelas ini."))
                        "pending" -> emit(Resource.Error("Permintaan pendaftaran untuk kelas ini masih pending."))
                        else -> {
                            localDataSource.insertEnrollment(enrollment.copy(enrollId = existingEnrollment.enrollId))
                            emit(Resource.Success("Permintaan pendaftaran terkirim!"))
                        }
                    }
                } else {
                    localDataSource.insertEnrollment(enrollment)
                    emit(Resource.Success("Permintaan pendaftaran terkirim!"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengirim permintaan"))
            }
        }

    override suspend fun leaveClass(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.deleteEnrollment(nim, kelasId)
                emit(Resource.Success("Berhasil keluar dari kelas"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal keluar dari kelas"))
            }
        }

    override suspend fun updateEnrollmentStatus(
        nim: Int,
        kelasId: String,
        newStatus: String,
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.updateEnrollmentStatus(nim, kelasId, newStatus)
                val message =
                    if (newStatus == "approved") "Mahasiswa berhasil diterima" else "Mahasiswa berhasil ditolak"
                emit(Resource.Success(message))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memperbarui status pendaftaran"))
            }
        }

    override fun getMaterialsByClass(kelasId: String): Flow<Resource<List<Materi>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getMaterialsByClass(kelasId)
                .map {
                    DataMapper.mapMateriEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getAssignmentsByClass(kelasId: String): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAssignmentsByClass(kelasId)
                .map {
                    DataMapper.mapTugasEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertAssignment(assignment)
                emit(Resource.Success("Tugas berhasil ditambahkan"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal menambahkan tugas"))
            }
        }

    override suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertSubmission(submission)
                emit(Resource.Success("Tugas berhasil dikumpulkan"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengumpulkan tugas"))
            }
        }

    override fun getSubmissionListItemsByAssignment(assignmentId: Int): Flow<Resource<List<SubmissionListItem>>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.getSubmissionsByAssignment(assignmentId).collect { submissions ->
                    val submissionListItems =
                        submissions.map { submissionEntity ->
                            val mahasiswa =
                                localDataSource.getMahasiswaByNim(submissionEntity.nim).first()
                            SubmissionListItem(
                                submissionEntity = submissionEntity,
                                studentName = mahasiswa?.nama,
                                studentPhotoUrl = mahasiswa?.fotoProfil,
                            )
                        }
                    emit(Resource.Success(submissionListItems))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil daftar submission"))
            }
        }

    override fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            val currentDate = getCurrentFormattedDate()
            localDataSource
                .getNotFinishedTasks(nim, kelasId, currentDate)
                .map {
                    DataMapper.mapTugasEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getLateTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            val currentDate = getCurrentFormattedDate()
            localDataSource
                .getLateTasks(nim, kelasId, currentDate)
                .map {
                    DataMapper.mapTugasEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getActiveAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            val currentDate = getCurrentFormattedDate()
            localDataSource
                .getAllKelas()
                .first()
                .filter { it.nidn == nidn }
                .map { it.kelasId }
                .let { kelasIds ->
                    if (kelasIds.isEmpty()) {
                        emit(Resource.Success(emptyList()))
                        return@flow
                    }
                    localDataSource
                        .getAssignmentsByKelasIdsAndFutureDeadline(kelasIds, currentDate)
                        .map {
                            DataMapper.mapTugasEntitiesToDomains(it)
                        }.collect {
                            emit(Resource.Success(it))
                        }
                }
        }

    override fun getPastDeadlineAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            val currentDate = getCurrentFormattedDate()
            localDataSource
                .getAllKelas()
                .first()
                .filter { it.nidn == nidn }
                .map { it.kelasId }
                .let { kelasIds ->
                    if (kelasIds.isEmpty()) {
                        emit(Resource.Success(emptyList()))
                        return@flow
                    }
                    localDataSource
                        .getAssignmentsByKelasIdsAndPastDeadline(kelasIds, currentDate)
                        .map {
                            DataMapper.mapTugasEntitiesToDomains(it)
                        }.collect {
                            emit(Resource.Success(it))
                        }
                }
        }

    override fun getForumsByClass(kelasId: String): Flow<Resource<List<Forum>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getForumsByClass(kelasId)
                .map {
                    DataMapper.mapForumEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getPostsByForum(forumId: Int): Flow<Resource<List<Postingan>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getPostsByForum(forumId)
                .map {
                    DataMapper.mapPostinganEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override suspend fun insertPost(post: PostEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertPost(post)
                emit(Resource.Success("Postingan berhasil ditambahkan"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal menambahkan post"))
            }
        }

    override fun getAttendanceHistory(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Kehadiran>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAttendanceHistory(nim, kelasId)
                .map {
                    DataMapper.mapKehadiranEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertAttendance(attendance)
                emit(Resource.Success(attendance.absensiId))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mencatat kehadiran"))
            }
        }

    override fun getTodaySchedule(nim: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val currentDayName = DataMapper.mapDayOfWeekToDayName(dayOfWeek)

            localDataSource
                .getAllSchedulesByNim(nim)
                .map { kelasEntities ->
                    DataMapper
                        .mapKelasEntitiesToDomains(kelasEntities)
                        .filter { kelas ->
                            kelas.jadwal
                                .split(',')
                                .any { it.trim().equals(currentDayName, ignoreCase = true) }
                        }
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getTodayScheduleDosen(nidn: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val currentDayName = DataMapper.mapDayOfWeekToDayName(dayOfWeek)

            localDataSource
                .getAllKelas()
                .map { kelasEntities ->
                    kelasEntities
                        .filter { it.nidn == nidn }
                        .map { DataMapper.mapKelasEntityToDomain(it) }
                        .filter { kelas ->
                            kelas.jadwal
                                .split(',')
                                .any { it.trim().equals(currentDayName, ignoreCase = true) }
                        }
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getAllSchedulesByNim(nim: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllSchedulesByNim(nim)
                .map {
                    DataMapper.mapKelasEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getAllSchedulesByNidn(nidn: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllKelas()
                .map { kelasEntities ->
                    DataMapper.mapKelasEntitiesToDomains(kelasEntities.filter { it.nidn == nidn })
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getAttendanceStreak(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAttendanceStreak(nim, kelasId)
                .map { streakEntity ->
                    Resource.Success(streakEntity?.currentStreak ?: 0)
                }.catch { e ->
                    emit(Resource.Error(e.message ?: "Gagal mendapatkan streak kehadiran"))
                }.collect {
                    emit(it)
                }
        }

    override suspend fun updateMahasiswaProfile(mahasiswa: Mahasiswa): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val mahasiswaEntity = DataMapper.mapMahasiswaDomainToEntity(mahasiswa)
                val currentEntity = localDataSource.getMahasiswaByNim(mahasiswa.nim).first()
                if (currentEntity != null) {
                    val updatedEntity = mahasiswaEntity.copy(password = currentEntity.password)
                    localDataSource.updateMahasiswa(updatedEntity)
                    emit(Resource.Success("Profil berhasil diperbarui"))
                } else {
                    emit(Resource.Error("Mahasiswa tidak ditemukan untuk pembaruan"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memperbarui profil"))
            }
        }

    override suspend fun updateDosenProfile(dosen: Dosen): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val dosenEntity = DataMapper.mapDosenDomainToEntity(dosen)
                val currentEntity = localDataSource.getDosenByNidn(dosen.nidn).first()
                if (currentEntity != null) {
                    val updatedEntity = dosenEntity.copy(password = currentEntity.password)
                    localDataSource.updateDosen(updatedEntity)
                    emit(Resource.Success("Profil berhasil diperbarui"))
                } else {
                    emit(Resource.Error("Dosen tidak ditemukan untuk pembaruan"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memperbarui profil"))
            }
        }

    override fun getMahasiswaByKelasId(kelasId: String): Flow<Resource<List<Mahasiswa>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getMahasiswaByKelasId(kelasId)
                .map {
                    DataMapper.mapMahasiswaEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getMahasiswaCountByKelasId(kelasId: String): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getMahasiswaCountByKelasId(kelasId).collect {
                emit(Resource.Success(it))
            }
        }

    override fun getPendingEnrollmentRequests(kelasId: String): Flow<Resource<List<Mahasiswa>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getPendingEnrollmentRequests(kelasId)
                .map {
                    DataMapper.mapMahasiswaEntitiesToDomains(it)
                }.collect {
                    emit(Resource.Success(it))
                }
        }

    override fun getPendingEnrollmentRequestCount(kelasId: String): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getPendingEnrollmentRequestCount(kelasId).collect {
                emit(Resource.Success(it))
            }
        }

    override fun getPendingEnrollmentRequestCountForDosen(nidn: Int): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                var totalPendingRequests = 0
                val allClasses = localDataSource.getAllKelas().first()
                val dosenClasses = allClasses.filter { it.nidn == nidn }

                for (kelas in dosenClasses) {
                    val pendingCountForClass =
                        localDataSource.getPendingEnrollmentRequestCount(kelas.kelasId).first()
                    totalPendingRequests += pendingCountForClass
                }
                emit(Resource.Success(totalPendingRequests))
            } catch (e: Exception) {
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil jumlah permintaan pendaftaran untuk dosen",
                    ),
                )
            }
        }

    override fun getLastNotifiedPendingCount(): Flow<Int> = localDataSource.getLastNotifiedPendingCount()

    override suspend fun saveLastNotifiedPendingCount(count: Int) = localDataSource.saveLastNotifiedPendingCount(count)
}
