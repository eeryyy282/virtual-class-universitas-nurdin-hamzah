package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "classes",
    foreignKeys = [
        ForeignKey(
            entity = DosenEntity::class,
            parentColumns = ["nidn"],
            childColumns = ["nidn"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class KelasEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "kelas_id")
    val kelasId: String,
    @ColumnInfo(name = "nama_kelas")
    val namaKelas: String,
    val deskripsi: String,
    val nidn: Int,
    val jadwal: String,
    val semester: String,
    val credit: Int,
    val category: String,
    val classImage: String? = null,
    val ruang: String,
)
