package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.KelasEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassroomDao {
    @Query("SELECT * FROM classes")
    fun getAllKelas(): Flow<List<KelasEntity>>

    @Query("SELECT * FROM classes WHERE kelas_id = :kelasId")
    fun getKelasById(kelasId: String): Flow<KelasEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelas(kelas: KelasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Query("DELETE FROM enrollments WHERE nim = :nim AND kelas_id = :kelasId")
    suspend fun deleteEnrollment(
        nim: Int,
        kelasId: String,
    )

    @Query("SELECT * FROM enrollments WHERE nim = :nim")
    fun getEnrolledClasses(nim: Int): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE nim = :nim AND kelas_id = :kelasId")
    fun getEnrollmentByNimAndKelasId(
        nim: Int,
        kelasId: String,
    ): Flow<EnrollmentEntity?>

    @Query("SELECT * FROM materials WHERE kelas_id = :kelasId")
    fun getMaterialsByClass(kelasId: String): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("SELECT kelas_id FROM classes WHERE nama_kelas = :namaKelas AND nidn = :nidn ORDER BY kelas_id DESC LIMIT 1")
    suspend fun getKelasIdByNameAndNidn(
        namaKelas: String,
        nidn: Int,
    ): String?

    @Query("SELECT * FROM classes WHERE jurusan = :jurusan")
    fun getAllKelasByJurusan(jurusan: String): Flow<List<KelasEntity>>

    @Query("SELECT m.* FROM mahasiswa m INNER JOIN enrollments e ON m.nim = e.nim WHERE e.kelas_id = :kelasId AND e.status = 'approved'")
    fun getMahasiswaByKelasId(kelasId: String): Flow<List<MahasiswaEntity>>

    @Query("SELECT COUNT(e.nim) FROM enrollments e WHERE e.kelas_id = :kelasId AND e.status = 'approved'")
    fun getMahasiswaCountByKelasId(kelasId: String): Flow<Int>

    @Query("SELECT c.* FROM classes c INNER JOIN enrollments e ON c.kelas_id = e.kelas_id WHERE e.nim = :nim AND e.status = 'approved'")
    fun getAllSchedulesByNim(nim: Int): Flow<List<KelasEntity>>

    @Query("SELECT m.* FROM mahasiswa m INNER JOIN enrollments e ON m.nim = e.nim WHERE e.kelas_id = :kelasId AND e.status = 'pending'")
    fun getPendingEnrollmentRequests(kelasId: String): Flow<List<MahasiswaEntity>>

    @Query("SELECT COUNT(e.nim) FROM enrollments e WHERE e.kelas_id = :kelasId AND e.status = 'pending'")
    fun getPendingEnrollmentRequestCount(kelasId: String): Flow<Int>

    @Query("UPDATE enrollments SET status = :newStatus WHERE nim = :nim AND kelas_id = :kelasId")
    suspend fun updateEnrollmentStatus(
        nim: Int,
        kelasId: String,
        newStatus: String,
    )
}
