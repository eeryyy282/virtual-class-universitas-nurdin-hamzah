package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kelas(
    val kelasId: String,
    val namaKelas: String,
    val deskripsi: String,
    val nidn: Int,
    val jadwal: String,
    val semester: String,
    val ruang: String,
    val credit: Int,
    val jurusan: String,
    val classImage: String? = null,
) : Parcelable
