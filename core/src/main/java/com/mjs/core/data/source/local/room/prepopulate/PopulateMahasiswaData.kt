package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.room.dao.AuthDao

internal suspend fun populateMahasiswa(
    authDao: AuthDao,
    mhs1Nim: Int,
    mhs2Nim: Int,
    mhs3Nim: Int,
    mhs4Nim: Int,
    mhs5Nim: Int,
    mhs6Nim: Int,
) {
    authDao.registerMahasiswa(
        MahasiswaEntity(
            mhs1Nim,
            "Muhammad Juzairi Safitli",
            "juzairi.safitli@student.example.ac.id",
            "pass2111",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            "Dr. Arini Larasati, S.Kom., M.Cs.",
            "Sistem Informasi",
        ),
    )
    authDao.registerMahasiswa(
        MahasiswaEntity(
            mhs2Nim,
            "Yuni Azira",
            "yuni.azira@student.example.ac.id",
            "pass2202",
            "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
            "Prof. Bambang Wijaya, Ph.D.",
            "Sistem Informasi",
        ),
    )
    authDao.registerMahasiswa(
        MahasiswaEntity(
            mhs3Nim,
            "Budi Hartono",
            "budi.hartono@student.example.ac.id",
            "pass2203",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            "Chandra Kusuma, M.Sc.",
            "Teknik Informatika",
        ),
    )
    authDao.registerMahasiswa(
        MahasiswaEntity(
            mhs4Nim,
            "Rina Amelia",
            "rina.amelia@student.example.ac.id",
            "pass2304",
            "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
            "Dr. Arini Larasati, S.Kom., M.Cs.",
            "Ilmu Komunikasi",
        ),
    )
    authDao.registerMahasiswa(
        MahasiswaEntity(
            mhs5Nim,
            "Eko Prasetyo",
            "eko.prasetyo@student.example.ac.id",
            "pass2305",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            "Dr. Dewi Sartika, M.Si.",
            "Ilmu Pemerintahan",
        ),
    )
    authDao.registerMahasiswa(
        MahasiswaEntity(
            mhs6Nim,
            "Siti Aminah",
            "siti.aminah@student.example.ac.id",
            "pass2306",
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
            "Prof. Ahmad Yani, M.Hum.",
            "Ilmu Pemerintahan",
        ),
    )
}
