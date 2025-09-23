package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.room.dao.CourseDao
import java.util.Date // Import Date

internal suspend fun populateCourses(
    courseDao: CourseDao,
    placeholderImageUrl: String,
    lecturer1Id: Long,
    lecturer2Id: Long,
    lecturer3Id: Long,
    lecturer4Id: Long,
    lecturer5Id: Long,
    coursePML: String,
    courseAI: String,
    courseWEB: String,
    courseJARKOM: String,
    courseGRAFKOM: String,
    courseBDT: String,
    courseAPSI: String,
    courseMANPROSI: String,
    courseKOMMAS: String,
    courseJURDIG: String,
    coursePOLPEM: String,
    courseKEBPUB: String,
    coursePEMDA: String,
    scheduleTime1: Date,
    scheduleTime2: Date,
    scheduleTime3: Date,
) {
    courseDao.insertCourse(
        CourseEntity(
            coursePML,
            "Pemrograman Mobile Lanjut",
            "Fokus pada pengembangan aplikasi Android native dengan Kotlin, Jetpack Compose, MVVM, dan integrasi API.",
            lecturer1Id,
            scheduleTime1,
            "Semester Ganjil 2024/2025",
            4,
            "Teknik Informatika",
            "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=400",
            "Gedung A Ruang 301",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseAI,
            "Kecerdasan Buatan Lanjutan",
            "Pembahasan mendalam tentang algoritma machine learning, deep learning, natural language processing, dan computer vision.",
            lecturer2Id,
            scheduleTime2,
            "Semester Ganjil 2024/2025",
            4,
            "Teknik Informatika",
            "https://images.unsplash.com/photo-1526378722484-bd91ca387e72?w=400",
            "Gedung Filkom Ruang 3.3",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseWEB,
            "Pengembangan Web Framework",
            "Praktik penggunaan framework web modern seperti React/Vue untuk frontend dan Node.js/Django untuk backend.",
            lecturer1Id,
            scheduleTime3,
            "Semester Ganjil 2024/2025",
            3,
            "Teknik Informatika",
            placeholderImageUrl,
            "Laboratorium Web Programming",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseJARKOM,
            "Jaringan Komputer",
            "Dasar-dasar jaringan komputer, TCP/IP, routing, dan keamanan jaringan.",
            lecturer3Id,
            scheduleTime1,
            "Semester Ganjil 2024/2025",
            3,
            "Teknik Informatika",
            placeholderImageUrl,
            "Lab Jaringan Gedung B",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseGRAFKOM,
            "Grafika Komputer",
            "Konsep dasar grafika komputer, pemodelan 2D & 3D, rendering, dan animasi.",
            lecturer5Id,
            scheduleTime2,
            "Semester Ganjil 2024/2025",
            3,
            "Teknik Informatika",
            placeholderImageUrl,
            "Studio Desain Gedung C",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseBDT,
            "Basis Data Terdistribusi",
            "Mempelajari konsep, arsitektur, dan implementasi sistem basis data terdistribusi, termasuk replikasi, partisi, dan konsistensi data.",
            lecturer2Id,
            scheduleTime3,
            "Semester Ganjil 2024/2025",
            3,
            "Sistem Informasi",
            "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400",
            "Laboratorium Basis Data Terpadu",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseAPSI,
            "Analisis & Perancangan Sistem Informasi",
            "Metodologi analisis kebutuhan dan perancangan sistem informasi enterprise.",
            lecturer1Id,
            scheduleTime1,
            "Semester Ganjil 2024/2025",
            4,
            "Sistem Informasi",
            placeholderImageUrl,
            "Ruang Diskusi SI-101",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseMANPROSI,
            "Manajemen Proyek Sistem Informasi",
            "Prinsip dan praktik manajemen proyek dalam pengembangan sistem informasi.",
            lecturer3Id,
            scheduleTime2,
            "Semester Ganjil 2024/2025",
            3,
            "Sistem Informasi",
            placeholderImageUrl,
            "Aula Proyek Gedung D",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseKOMMAS,
            "Komunikasi Pemasaran",
            "Strategi komunikasi dalam pemasaran produk dan jasa di era digital.",
            lecturer4Id,
            scheduleTime3,
            "Semester Genap 2023/2024",
            3,
            "Ilmu Komunikasi",
            placeholderImageUrl,
            "Studio Media IK-202",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseJURDIG,
            "Jurnalistik Digital",
            "Praktik jurnalistik online, media sosial, dan multimedia storytelling.",
            lecturer5Id,
            scheduleTime1,
            "Semester Genap 2023/2024",
            3,
            "Ilmu Komunikasi",
            placeholderImageUrl,
            "Newsroom IK-105",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            coursePOLPEM,
            "Pengantar Ilmu Politik & Pemerintahan",
            "Dasar-dasar konsep ilmu politik, sistem pemerintahan, dan hubungan antar lembaga negara.",
            lecturer4Id,
            scheduleTime2,
            "Semester Ganjil 2024/2025",
            3,
            "Ilmu Pemerintahan",
            "https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=400",
            "Aula Fisipol Gedung E",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            courseKEBPUB,
            "Kebijakan Publik",
            "Analisis proses perumusan, implementasi, dan evaluasi kebijakan publik di berbagai sektor.",
            lecturer5Id,
            scheduleTime3,
            "Semester Ganjil 2024/2025",
            4,
            "Ilmu Pemerintahan",
            "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=400",
            "Ruang Seminar IP-201",
        ),
    )
    courseDao.insertCourse(
        CourseEntity(
            coursePEMDA,
            "Pemerintahan Daerah",
            "Studi mengenai otonomi daerah, hubungan pusat-daerah, dan manajemen pemerintahan di tingkat lokal.",
            lecturer4Id,
            scheduleTime1,
            "Semester Ganjil 2024/2025",
            3,
            "Ilmu Pemerintahan",
            placeholderImageUrl,
            "Ruang Course IP-105",
        ),
    )
}
