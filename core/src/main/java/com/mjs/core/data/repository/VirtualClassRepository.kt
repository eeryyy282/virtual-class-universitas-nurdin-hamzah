package com.mjs.core.data.repository

import com.mjs.core.data.Resource
import com.mjs.core.data.source.local.LocalDataSource
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
import com.mjs.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class VirtualClassRepository(
    private val localDataSource: LocalDataSource,
) : IVirtualClassRepository {
    override fun getThemeSetting(): Flow<Boolean> = localDataSource.getThemeSetting()

    override suspend fun saveThemeSetting(isDarkModeActive: Boolean) = localDataSource.saveThemeSetting(isDarkModeActive)

    override fun getMahasiswaByNim(nim: String): Flow<Resource<Mahasiswa>> =
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

    override fun getDosenByNidn(nidn: String): Flow<Resource<Dosen>> =
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

    override fun getEnrolledClasses(nim: String): Flow<Resource<List<EnrollmentEntity>>> =
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

    override fun getMaterialsByClass(kelasId: Int): Flow<Resource<List<Materi>>> =
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

    override fun getAssignmentsByClass(kelasId: Int): Flow<Resource<List<Tugas>>> =
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

    override fun getForumsByClass(kelasId: Int): Flow<Resource<List<Forum>>> =
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
        nim: String,
        kelasId: Int,
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
        nim: String,
        kelasId: Int,
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
}
