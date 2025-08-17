package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.room.dao.TaskDao

internal suspend fun populateAssignmentsAndGetIds(
    taskDao: TaskDao,
    kelasPML: String,
    kelasAI: String,
    kelasBDT: String,
    kelasAPSI: String,
    kelasKOMMAS: String,
    kelasWEB: String,
    kelasPOLPEM: String,
    kelasKEBPUB: String,
    kelasPEMDA: String,
    oneMonthAgo: String,
    inOneWeek: String,
    twoWeeksAgo: String,
    inTwoWeeks: String,
    threeDaysAgo: String,
    inOneMonth: String,
    oneWeekAgo: String,
    inThreeDays: String,
): Map<String, Long> {
    val ids = mutableMapOf<String, Long>()
    ids["tugas1PML"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasPML,
                "Tugas 1: Aplikasi Kalkulator",
                "Buat aplikasi kalkulator sederhana dengan Jetpack Compose.",
                oneMonthAgo,
                inOneWeek,
                "kalkulator_spek.pdf",
            ),
        )
    ids["tugas1AI"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasAI,
                "Tugas 1: Klasifikasi Gambar",
                "Implementasikan model CNN untuk klasifikasi dataset gambar.",
                twoWeeksAgo,
                inTwoWeeks,
            ),
        )
    ids["tugas1BDT"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasBDT,
                "Rancangan Partisi DB",
                "Rancang skema partisi untuk database skala besar.",
                oneMonthAgo,
                inOneWeek,
            ),
        )
    ids["tugas1APSI"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasAPSI,
                "Proposal Proyek SI",
                "Buat proposal pengembangan sistem informasi perpustakaan.",
                threeDaysAgo,
                inTwoWeeks,
                "template_proposal.docx",
            ),
        )
    ids["tugas1KOMMAS"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasKOMMAS,
                "Analisis Kampanye Iklan",
                "Analisis efektivitas kampanye iklan digital brand X.",
                oneWeekAgo,
                inOneMonth,
            ),
        )
    taskDao.insertAssignment(
        AssignmentEntity(
            0,
            kelasWEB,
            "Landing Page Company Profile",
            "Buat landing page menggunakan React/Vue.",
            twoWeeksAgo,
            inOneMonth,
        ),
    )
    ids["tugas1POLPEM"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasPOLPEM,
                "Esai Sistem Presidensial",
                "Analisis kelebihan dan kekurangan sistem presidensial.",
                oneMonthAgo,
                inTwoWeeks,
            ),
        )
    ids["tugas1KEBPUB"] =
        taskDao.insertAssignment(
            AssignmentEntity(
                0,
                kelasKEBPUB,
                "Review Jurnal Kebijakan",
                "Review jurnal internasional tentang kebijakan lingkungan.",
                twoWeeksAgo,
                inOneMonth,
                "guideline_review.pdf",
            ),
        )
    taskDao.insertAssignment(
        AssignmentEntity(
            0,
            kelasPEMDA,
            "Studi Kasus Pilkada",
            "Analisis studi kasus pelaksanaan Pilkada Serentak.",
            oneWeekAgo,
            inThreeDays,
        ),
    )
    return ids
}
