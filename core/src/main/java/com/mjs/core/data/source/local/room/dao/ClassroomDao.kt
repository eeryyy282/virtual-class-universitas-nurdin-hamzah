package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.KelasEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassroomDao {
    @Query("SELECT * FROM classes")
    fun getAllKelas(): Flow<List<KelasEntity>>

    @Query("SELECT * FROM classes WHERE kelas_id = :kelasId") // Added this function
    fun getKelasById(kelasId: Int): Flow<KelasEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelas(kelas: KelasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Query("SELECT * FROM enrollments WHERE nim = :nim")
    fun getEnrolledClasses(nim: String): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM materials WHERE kelas_id = :kelasId")
    fun getMaterialsByClass(kelasId: Int): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("SELECT kelas_id FROM classes WHERE nama_kelas = :namaKelas AND nidn = :nidn ORDER BY kelas_id DESC LIMIT 1")
    suspend fun getKelasIdByNameAndNidn(
        namaKelas: String,
        nidn: String,
    ): Int?
}
