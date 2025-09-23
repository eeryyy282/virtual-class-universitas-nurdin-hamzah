package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    val studentId: Long,
    val name: String,
    val email: String,
    val profilePicture: String?,
    val supervisor: String,
    val department: String,
) : Parcelable
