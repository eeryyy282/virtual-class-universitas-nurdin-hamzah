package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dosen(
    val nidn: Int,
    val nama: String,
    val email: String,
    val fotoProfil: String?,
) : Parcelable
