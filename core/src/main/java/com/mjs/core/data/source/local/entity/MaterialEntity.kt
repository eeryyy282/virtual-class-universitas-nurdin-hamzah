package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = KelasEntity::class,
            parentColumns = ["kelas_id"],
            childColumns = ["kelas_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "materi_id")
    val materiId: Int = 0,
    @ColumnInfo(name = "kelas_id")
    val kelasId: String,
    @ColumnInfo(name = "judul_materi")
    val judulMateri: String,
    val deskripsi: String,
    @ColumnInfo(name = "attachment")
    val attachment: String? = null,
    @ColumnInfo(name = "tanggal_upload")
    val tanggalUpload: String,
    @ColumnInfo(name = "type")
    val tipe: String? = null,
)
