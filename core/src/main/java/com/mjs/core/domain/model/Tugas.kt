package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tugas(
    val assignmentId: Int,
    val kelasId: String,
    val judulTugas: String,
    val deskripsi: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val attachment: String? = null,
) : Parcelable
