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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int = 0,
    @ColumnInfo(name = "nama_kelas")
    val namaKelas: String,
    val deskripsi: String,
    val nidn: String,
    val jadwal: String,
)
