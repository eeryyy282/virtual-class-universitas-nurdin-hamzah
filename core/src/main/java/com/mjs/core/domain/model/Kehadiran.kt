package com.mjs.core.domain.model

data class Kehadiran(
    val absensiId: Int,
    val kelasId: Int,
    val nim: String,
    val tanggalSesi: String,
    val status: String,
)
