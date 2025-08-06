package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Materi(
    val materiId: Int,
    val kelasId: Int,
    val judulMateri: String,
    val deskripsi: String,
    val fileUrl: String,
    val tanggalUpload: String,
) : Parcelable
