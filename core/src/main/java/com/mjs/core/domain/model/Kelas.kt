package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kelas(
    val kelasId: Int,
    val namaKelas: String,
    val deskripsi: String,
    val nidn: String,
    val jadwal: String,
    val semester: String,
    val credit: Int,
    val category: String,
    val classImage: String? = null,
) : Parcelable
