package com.mjs.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Post(
    val postId: Long,
    val forumId: Long,
    val userId: Long,
    val userRole: String,
    val postContent: String,
    val postDate: Date,
) : Parcelable
