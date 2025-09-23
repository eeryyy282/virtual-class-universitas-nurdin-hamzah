package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Submission(
    val submissionId: Long,
    val assignmentId: Long,
    val studentId: Long,
    val attachment: String? = null,
    val submissionDate: Date,
    val grade: Int? = null,
    val note: String? = null,
) : Parcelable
