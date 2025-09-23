package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.room.dao.CourseDao
import java.util.Date

internal suspend fun populateMaterials(
    courseDao: CourseDao,
    coursePML: String,
    courseAI: String,
    courseBDT: String,
    courseAPSI: String,
    courseKOMMAS: String,
    courseWEB: String,
    courseJARKOM: String,
    coursePOLPEM: String,
    courseKEBPUB: String,
    coursePEMDA: String,
    oneMonthAgo: Date,
    threeDaysAgo: Date,
    twoWeeksAgo: Date,
    oneWeekAgo: Date,
) {
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            coursePML,
            "Slide Pengantar Kotlin",
            "Pengenalan dasar bahasa Kotlin untuk Android.",
            "kotlin_intro.pdf",
            oneMonthAgo,
            "pdf",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            coursePML,
            "Video Tutorial Jetpack Compose",
            "Link video tutorial membuat UI dengan Compose.",
            "https://youtube.com/compose-tutorial",
            threeDaysAgo,
            "link",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseAI,
            "Paper Deep Learning",
            "Riset terkini tentang convolutional neural networks.",
            "cnn_research.pdf",
            twoWeeksAgo,
            "pdf",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseBDT,
            "Modul Replikasi Database",
            "Konsep dan teknik replikasi pada basis data terdistribusi.",
            "replikasi_db.pdf",
            oneMonthAgo,
            "pdf",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseAPSI,
            "Studi Kasus Perancangan SI",
            "Contoh analisis dan perancangan sistem untuk e-commerce.",
            "case_study_ecommerce.docx",
            oneWeekAgo,
            "docx",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseKOMMAS,
            "Ebook Digital Marketing",
            "Panduan lengkap strategi pemasaran digital.",
            "digital_marketing_guide.epub",
            twoWeeksAgo,
            "epub",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseWEB,
            "Dokumentasi ReactJS",
            "Link ke dokumentasi resmi ReactJS.",
            "https://reactjs.org/docs",
            oneMonthAgo,
            "link",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseJARKOM,
            "Simulasi Jaringan Cisco",
            "File packet tracer untuk simulasi jaringan.",
            "simulasi_router.pkt",
            threeDaysAgo,
            "pkt",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            coursePOLPEM,
            "Buku Dasar Ilmu Pemerintahan",
            "Referensi utama mata kuliah.",
            "dasar_pemerintahan.pdf",
            oneMonthAgo,
            "pdf",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            courseKEBPUB,
            "Contoh Analisis Kebijakan",
            "Studi kasus analisis kebijakan pendidikan.",
            "analisis_kebijakan_pendidikan.pdf",
            twoWeeksAgo,
            "pdf",
        ),
    )
    courseDao.insertMaterial(
        MaterialEntity(
            0L,
            coursePEMDA,
            "UU Otonomi Daerah",
            "Salinan Undang-Undang terkait Pemda.",
            "uu_otda.pdf",
            oneWeekAgo,
            "pdf",
        ),
    )
}
