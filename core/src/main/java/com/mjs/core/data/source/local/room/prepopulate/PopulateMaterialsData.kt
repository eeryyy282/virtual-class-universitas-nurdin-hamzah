package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.room.dao.TaskDao

internal suspend fun populateMaterials(
    taskDao: TaskDao,
    kelasPML: String,
    kelasAI: String,
    kelasBDT: String,
    kelasAPSI: String,
    kelasKOMMAS: String,
    kelasWEB: String,
    kelasJARKOM: String,
    kelasPOLPEM: String,
    kelasKEBPUB: String,
    kelasPEMDA: String,
    oneMonthAgo: String,
    threeDaysAgo: String,
    twoWeeksAgo: String,
    oneWeekAgo: String,
) {
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasPML,
            "Slide Pengantar Kotlin",
            "Pengenalan dasar bahasa Kotlin untuk Android.",
            "kotlin_intro.pdf",
            oneMonthAgo,
            "pdf",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasPML,
            "Video Tutorial Jetpack Compose",
            "Link video tutorial membuat UI dengan Compose.",
            "https://youtube.com/compose-tutorial",
            threeDaysAgo,
            "link",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasAI,
            "Paper Deep Learning",
            "Riset terkini tentang convolutional neural networks.",
            "cnn_research.pdf",
            twoWeeksAgo,
            "pdf",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasBDT,
            "Modul Replikasi Database",
            "Konsep dan teknik replikasi pada basis data terdistribusi.",
            "replikasi_db.pdf",
            oneMonthAgo,
            "pdf",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasAPSI,
            "Studi Kasus Perancangan SI",
            "Contoh analisis dan perancangan sistem untuk e-commerce.",
            "case_study_ecommerce.docx",
            oneWeekAgo,
            "docx",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasKOMMAS,
            "Ebook Digital Marketing",
            "Panduan lengkap strategi pemasaran digital.",
            "digital_marketing_guide.epub",
            twoWeeksAgo,
            "epub",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasWEB,
            "Dokumentasi ReactJS",
            "Link ke dokumentasi resmi ReactJS.",
            "https://reactjs.org/docs",
            oneMonthAgo,
            "link",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasJARKOM,
            "Simulasi Jaringan Cisco",
            "File packet tracer untuk simulasi jaringan.",
            "simulasi_router.pkt",
            threeDaysAgo,
            "pkt",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasPOLPEM,
            "Buku Dasar Ilmu Pemerintahan",
            "Referensi utama mata kuliah.",
            "dasar_pemerintahan.pdf",
            oneMonthAgo,
            "pdf",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasKEBPUB,
            "Contoh Analisis Kebijakan",
            "Studi kasus analisis kebijakan pendidikan.",
            "analisis_kebijakan_pendidikan.pdf",
            twoWeeksAgo,
            "pdf",
        ),
    )
    taskDao.insertMaterial(
        MaterialEntity(
            0,
            kelasPEMDA,
            "UU Otonomi Daerah",
            "Salinan Undang-Undang terkait Pemda.",
            "uu_otda.pdf",
            oneWeekAgo,
            "pdf",
        ),
    )
}
