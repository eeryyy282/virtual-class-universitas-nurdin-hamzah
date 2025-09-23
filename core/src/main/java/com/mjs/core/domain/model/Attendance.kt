package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Attendance(
    val attendanceId: Long,
    val courseId: String,
    val studentId: Long,
    val attendanceDate: Date,
    val status: String,
    val description: String? = null,
) : Parcelable
