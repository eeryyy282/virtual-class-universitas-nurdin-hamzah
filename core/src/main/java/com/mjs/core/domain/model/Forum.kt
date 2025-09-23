package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Forum(
    val forumId: Long,
    val courseId: String,
    val forumTitle: String,
    val description: String,
) : Parcelable
