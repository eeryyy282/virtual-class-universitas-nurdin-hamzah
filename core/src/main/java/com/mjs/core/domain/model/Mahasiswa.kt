package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mahasiswa(
    val nim: Int,
    val nama: String,
    val email: String,
    val fotoProfil: String?,
    val dosenPembimbing: String,
    val jurusan: String,
) : Parcelable
