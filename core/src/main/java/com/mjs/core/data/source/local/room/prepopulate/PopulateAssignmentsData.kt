package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.room.dao.AssignmentDao
import java.util.Date

internal suspend fun populateAssignmentsAndGetIds(
    assignmentDao: AssignmentDao,
    coursePML: String,
    courseAI: String,
    courseBDT: String,
    courseAPSI: String,
    courseKOMMAS: String,
    courseWEB: String,
    coursePOLPEM: String,
    courseKEBPUB: String,
    coursePEMDA: String,
    oneMonthAgo: Date,
    inOneWeek: Date,
    twoWeeksAgo: Date,
    inTwoWeeks: Date,
    threeDaysAgo: Date,
    inOneMonth: Date,
    oneWeekAgo: Date,
    inThreeDays: Date,
): Map<String, Long> {
    val ids = mutableMapOf<String, Long>()
    ids["assignment1PML"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                coursePML,
                "Tugas 1: Aplikasi Kalkulator",
                "Buat aplikasi kalkulator sederhana menggunakan Jetpack Compose.",
                oneMonthAgo,
                inOneWeek,
                "calculator_specs.pdf",
            ),
        )
    ids["assignment1AI"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                courseAI,
                "Tugas 1: Klasifikasi Gambar",
                "Implementasikan model CNN untuk klasifikasi dataset gambar.",
                twoWeeksAgo,
                inTwoWeeks,
            ),
        )
    ids["assignment1BDT"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                courseBDT,
                "Desain Partisi DB",
                "Rancang skema partisi untuk database skala besar.",
                oneMonthAgo,
                inOneWeek,
            ),
        )
    ids["assignment1APSI"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                courseAPSI,
                "Proposal Proyek SI",
                "Buat proposal untuk pengembangan sistem informasi perpustakaan.",
                threeDaysAgo,
                inTwoWeeks,
                "proposal_template.docx",
            ),
        )
    ids["assignment1KOMMAS"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                courseKOMMAS,
                "Analisis Kampanye Iklan",
                "Analisis efektivitas kampanye iklan digital merek X.",
                oneWeekAgo,
                inOneMonth,
            ),
        )
    ids["assignment1WEB"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                courseWEB,
                "Halaman Arahan Profil Perusahaan",
                "Buat halaman arahan menggunakan React/Vue.",
                twoWeeksAgo,
                inOneMonth,
            ),
        )
    ids["assignment1POLPEM"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                coursePOLPEM,
                "Esai Sistem Presidensial",
                "Analisis kelebihan dan kekurangan sistem presidensial.",
                oneMonthAgo,
                inTwoWeeks,
            ),
        )
    ids["assignment1KEBPUB"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                courseKEBPUB,
                "Ulasan Jurnal Kebijakan",
                "Ulas jurnal internasional tentang kebijakan lingkungan.",
                twoWeeksAgo,
                inOneMonth,
                "review_guideline.pdf",
            ),
        )
    ids["assignment1PEMDA"] =
        assignmentDao.insertAssignment(
            AssignmentEntity(
                0L,
                coursePEMDA,
                "Studi Kasus Pemilihan Daerah",
                "Analisis studi kasus implementasi Pilkada Serentak.",
                oneWeekAgo,
                inThreeDays,
            ),
        )
    return ids
}
