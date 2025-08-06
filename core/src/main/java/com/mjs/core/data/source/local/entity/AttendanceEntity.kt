package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = KelasEntity::class,
            parentColumns = ["kelas_id"],
            childColumns = ["kelas_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MahasiswaEntity::class,
            parentColumns = ["nim"],
            childColumns = ["nim"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "absensi_id")
    val absensiId: Int = 0,
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int,
    val nim: String,
    @ColumnInfo(name = "tanggal_sesi")
    val tanggalSesi: String,
    val status: String,
)
