package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Course(
    val courseId: String,
    val courseName: String,
    val description: String,
    val lecturerId: Long,
    val schedule: Date,
    val semester: String,
    val room: String,
    val credit: Int,
    val department: String,
    val courseImage: String? = null,
) : Parcelable
