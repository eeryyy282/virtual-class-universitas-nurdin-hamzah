package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Postingan(
    val postId: Int,
    val forumId: Int,
    val userId: Int,
    val userRole: String,
    val isiPost: String,
    val tanggalPost: String,
) : Parcelable
