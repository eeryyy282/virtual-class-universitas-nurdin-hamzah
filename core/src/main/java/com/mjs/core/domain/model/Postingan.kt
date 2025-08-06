package com.mjs.core.domain.model

data class Postingan(
    val postId: Int,
    val forumId: Int,
    val userId: String,
    val userRole: String,
    val isiPost: String,
    val tanggalPost: String,
)
