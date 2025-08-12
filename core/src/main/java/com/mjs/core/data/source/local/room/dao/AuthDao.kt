package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mjs.core.data.source.local.entity.DosenEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Query("SELECT * FROM mahasiswa WHERE nim = :nim AND password = :sandi")
    suspend fun loginMahasiswa(
        nim: Int,
        sandi: String,
    ): MahasiswaEntity?

    @Query("SELECT * FROM dosen WHERE nidn = :nidn AND password = :sandi")
    suspend fun loginDosen(
        nidn: Int,
        sandi: String,
    ): DosenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerMahasiswa(mahasiswa: MahasiswaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerDosen(dosen: DosenEntity)

    @Query("SELECT * FROM mahasiswa WHERE nim = :nim")
    fun getMahasiswaByNim(nim: Int): Flow<MahasiswaEntity?>

    @Query("SELECT * FROM dosen WHERE nidn = :nidn")
    fun getDosenByNidn(nidn: Int): Flow<DosenEntity?>

    @Update
    suspend fun updateMahasiswa(mahasiswa: MahasiswaEntity)

    @Update
    suspend fun updateDosen(dosen: DosenEntity)
}
