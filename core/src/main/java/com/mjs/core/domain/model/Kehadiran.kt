package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kehadiran(
    val absensiId: Int,
    val kelasId: Int,
    val nim: String,
    val tanggalSesi: String,
    val status: String,
) : Parcelable
