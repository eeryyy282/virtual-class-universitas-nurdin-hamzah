package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.LecturerEntity
import com.mjs.core.data.source.local.room.dao.AuthDao

internal suspend fun populateLecturers(
    authDao: AuthDao,
    lecturer1Id: Long,
    lecturer2Id: Long,
    lecturer3Id: Long,
    lecturer4Id: Long,
    lecturer5Id: Long,
) {
    authDao.registerLecturer(
        LecturerEntity(
            lecturer1Id,
            "Sri Mulyati, M.Kom.",
            "sri.mulyati@example.ac.id",
            "pass1122",
            "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
        ),
    )
    authDao.registerLecturer(
        LecturerEntity(
            lecturer2Id,
            "Ahmad Louis, M. Kom.",
            "ahmad.louis@example.ac.id",
            "pass2233",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
        ),
    )
    authDao.registerLecturer(
        LecturerEntity(
            lecturer3Id,
            "Chandra Kusuma, M.Sc.",
            "chandra.kusuma@example.ac.id",
            "pass3344",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
        ),
    )
    authDao.registerLecturer(
        LecturerEntity(
            lecturer4Id,
            "Dr. Dewi Sartika, M.Si.",
            "dewi.sartika@example.ac.id",
            "pass4455",
            "https://images.unsplash.com/photo-1573497491208-6b1acb260507?w=400",
        ),
    )
    authDao.registerLecturer(
        LecturerEntity(
            lecturer5Id,
            "Prof. Ahmad Yani, M.Hum.",
            "ahmad.yani@example.ac.id",
            "pass5566",
            "https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400",
        ),
    )
}
