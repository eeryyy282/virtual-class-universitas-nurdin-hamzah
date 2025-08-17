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
                    .getAllKelas()
                    .map { kelasEntities ->
                        val domainKelasList = DataMapper.mapKelasEntitiesToDomains(kelasEntities)
                        domainKelasList.filter { kelas ->
                            kelas.jurusan.equals(jurusan, ignoreCase = true)
                        }
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
                            localDataSource.insertEnrollment(enrollment)
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
                emit(Resource.Success("Jawaban berhasil dikirim"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengirim jawaban"))
            }
        }

    override fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val currentDate = getCurrentFormattedDate()
                localDataSource
                    .getNotFinishedTasks(nim, kelasId, currentDate)
                    .map { DataMapper.mapTugasEntitiesToDomains(it) }
                    .collect { emit(Resource.Success(it)) }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memuat tugas belum selesai"))
            }
        }

    override fun getLateTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val currentDate = getCurrentFormattedDate()
                localDataSource
                    .getLateTasks(nim, kelasId, currentDate)
                    .map { DataMapper.mapTugasEntitiesToDomains(it) }
                    .collect { emit(Resource.Success(it)) }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memuat tugas terlambat"))
            }
        }

    override fun getActiveAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val dosen = localDataSource.getDosenByNidn(nidn).first()
                if (dosen == null) {
                    emit(Resource.Error("Dosen tidak ditemukan"))
                    return@flow
                }
                val kelasIds =
                    localDataSource
                        .getAllKelas()
                        .first()
                        .filter { it.nidn == nidn }
                        .map { it.kelasId }

                if (kelasIds.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                    return@flow
                }

                val currentDate = getCurrentFormattedDate()
                localDataSource
                    .getAssignmentsByKelasIdsAndFutureDeadline(kelasIds, currentDate)
                    .map { DataMapper.mapTugasEntitiesToDomains(it) }
                    .collect { emit(Resource.Success(it)) }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memuat tugas aktif"))
            }
        }

    override fun getPastDeadlineAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val dosen = localDataSource.getDosenByNidn(nidn).first()
                if (dosen == null) {
                    emit(Resource.Error("Dosen tidak ditemukan"))
                    return@flow
                }
                val kelasIds =
                    localDataSource
                        .getAllKelas()
                        .first()
                        .filter { it.nidn == nidn }
                        .map { it.kelasId }

                if (kelasIds.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                    return@flow
                }

                val currentDate = getCurrentFormattedDate()
                localDataSource
                    .getAssignmentsByKelasIdsAndPastDeadline(kelasIds, currentDate)
                    .map { DataMapper.mapTugasEntitiesToDomains(it) }
                    .collect { emit(Resource.Success(it)) }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memuat tugas lewat tenggat"))
            }
        }

    override fun getForumsByClass(kelasId: String): Flow<Resource<List<Forum>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getForumsByClass(kelasId)
                .map { DataMapper.mapForumEntitiesToDomains(it) }
                .collect { emit(Resource.Success(it)) }
        }

    override fun getPostsByForum(forumId: Int): Flow<Resource<List<Postingan>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getPostsByForum(forumId)
                .map { DataMapper.mapPostinganEntitiesToDomains(it) }
                .collect { emit(Resource.Success(it)) }
        }

    override suspend fun insertPost(post: PostEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertPost(post)
                emit(Resource.Success("Postingan berhasil dikirim"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengirim postingan"))
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
                .map { DataMapper.mapKehadiranEntitiesToDomains(it) }
                .collect { emit(Resource.Success(it)) }
        }

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertAttendance(attendance)

                var streakEntity: com.mjs.core.data.source.local.entity.AttendanceStreakEntity? =
                    localDataSource.getAttendanceStreak(attendance.nim, attendance.kelasId).first()

                val currentAttendanceCal =
                    Calendar.getInstance().apply {
                        time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                            attendance.tanggalHadir,
                        ) ?: Date()
                    }

                if (streakEntity == null) {
                    streakEntity =
                        com.mjs.core.data.source.local.entity.AttendanceStreakEntity(
                            streakId = 0,
                            nim = attendance.nim,
                            kelasId = attendance.kelasId,
                            currentStreak = 1,
                            longestStreak = 1,
                            lastAttendedDate = attendance.tanggalHadir,
                        )
                } else {
                    val lastRecordedStreakCal =
                        Calendar.getInstance().apply {
                            time =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                                    streakEntity.lastAttendedDate,
                                ) ?: Date()
                        }

                    val dayAfterLastStreakCal =
                        Calendar.getInstance().apply {
                            time = lastRecordedStreakCal.time
                            add(Calendar.DAY_OF_YEAR, 1)
                        }

                    if (currentAttendanceCal.get(Calendar.YEAR) ==
                        dayAfterLastStreakCal.get(
                            Calendar.YEAR,
                        ) &&
                        currentAttendanceCal.get(Calendar.DAY_OF_YEAR) ==
                        dayAfterLastStreakCal.get(
                            Calendar.DAY_OF_YEAR,
                        )
                    ) {
                        val newCurrentStreak = streakEntity.currentStreak + 1
                        val newLongestStreak =
                            if (newCurrentStreak > streakEntity.longestStreak) newCurrentStreak else streakEntity.longestStreak
                        streakEntity =
                            streakEntity.copy(
                                currentStreak = newCurrentStreak,
                                longestStreak = newLongestStreak,
                                lastAttendedDate = attendance.tanggalHadir,
                            )
                    } else if (!(
                            currentAttendanceCal.get(Calendar.YEAR) ==
                                lastRecordedStreakCal.get(
                                    Calendar.YEAR,
                                ) &&
                                currentAttendanceCal.get(Calendar.DAY_OF_YEAR) ==
                                lastRecordedStreakCal.get(
                                    Calendar.DAY_OF_YEAR,
                                ) &&
                                streakEntity.lastAttendedDate == attendance.tanggalHadir
                        )
                    ) {
                        streakEntity =
                            streakEntity.copy(
                                currentStreak = 1,
                                lastAttendedDate = attendance.tanggalHadir,
                            )
                    }
                }

                localDataSource.updateAttendanceStreak(streakEntity)
                emit(Resource.Success(streakEntity.currentStreak))
            } catch (e: Exception) {
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mencatat kehadiran atau memperbarui streak",
                        -1,
                    ),
                )
            }
        }

    override fun getAttendanceStreak(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getAttendanceStreak(nim, kelasId).collect { streak ->
                if (streak != null) {
                    emit(Resource.Success(streak.currentStreak))
                } else {
                    emit(Resource.Success(0))
                }
            }
        }

    override fun getTodaySchedule(nim: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllSchedulesByNim(nim)
                .map { enrolledClasses ->
                    val today = Calendar.getInstance()
                    val dayOfWeekToday =
                        when (today.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> "Senin"
                            Calendar.TUESDAY -> "Selasa"
                            Calendar.WEDNESDAY -> "Rabu"
                            Calendar.THURSDAY -> "Kamis"
                            Calendar.FRIDAY -> "Jumat"
                            Calendar.SATURDAY -> "Sabtu"
                            Calendar.SUNDAY -> "Minggu"
                            else -> ""
                        }
                    DataMapper.mapKelasEntitiesToDomains(
                        enrolledClasses.filter {
                            it.jadwal.startsWith(
                                dayOfWeekToday,
                            )
                        },
                    )
                }.collect { emit(Resource.Success(it)) }
        }

    override fun getTodayScheduleDosen(nidn: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllKelas()
                .map { allClasses ->
                    val today = Calendar.getInstance()
                    val dayOfWeekToday =
                        when (today.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> "Senin"
                            Calendar.TUESDAY -> "Selasa"
                            Calendar.WEDNESDAY -> "Rabu"
                            Calendar.THURSDAY -> "Kamis"
                            Calendar.FRIDAY -> "Jumat"
                            Calendar.SATURDAY -> "Sabtu"
                            Calendar.SUNDAY -> "Minggu"
                            else -> ""
                        }
                    DataMapper.mapKelasEntitiesToDomains(
                        allClasses.filter {
                            it.nidn == nidn &&
                                it.jadwal.startsWith(
                                    dayOfWeekToday,
                                )
                        },
                    )
                }.collect { emit(Resource.Success(it)) }
        }

    override fun getAllSchedulesByNim(nim: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllSchedulesByNim(nim)
                .map { DataMapper.mapKelasEntitiesToDomains(it) }
                .collect { emit(Resource.Success(it)) }
        }

    override fun getAllSchedulesByNidn(nidn: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getAllKelas()
                .map { allClasses ->
                    DataMapper.mapKelasEntitiesToDomains(allClasses.filter { it.nidn == nidn })
                }.collect { emit(Resource.Success(it)) }
        }

    override fun getMahasiswaByKelasId(kelasId: String): Flow<Resource<List<Mahasiswa>>> =
        flow {
            emit(Resource.Loading())
            localDataSource
                .getMahasiswaByKelasId(kelasId)
                .map { DataMapper.mapMahasiswaEntitiesToDomains(it) }
                .collect { emit(Resource.Success(it)) }
        }

    override fun getMahasiswaCountByKelasId(kelasId: String): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getMahasiswaCountByKelasId(kelasId).collect {
                emit(Resource.Success(it))
            }
        }

    override suspend fun updateMahasiswaProfile(mahasiswa: Mahasiswa): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val mahasiswaEntity = localDataSource.getMahasiswaByNim(mahasiswa.nim).first()
                if (mahasiswaEntity != null) {
                    val updatedEntity =
                        mahasiswaEntity.copy(
                            nama = mahasiswa.nama,
                            email = mahasiswa.email,
                            fotoProfil = mahasiswa.fotoProfil,
                        )
                    localDataSource.updateMahasiswa(updatedEntity)
                    emit(Resource.Success("Profil berhasil diperbarui"))
                } else {
                    emit(Resource.Error("Mahasiswa tidak ditemukan"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memperbarui profil"))
            }
        }

    override suspend fun updateDosenProfile(dosen: Dosen): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val dosenEntity = localDataSource.getDosenByNidn(dosen.nidn).first()
                if (dosenEntity != null) {
                    val updatedEntity =
                        dosenEntity.copy(
                            nama = dosen.nama,
                            email = dosen.email,
                            fotoProfil = dosen.fotoProfil,
                        )
                    localDataSource.updateDosen(updatedEntity)
                    emit(Resource.Success("Profil berhasil diperbarui"))
                } else {
                    emit(Resource.Error("Dosen tidak ditemukan"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memperbarui profil"))
            }
        }

    override fun getPendingEnrollmentRequests(kelasId: String): Flow<Resource<List<Mahasiswa>>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource
                    .getPendingEnrollmentRequests(kelasId)
                    .map { DataMapper.mapMahasiswaEntitiesToDomains(it) }
                    .collect { emit(Resource.Success(it)) }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memuat permintaan pendaftaran"))
            }
        }

    override fun getPendingEnrollmentRequestCount(kelasId: String): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getPendingEnrollmentRequestCount(kelasId).collect {
                emit(Resource.Success(it))
            }
        }
}
