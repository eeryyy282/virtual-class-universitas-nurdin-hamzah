package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Materi(
    val materiId: Int,
    val kelasId: String,
    val judulMateri: String,
    val deskripsi: String,
    val attachment: String? = null,
    val tanggalUpload: String,
    val tipe: String? = null,
) : Parcelable
