package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Forum(
    val forumId: Int,
    val kelasId: String,
    val judulForum: String,
    val deskripsi: String,
) : Parcelable
