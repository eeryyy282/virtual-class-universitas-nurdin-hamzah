package com.mjs.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dosen")
data class DosenEntity(
    @PrimaryKey
    val nidn: Int,
    val nama: String,
    val email: String,
    val password: String,
    @ColumnInfo(name = "foto_profil")
    val fotoProfil: String? = null,
)
