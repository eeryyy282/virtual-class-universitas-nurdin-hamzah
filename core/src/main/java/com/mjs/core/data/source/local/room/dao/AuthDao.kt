package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.DosenEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity)

    @Query("SELECT * FROM mahasiswa WHERE nim = :nim")
    fun getMahasiswaByNim(nim: String): Flow<MahasiswaEntity?>

    @Query("SELECT * FROM dosen WHERE nidn = :nidn")
    fun getDosenByNidn(nidn: String): Flow<DosenEntity?>
}
