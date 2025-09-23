package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Assignment(
    val assignmentId: Long,
    val courseId: String,
    val assignmentTitle: String,
    val description: String,
    val startDate: Date,
    val deadlineDate: Date,
    val attachment: String? = null,
) : Parcelable
