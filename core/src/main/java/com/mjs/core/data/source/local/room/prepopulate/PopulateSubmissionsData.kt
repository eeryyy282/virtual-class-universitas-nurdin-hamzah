package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.room.dao.TaskDao

internal suspend fun populateSubmissions(
    taskDao: TaskDao,
    tugas1PMLId: Long,
    tugas1AIId: Long,
    tugas1BDTId: Long,
    tugas1KOMMASId: Long,
    tugas1POLPEMId: Long,
    tugas1KEBPUBId: Long,
    mhs1Nim: Int,
    mhs2Nim: Int,
    mhs3Nim: Int,
    mhs4Nim: Int,
    mhs5Nim: Int,
    mhs6Nim: Int,
    yesterday: String,
    currentDate: String,
) {
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1PMLId.toInt(),
            mhs3Nim,
            "kalkulator_budi.zip",
            yesterday,
            85,
            "Bagus, tapi UI bisa ditingkatkan.",
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1PMLId.toInt(),
            mhs1Nim,
            "kalkulator_juzairi.zip",
            currentDate,
            90,
            "Implementasi logic sudah benar.",
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1AIId.toInt(),
            mhs3Nim,
            "cnn_model_budi.zip",
            currentDate,
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1BDTId.toInt(),
            mhs1Nim,
            "partisi_db_juzairi.pdf",
            yesterday,
            78,
            "Perlu penjelasan lebih detail pada pilihan strategi.",
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1BDTId.toInt(),
            mhs2Nim,
            "partisi_db_yuni.pdf",
            currentDate,
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1KOMMASId.toInt(),
            mhs4Nim,
            "analisis_iklan_rina.docx",
            yesterday,
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1POLPEMId.toInt(),
            mhs5Nim,
            "esai_eko.docx",
            yesterday,
            88,
            "Analisis cukup tajam.",
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1POLPEMId.toInt(),
            mhs6Nim,
            "esai_siti.pdf",
            currentDate,
        ),
    )
    taskDao.insertSubmission(
        SubmissionEntity(
            0,
            tugas1KEBPUBId.toInt(),
            mhs5Nim,
            "review_eko.pdf",
            currentDate,
        ),
    )
}
