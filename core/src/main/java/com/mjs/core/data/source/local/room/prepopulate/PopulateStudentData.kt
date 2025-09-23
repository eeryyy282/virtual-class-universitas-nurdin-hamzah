package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.room.dao.AuthDao

internal suspend fun populateStudents(
    authDao: AuthDao,
    student1Id: Long,
    student2Id: Long,
    student3Id: Long,
    student4Id: Long,
    student5Id: Long,
    student6Id: Long,
) {
    authDao.registerStudent(
        StudentEntity(
            student1Id,
            "Muhammad Juzairi Safitli",
            "juzairi.safitli@student.example.ac.id",
            "pass2111",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            "Dr. Arini Larasati, S.Kom., M.Cs.",
            "Sistem Informasi",
        ),
    )
    authDao.registerStudent(
        StudentEntity(
            student2Id,
            "Yuni Azira",
            "yuni.azira@student.example.ac.id",
            "pass2202",
            "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
            "Prof. Bambang Wijaya, Ph.D.",
            "Sistem Informasi",
        ),
    )
    authDao.registerStudent(
        StudentEntity(
            student3Id,
            "Budi Hartono",
            "budi.hartono@student.example.ac.id",
            "pass2203",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            "Chandra Kusuma, M.Sc.",
            "Teknik Informatika",
        ),
    )
    authDao.registerStudent(
        StudentEntity(
            student4Id,
            "Rina Amelia",
            "rina.amelia@student.example.ac.id",
            "pass2304",
            "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
            "Dr. Arini Larasati, S.Kom., M.Cs.",
            "Ilmu Komunikasi",
        ),
    )
    authDao.registerStudent(
        StudentEntity(
            student5Id,
            "Eko Prasetyo",
            "eko.prasetyo@student.example.ac.id",
            "pass2305",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            "Dr. Dewi Sartika, M.Si.",
            "Ilmu Pemerintahan",
        ),
    )
    authDao.registerStudent(
        StudentEntity(
            student6Id,
            "Siti Aminah",
            "siti.aminah@student.example.ac.id",
            "pass2306",
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
            "Prof. Ahmad Yani, M.Hum.",
            "Ilmu Pemerintahan",
        ),
    )
}
