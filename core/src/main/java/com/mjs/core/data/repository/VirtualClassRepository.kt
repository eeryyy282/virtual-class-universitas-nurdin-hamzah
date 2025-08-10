package com.mjs.core.data.repository

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.LocalDataSource
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.KelasEntity
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
import com.mjs.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    override fun getThemeSetting(): Flow<Boolean> = localDataSource.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) = localDataSource.saveThemeSetting(isDarkModeActive)

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
        flow {
            emit(Resource.Loading())
            localDataSource.getDosenByNidn(nidn).collect { entity ->
                if (entity != null) {
                    emit(Resource.Success(DataMapper.mapDosenEntityToDomain(entity)))
                } else {
                    emit(Resource.Error("Dosen dengan NIDN $nidn tidak ditemukan"))
                }
            }
        }

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

    override fun getEnrolledClasses(nim: Int): Flow<Resource<List<EnrollmentEntity>>> =
        flow {
            emit(Resource.Loading())
            localDataSource.getEnrolledClasses(nim).collect {
                emit(Resource.Success(it))
            }
        }

    override suspend fun enrollToClass(enrollment: EnrollmentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertEnrollment(enrollment)
                emit(Resource.Success("Permintaan pendaftaran terkirim!"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengirim permintaan"))
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
                emit(Resource.Success("Tugas berhasil dibuat!"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal membuat tugas"))
            }
        }

    override suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertSubmission(submission)
                emit(Resource.Success("Tugas berhasil dikumpulkan!"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengumpulkan tugas"))
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
                emit(Resource.Success("Pesan berhasil dikirim!"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengirim pesan"))
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

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertAttendance(attendance)
                emit(Resource.Success("Kehadiran berhasil dicatat!"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mencatat kehadiran"))
            }
        }

    override fun getAttendanceStreak(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.getAttendanceStreak(nim, kelasId).collect { streakEntity ->
                    if (streakEntity != null) {
                        emit(Resource.Success(streakEntity.currentStreak))
                    } else {
                        emit(Resource.Success(0))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil data streak kehadiran"))
            }
        }

    override fun getTodaySchedule(nim: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val calendar = Calendar.getInstance()
                val currentDayName =
                    SimpleDateFormat("EEEE", Locale("id", "ID")).format(calendar.time)

                val enrolledClasses = localDataSource.getEnrolledClasses(nim).first()
                if (enrolledClasses.isNotEmpty()) {
                    val todayClasses =
                        enrolledClasses.mapNotNull { enrollment ->
                            val kelasEntity =
                                localDataSource.getKelasById(enrollment.kelasId).first()
                            kelasEntity?.takeIf {
                                it.jadwal.contains(
                                    currentDayName,
                                    ignoreCase = true,
                                )
                            }
                        }
                    emit(Resource.Success(DataMapper.mapKelasEntitiesToDomains(todayClasses)))
                } else {
                    emit(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil jadwal hari ini"))
            }
        }

    override fun getTodayScheduleDosen(nidn: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val calendar = Calendar.getInstance()
                val currentDayName =
                    SimpleDateFormat("EEEE", Locale("id", "ID")).format(calendar.time)

                val allClasses = localDataSource.getAllKelas().first()
                if (allClasses.isNotEmpty()) {
                    val todayClasses =
                        allClasses.filter { kelasEntity ->
                            kelasEntity.nidn == nidn &&
                                kelasEntity.jadwal.contains(
                                    currentDayName,
                                    ignoreCase = true,
                                )
                        }
                    emit(Resource.Success(DataMapper.mapKelasEntitiesToDomains(todayClasses)))
                } else {
                    emit(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil jadwal dosen hari ini"))
            }
        }

    override fun getAllSchedulesByNim(nim: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val enrolledClasses = localDataSource.getEnrolledClasses(nim).first()
                if (enrolledClasses.isNotEmpty()) {
                    val allStudentSchedules =
                        mutableListOf<KelasEntity>()
                    for (enrollment in enrolledClasses) {
                        localDataSource
                            .getKelasById(enrollment.kelasId)
                            .first()
                            ?.let { kelasEntity ->
                                allStudentSchedules.add(kelasEntity)
                            }
                    }
                    emit(Resource.Success(DataMapper.mapKelasEntitiesToDomains(allStudentSchedules)))
                } else {
                    emit(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil semua jadwal mahasiswa"))
            }
        }

    override fun getAllSchedulesByNidn(nidn: Int): Flow<Resource<List<Kelas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val allClasses = localDataSource.getAllKelas().first()
                if (allClasses.isNotEmpty()) {
                    val dosenSchedules = allClasses.filter { it.nidn == nidn }
                    emit(Resource.Success(DataMapper.mapKelasEntitiesToDomains(dosenSchedules)))
                } else {
                    emit(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil semua jadwal dosen"))
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
                emit(Resource.Error(e.message ?: "Gagal mengambil tugas yang belum selesai"))
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
                emit(Resource.Error(e.message ?: "Gagal mengambil tugas yang terlambat"))
            }
        }

    override fun getActiveAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val allClasses = localDataSource.getAllKelas().first()
                val dosenKelasIds = allClasses.filter { it.nidn == nidn }.map { it.kelasId }
                if (dosenKelasIds.isNotEmpty()) {
                    val currentDate = getCurrentFormattedDate()
                    localDataSource
                        .getAssignmentsByKelasIdsAndFutureDeadline(
                            dosenKelasIds,
                            currentDate,
                        ).map { DataMapper.mapTugasEntitiesToDomains(it) }
                        .collect { emit(Resource.Success(it)) }
                } else {
                    emit(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil tugas aktif untuk dosen"))
            }
        }

    override fun getPastDeadlineAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            try {
                val allClasses = localDataSource.getAllKelas().first()
                val dosenKelasIds = allClasses.filter { it.nidn == nidn }.map { it.kelasId }
                if (dosenKelasIds.isNotEmpty()) {
                    val currentDate = getCurrentFormattedDate()
                    localDataSource
                        .getAssignmentsByKelasIdsAndPastDeadline(
                            dosenKelasIds,
                            currentDate,
                        ).map { DataMapper.mapTugasEntitiesToDomains(it) }
                        .collect { emit(Resource.Success(it)) }
                } else {
                    emit(Resource.Success(emptyList()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil tugas lewat tenggat untuk dosen"))
            }
        }
}
