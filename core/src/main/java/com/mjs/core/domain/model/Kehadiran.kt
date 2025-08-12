package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kehadiran(
    val absensiId: Int,
    val kelasId: String,
    val nim: Int,
    val tanggalHadir: String,
    val status: String,
    val keterangan: String? = null,
) : Parcelable
