package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Material(
    val materialId: Long,
    val courseId: String,
    val courseTitle: String,
    val description: String,
    val attachment: String? = null,
    val uploadDate: Date,
    val type: String? = null,
) : Parcelable
