package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lecturer(
    val lecturerId: Long,
    val name: String,
    val email: String,
    val profilePicture: String?,
) : Parcelable
