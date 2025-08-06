package com.mjs.core.domain.model

data class Tugas(
    val assignmentId: Int,
    val kelasId: Int,
    val judulTugas: String,
    val deskripsi: String,
    val deadline: String,
)
