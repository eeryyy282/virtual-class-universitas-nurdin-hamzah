package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.KelasEntity
import com.mjs.core.data.source.local.room.dao.ClassroomDao

internal suspend fun populateKelas(
    classroomDao: ClassroomDao,
    placeholderImageUrl: String,
    dosen1Nidn: Int,
    dosen2Nidn: Int,
    dosen3Nidn: Int,
    dosen4Nidn: Int,
    dosen5Nidn: Int,
    kelasPML: String,
    kelasAI: String,
    kelasWEB: String,
    kelasJARKOM: String,
    kelasGRAFKOM: String,
    kelasBDT: String,
    kelasAPSI: String,
    kelasMANPROSI: String,
    kelasKOMMAS: String,
    kelasJURDIG: String,
    kelasPOLPEM: String,
    kelasKEBPUB: String,
    kelasPEMDA: String,
) {
    classroomDao.insertKelas(
        KelasEntity(
            kelasPML,
            "Pemrograman Mobile Lanjut",
            "Fokus pada pengembangan aplikasi Android native dengan Kotlin, Jetpack Compose, MVVM, dan integrasi API.",
            dosen1Nidn,
            "Senin, 10:00 - 12:30 WIB",
            "Semester Ganjil 2024/2025",
            4,
            "Teknik Informatika",
            "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=400",
            "Gedung A Ruang 301",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasAI,
            "Kecerdasan Buatan Lanjutan",
            "Pembahasan mendalam tentang algoritma machine learning, deep learning, natural language processing, dan computer vision.",
            dosen2Nidn,
            "Rabu, 08:00 - 10:30 WIB",
            "Semester Ganjil 2024/2025",
            4,
            "Teknik Informatika",
            "https://images.unsplash.com/photo-1526378722484-bd91ca387e72?w=400",
            "Gedung C Ruang Cerdas",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasWEB,
            "Pengembangan Web Framework",
            "Praktik penggunaan framework web modern seperti React/Vue untuk frontend dan Node.js/Django untuk backend.",
            dosen1Nidn,
            "Kamis, 14:00 - 16:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Teknik Informatika",
            placeholderImageUrl,
            "Laboratorium Web Programming",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasJARKOM,
            "Jaringan Komputer",
            "Dasar-dasar jaringan komputer, TCP/IP, routing, dan keamanan jaringan.",
            dosen3Nidn,
            "Selasa, 08:00 - 10:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Teknik Informatika",
            placeholderImageUrl,
            "Lab Jaringan Gedung B",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasGRAFKOM,
            "Grafika Komputer",
            "Konsep dasar grafika komputer, pemodelan 2D & 3D, rendering, dan animasi.",
            dosen5Nidn,
            "Jumat, 13:00 - 15:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Teknik Informatika",
            placeholderImageUrl,
            "Studio Desain Gedung C",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasBDT,
            "Basis Data Terdistribusi",
            "Mempelajari konsep, arsitektur, dan implementasi sistem basis data terdistribusi, termasuk replikasi, partisi, dan konsistensi data.",
            dosen2Nidn,
            "Selasa, 13:00 - 15:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Sistem Informasi",
            "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400",
            "Laboratorium Basis Data Terpadu",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasAPSI,
            "Analisis & Perancangan Sistem Informasi",
            "Metodologi analisis kebutuhan dan perancangan sistem informasi enterprise.",
            dosen1Nidn,
            "Kamis, 10:00 - 12:30 WIB",
            "Semester Ganjil 2024/2025",
            4,
            "Sistem Informasi",
            placeholderImageUrl,
            "Ruang Diskusi SI-101",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasMANPROSI,
            "Manajemen Proyek Sistem Informasi",
            "Prinsip dan praktik manajemen proyek dalam pengembangan sistem informasi.",
            dosen3Nidn,
            "Senin, 14:00 - 16:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Sistem Informasi",
            placeholderImageUrl,
            "Aula Proyek Gedung D",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasKOMMAS,
            "Komunikasi Pemasaran",
            "Strategi komunikasi dalam pemasaran produk dan jasa di era digital.",
            dosen4Nidn,
            "Rabu, 13:00 - 15:30 WIB",
            "Semester Genap 2023/2024",
            3,
            "Ilmu Komunikasi",
            placeholderImageUrl,
            "Studio Media IK-202",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasJURDIG,
            "Jurnalistik Digital",
            "Praktik jurnalistik online, media sosial, dan multimedia storytelling.",
            dosen5Nidn,
            "Jumat, 09:00 - 11:30 WIB",
            "Semester Genap 2023/2024",
            3,
            "Ilmu Komunikasi",
            placeholderImageUrl,
            "Newsroom IK-105",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasPOLPEM,
            "Pengantar Ilmu Politik & Pemerintahan",
            "Dasar-dasar konsep ilmu politik, sistem pemerintahan, dan hubungan antar lembaga negara.",
            dosen4Nidn,
            "Senin, 08:00 - 10:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Ilmu Pemerintahan",
            "https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=400",
            "Aula Fisipol Gedung E",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasKEBPUB,
            "Kebijakan Publik",
            "Analisis proses perumusan, implementasi, dan evaluasi kebijakan publik di berbagai sektor.",
            dosen5Nidn,
            "Rabu, 10:00 - 12:30 WIB",
            "Semester Ganjil 2024/2025",
            4,
            "Ilmu Pemerintahan",
            "https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=400",
            "Ruang Seminar IP-201",
        ),
    )
    classroomDao.insertKelas(
        KelasEntity(
            kelasPEMDA,
            "Pemerintahan Daerah",
            "Studi mengenai otonomi daerah, hubungan pusat-daerah, dan manajemen pemerintahan di tingkat lokal.",
            dosen4Nidn,
            "Jumat, 14:00 - 16:30 WIB",
            "Semester Ganjil 2024/2025",
            3,
            "Ilmu Pemerintahan",
            placeholderImageUrl,
            "Ruang Kelas IP-105",
        ),
    )
}
