package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.DosenEntity
import com.mjs.core.data.source.local.room.dao.AuthDao

internal suspend fun populateDosen(
    authDao: AuthDao,
    dosen1Nidn: Int,
    dosen2Nidn: Int,
    dosen3Nidn: Int,
    dosen4Nidn: Int,
    dosen5Nidn: Int,
) {
    authDao.registerDosen(
        DosenEntity(
            dosen1Nidn,
            "Sri Mulyati M.Kom.",
            "sri.mulyati@example.ac.id",
            "pass1122",
            "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
        ),
    )
    authDao.registerDosen(
        DosenEntity(
            dosen2Nidn,
            "Ahmad Louis, M. Kom.",
            "ahmad.louis@example.ac.id",
            "pass2233",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
        ),
    )
    authDao.registerDosen(
        DosenEntity(
            dosen3Nidn,
            "Chandra Kusuma, M.Sc.",
            "chandra.kusuma@example.ac.id",
            "pass3344",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
        ),
    )
    authDao.registerDosen(
        DosenEntity(
            dosen4Nidn,
            "Dr. Dewi Sartika, M.Si.",
            "dewi.sartika@example.ac.id",
            "pass4455",
            "https://images.unsplash.com/photo-1573497491208-6b1acb260507?w=400",
        ),
    )
    authDao.registerDosen(
        DosenEntity(
            dosen5Nidn,
            "Prof. Ahmad Yani, M.Hum.",
            "ahmad.yani@example.ac.id",
            "pass5566",
            "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400",
        ),
    )
}
