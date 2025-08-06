package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "assignments",
    foreignKeys = [
        ForeignKey(
            entity = KelasEntity::class,
            parentColumns = ["kelas_id"],
            childColumns = ["kelas_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Int = 0,
    @ColumnInfo(name = "kelas_id")
    val kelasId: Int,
    @ColumnInfo(name = "judul_tugas")
    val judulTugas: String,
    val deskripsi: String,
    val deadline: String,
)
