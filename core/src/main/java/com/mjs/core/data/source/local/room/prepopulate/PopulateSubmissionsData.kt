package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.room.dao.SubmissionDao
import java.util.Date

internal suspend fun populateSubmissions(
    submissionDao: SubmissionDao,
    assignment1PMLId: Long,
    assignment1AIId: Long,
    assignment1BDTId: Long,
    assignment1KOMMASId: Long,
    assignment1POLPEMId: Long,
    assignment1KEBPUBId: Long,
    student1Id: Long,
    student2Id: Long,
    student3Id: Long,
    student4Id: Long,
    student5Id: Long,
    student6Id: Long,
    yesterday: Date,
    currentDate: Date,
) {
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1PMLId,
            studentId = student3Id,
            attachment = "kalkulator_budi.zip",
            submissionDate = yesterday,
            grade = 85,
            note = "Bagus, tapi UI bisa ditingkatkan.",
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1PMLId,
            studentId = student1Id,
            attachment = "kalkulator_juzairi.zip",
            submissionDate = currentDate,
            grade = 90,
            note = "Implementasi logic sudah benar.",
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1AIId,
            studentId = student3Id,
            attachment = "cnn_model_budi.zip",
            submissionDate = currentDate,
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1BDTId,
            studentId = student1Id,
            attachment = "partisi_db_juzairi.pdf",
            submissionDate = yesterday,
            grade = 78,
            note = "Perlu penjelasan lebih detail pada pilihan strategi.",
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1BDTId,
            studentId = student2Id,
            attachment = "partisi_db_yuni.pdf",
            submissionDate = currentDate,
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1KOMMASId,
            studentId = student4Id,
            attachment = "analisis_iklan_rina.docx",
            submissionDate = yesterday,
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1POLPEMId,
            studentId = student5Id,
            attachment = "esai_eko.docx",
            submissionDate = yesterday,
            grade = 88,
            note = "Analisis cukup tajam.",
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1POLPEMId,
            studentId = student6Id,
            attachment = "esai_siti.pdf",
            submissionDate = currentDate,
        ),
    )
    submissionDao.insertSubmission(
        SubmissionEntity(
            submissionId = 0L,
            assignmentId = assignment1KEBPUBId,
            studentId = student5Id,
            attachment = "review_eko.pdf",
            submissionDate = currentDate,
        ),
    )
}
