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
import kotlinx.coroutines.flow.onStart
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
        localDataSource
            .getMahasiswaByNim(nim)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapMahasiswaEntityToDomain(entity))
                } else {
                    Resource.Error("Mahasiswa dengan NIM $nim tidak ditemukan")
                }
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error("Kesalahan mengambil mahasiswa NIM $nim: ${e.message}")) }

    override fun getDosenByNidn(nidn: Int): Flow<Resource<Dosen>> =
        localDataSource
            .getDosenByNidn(nidn)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapDosenEntityToDomain(entity))
                } else {
                    Resource.Error("Dosen dengan NIDN $nidn tidak ditemukan")
                }
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(Resource.Error("Kesalahan mengambil dosen NIDN $nidn: ${e.message}"))
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
        localDataSource
            .getAllKelas()
            .map<List<com.mjs.core.data.source.local.entity.KelasEntity>, Resource<List<Kelas>>> { kelasEntities ->
                Resource.Success(DataMapper.mapKelasEntitiesToDomains(kelasEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil semua kelas")) }

    override fun getAllKelasByJurusan(jurusan: String): Flow<Resource<List<Kelas>>> =
        localDataSource
            .getAllKelasByJurusan(jurusan)
            .map<List<com.mjs.core.data.source.local.entity.KelasEntity>, Resource<List<Kelas>>> { kelasEntities ->
                Resource.Success(DataMapper.mapKelasEntitiesToDomains(kelasEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil kelas berdasarkan jurusan",
                    ),
                )
            }

    override fun getKelasById(kelasId: String): Flow<Resource<Kelas>> =
        localDataSource
            .getKelasById(kelasId)
            .map { entity ->
                if (entity != null) {
                    Resource.Success(DataMapper.mapKelasEntityToDomain(entity))
                } else {
                    Resource.Error("Kelas dengan ID $kelasId tidak ditemukan")
                }
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil kelas berdasarkan ID",
                    ),
                )
            }

    override fun getEnrolledClasses(nim: Int): Flow<Resource<List<EnrollmentEntity>>> =
        localDataSource
            .getEnrolledClasses(nim)
            .map<List<EnrollmentEntity>, Resource<List<EnrollmentEntity>>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil kelas yang diikuti")) }

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
                            localDataSource.insertEnrollment(
                                enrollment.copy(
                                    enrollId = existingEnrollment.enrollId,
                                    status = "pending",
                                    tanggalDaftar = getCurrentFormattedDate(),
                                ),
                            )
                            emit(Resource.Success("Permintaan pendaftaran terkirim!"))
                        }
                    }
                } else {
                    localDataSource.insertEnrollment(
                        enrollment.copy(
                            status = "pending",
                            tanggalDaftar = getCurrentFormattedDate(),
                        ),
                    )
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
        localDataSource
            .getMaterialsByClass(kelasId)
            .map<List<com.mjs.core.data.source.local.entity.MaterialEntity>, Resource<List<Materi>>> { materialEntities ->
                Resource.Success(DataMapper.mapMateriEntitiesToDomains(materialEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil materi kelas")) }

    override fun getAssignmentsByClass(kelasId: String): Flow<Resource<List<Tugas>>> =
        localDataSource
            .getAssignmentsByClass(kelasId)
            .map<List<AssignmentEntity>, Resource<List<Tugas>>> { assignmentEntities ->
                Resource.Success(DataMapper.mapTugasEntitiesToDomains(assignmentEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil tugas kelas")) }

    override fun getAssignmentById(assignmentId: Int): Flow<Resource<Tugas?>> =
        localDataSource
            .getAssignmentById(assignmentId)
            .map<AssignmentEntity?, Resource<Tugas?>> { entity ->
                Resource.Success(entity?.let { DataMapper.mapTugasEntityToDomain(it) })
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil tugas berdasarkan ID",
                    ),
                )
            }

    override suspend fun insertAssignment(assignment: AssignmentEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertAssignment(assignment.copy(tanggalMulai = getCurrentFormattedDate()))
                emit(Resource.Success("Tugas berhasil ditambahkan"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal menambahkan tugas"))
            }
        }

    override suspend fun updateTask(tugas: Tugas): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val assignmentEntity = DataMapper.mapTugasDomainToEntity(tugas)
                localDataSource.updateTask(assignmentEntity)
                emit(Resource.Success("Tugas berhasil diperbarui"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal memperbarui tugas"))
            }
        }

    override suspend fun deleteAssignmentById(assignmentId: Int): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.deleteAssignmentById(assignmentId)
                emit(Resource.Success("Tugas berhasil dihapus"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal menghapus tugas"))
            }
        }

    override suspend fun insertSubmission(submission: SubmissionEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertSubmission(submission.copy(submissionDate = getCurrentFormattedDate()))
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
                        submissions.mapNotNull { submissionEntity ->
                            val mahasiswaEntity =
                                localDataSource.getMahasiswaByNim(submissionEntity.nim).first()
                            mahasiswaEntity?.let {
                                SubmissionListItem(
                                    submissionEntity = submissionEntity,
                                    studentName = it.nama,
                                    studentPhotoUrl = it.fotoProfil,
                                )
                            }
                        }
                    emit(Resource.Success(submissionListItems))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil daftar submission"))
            }
        }

    override fun getSubmissionDetailById(submissionId: Int): Flow<Resource<SubmissionListItem?>> =
        flow {
            emit(Resource.Loading())
            try {
                val submissionEntity = localDataSource.getSubmissionById(submissionId).first()
                if (submissionEntity != null) {
                    val mahasiswaEntity =
                        localDataSource.getMahasiswaByNim(submissionEntity.nim).first()
                    val submissionListItem =
                        mahasiswaEntity?.let {
                            SubmissionListItem(
                                submissionEntity = submissionEntity,
                                studentName = it.nama,
                                studentPhotoUrl = it.fotoProfil,
                            )
                        }
                    emit(Resource.Success(submissionListItem))
                } else {
                    emit(Resource.Success(null))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal mengambil detail submission"))
            }
        }

    override suspend fun updateSubmissionGradeAndNote(
        submissionId: Int,
        grade: Int?,
        note: String?,
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.updateSubmissionGradeAndNote(submissionId, grade, note)
                emit(Resource.Success("Nilai dan catatan berhasil disimpan"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal menyimpan nilai dan catatan"))
            }
        }

    override fun getNotFinishedTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> =
        localDataSource
            .getNotFinishedTasks(nim, kelasId, getCurrentFormattedDate())
            .map<List<AssignmentEntity>, Resource<List<Tugas>>> { assignmentEntities ->
                Resource.Success(DataMapper.mapTugasEntitiesToDomains(assignmentEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil tugas yang belum selesai",
                    ),
                )
            }

    override fun getLateTasks(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Tugas>>> =
        localDataSource
            .getLateTasks(nim, kelasId, getCurrentFormattedDate())
            .map<List<AssignmentEntity>, Resource<List<Tugas>>> { assignmentEntities ->
                Resource.Success(DataMapper.mapTugasEntitiesToDomains(assignmentEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil tugas yang terlambat",
                    ),
                )
            }

    override fun getActiveAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            val currentDate = getCurrentFormattedDate()
            localDataSource.getAllKelas().collect { allKelas ->
                val kelasIds = allKelas.filter { it.nidn == nidn }.map { it.kelasId }
                if (kelasIds.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                } else {
                    localDataSource
                        .getAssignmentsByKelasIdsAndFutureDeadline(kelasIds, currentDate)
                        .map { DataMapper.mapTugasEntitiesToDomains(it) }
                        .collect { emit(Resource.Success(it)) }
                }
            }
        }.catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil tugas aktif dosen")) }

    override fun getPastDeadlineAssignmentsForDosen(nidn: Int): Flow<Resource<List<Tugas>>> =
        flow {
            emit(Resource.Loading())
            val currentDate = getCurrentFormattedDate()
            localDataSource.getAllKelas().collect { allKelas ->
                val kelasIds = allKelas.filter { it.nidn == nidn }.map { it.kelasId }
                if (kelasIds.isEmpty()) {
                    emit(Resource.Success(emptyList()))
                } else {
                    localDataSource
                        .getAssignmentsByKelasIdsAndPastDeadline(kelasIds, currentDate)
                        .map { DataMapper.mapTugasEntitiesToDomains(it) }
                        .collect { emit(Resource.Success(it)) }
                }
            }
        }.catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil tugas lampau dosen")) }

    override fun getForumsByClass(kelasId: String): Flow<Resource<List<Forum>>> =
        localDataSource
            .getForumsByClass(kelasId)
            .map<List<com.mjs.core.data.source.local.entity.ForumEntity>, Resource<List<Forum>>> { forumEntities ->
                Resource.Success(DataMapper.mapForumEntitiesToDomains(forumEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil forum kelas")) }

    override fun getPostsByForum(forumId: Int): Flow<Resource<List<Postingan>>> =
        localDataSource
            .getPostsByForum(forumId)
            .map<List<PostEntity>, Resource<List<Postingan>>> { postEntities ->
                Resource.Success(DataMapper.mapPostinganEntitiesToDomains(postEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil postingan forum")) }

    override suspend fun insertPost(post: PostEntity): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.insertPost(post.copy(tanggalPost = getCurrentFormattedDate()))
                emit(Resource.Success("Postingan berhasil ditambahkan"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Gagal menambahkan post"))
            }
        }

    override fun getAttendanceHistory(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<List<Kehadiran>>> =
        localDataSource
            .getAttendanceHistory(nim, kelasId)
            .map<List<AttendanceEntity>, Resource<List<Kehadiran>>> { attendanceEntities ->
                Resource.Success(DataMapper.mapKehadiranEntitiesToDomains(attendanceEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil riwayat kehadiran")) }

    override suspend fun insertAttendance(attendance: AttendanceEntity): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                val attendanceToInsert =
                    if (attendance.tanggalHadir.isBlank()) {
                        attendance.copy(tanggalHadir = getCurrentFormattedDate())
                    } else {
                        attendance
                    }
                localDataSource.insertAttendance(attendanceToInsert)
                emit(Resource.Success(attendanceToInsert.absensiId))
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
                }.collect { emit(Resource.Success(it)) }
        }.catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil jadwal hari ini")) }

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
                }.collect { emit(Resource.Success(it)) }
        }.catch { e ->
            emit(
                Resource.Error(
                    e.message ?: "Gagal mengambil jadwal dosen hari ini",
                ),
            )
        }

    override fun getAllSchedulesByNim(nim: Int): Flow<Resource<List<Kelas>>> =
        localDataSource
            .getAllSchedulesByNim(nim)
            .map<List<com.mjs.core.data.source.local.entity.KelasEntity>, Resource<List<Kelas>>> { kelasEntities ->
                Resource.Success(DataMapper.mapKelasEntitiesToDomains(kelasEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil semua jadwal mahasiswa",
                    ),
                )
            }

    override fun getAllSchedulesByNidn(nidn: Int): Flow<Resource<List<Kelas>>> =
        localDataSource
            .getAllKelas()
            .map<List<com.mjs.core.data.source.local.entity.KelasEntity>, Resource<List<Kelas>>> { kelasEntities ->
                Resource.Success(DataMapper.mapKelasEntitiesToDomains(kelasEntities.filter { it.nidn == nidn }))
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mengambil semua jadwal dosen")) }

    override fun getAttendanceStreak(
        nim: Int,
        kelasId: String,
    ): Flow<Resource<Int>> =
        localDataSource
            .getAttendanceStreak(nim, kelasId)
            .map<com.mjs.core.data.source.local.entity.AttendanceStreakEntity?, Resource<Int>> { streakEntity ->
                Resource.Success(streakEntity?.currentStreak ?: 0)
            }.onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Gagal mendapatkan streak kehadiran")) }

    override suspend fun updateMahasiswaProfile(mahasiswa: Mahasiswa): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            try {
                val currentEntity = localDataSource.getMahasiswaByNim(mahasiswa.nim).first()
                if (currentEntity != null) {
                    val updatedEntity =
                        DataMapper
                            .mapMahasiswaDomainToEntity(mahasiswa)
                            .copy(password = currentEntity.password)
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
                val currentEntity = localDataSource.getDosenByNidn(dosen.nidn).first()
                if (currentEntity != null) {
                    val updatedEntity =
                        DataMapper
                            .mapDosenDomainToEntity(dosen)
                            .copy(password = currentEntity.password)
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
        localDataSource
            .getMahasiswaByKelasId(kelasId)
            .map<List<MahasiswaEntity>, Resource<List<Mahasiswa>>> { mahasiswaEntities ->
                Resource.Success(DataMapper.mapMahasiswaEntitiesToDomains(mahasiswaEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil mahasiswa berdasarkan kelas",
                    ),
                )
            }

    override fun getMahasiswaCountByKelasId(kelasId: String): Flow<Resource<Int>> =
        localDataSource
            .getMahasiswaCountByKelasId(kelasId)
            .map<Int, Resource<Int>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil jumlah mahasiswa di kelas",
                    ),
                )
            }

    override fun getPendingEnrollmentRequests(kelasId: String): Flow<Resource<List<Mahasiswa>>> =
        localDataSource
            .getPendingEnrollmentRequests(kelasId)
            .map<List<MahasiswaEntity>, Resource<List<Mahasiswa>>> { mahasiswaEntities ->
                Resource.Success(DataMapper.mapMahasiswaEntitiesToDomains(mahasiswaEntities))
            }.onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil permintaan pendaftaran pending",
                    ),
                )
            }

    override fun getPendingEnrollmentRequestCount(kelasId: String): Flow<Resource<Int>> =
        localDataSource
            .getPendingEnrollmentRequestCount(kelasId)
            .map<Int, Resource<Int>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                emit(
                    Resource.Error(
                        e.message ?: "Gagal mengambil jumlah permintaan pendaftaran pending",
                    ),
                )
            }

    override fun getPendingEnrollmentRequestCountForDosen(nidn: Int): Flow<Resource<Int>> =
        flow {
            emit(Resource.Loading())
            try {
                localDataSource.getAllKelas().collect { dosenClasses ->
                    val dosenClassesFiltered = dosenClasses.filter { it.nidn == nidn }
                    if (dosenClassesFiltered.isEmpty()) {
                        emit(Resource.Success(0))
                    } else {
                        val countFlows =
                            dosenClassesFiltered.map { kelas ->
                                localDataSource.getPendingEnrollmentRequestCount(kelas.kelasId)
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
                        e.message ?: "Gagal mengambil jumlah permintaan pendaftaran untuk dosen",
                    ),
                )
            }
        }

    override fun getLastNotifiedPendingCount(): Flow<Int> = localDataSource.getLastNotifiedPendingCount()

    override suspend fun saveLastNotifiedPendingCount(count: Int) = localDataSource.saveLastNotifiedPendingCount(count)
}
