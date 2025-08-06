package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tugas(
    val assignmentId: Int,
    val kelasId: Int,
    val judulTugas: String,
    val deskripsi: String,
    val deadline: String,
) : Parcelable
