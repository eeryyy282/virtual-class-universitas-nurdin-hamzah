package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.room.dao.ForumDao

internal suspend fun populateForumsAndGetIds(
    forumDao: ForumDao,
    kelasPML: String,
    kelasAI: String,
    kelasBDT: String,
    kelasKOMMAS: String,
    kelasPOLPEM: String,
    kelasKEBPUB: String,
    oneMonthAgo: String,
    twoWeeksAgo: String,
    threeDaysAgo: String,
    oneWeekAgo: String,
): Map<String, Long> {
    val ids = mutableMapOf<String, Long>()
    ids["forumPML"] =
        forumDao.insertForum(
            ForumEntity(
                0,
                kelasPML,
                "Diskusi Error Jetpack Compose",
                "Tempat diskusi jika ada error saat development dengan Jetpack Compose.",
                oneMonthAgo,
            ),
        )
    ids["forumAI"] =
        forumDao.insertForum(
            ForumEntity(
                0,
                kelasAI,
                "Sharing Paper AI Terbaru",
                "Mari berbagi paper atau riset terbaru di bidang AI.",
                twoWeeksAgo,
            ),
        )
    ids["forumBDT"] =
        forumDao.insertForum(
            ForumEntity(
                0,
                kelasBDT,
                "Tanya Jawab Kuis Basis Data",
                "Diskusi soal-soal kuis basis data terdistribusi.",
                threeDaysAgo,
            ),
        )
    ids["forumKOMMAS"] =
        forumDao.insertForum(
            ForumEntity(
                0,
                kelasKOMMAS,
                "Ide Konten Kreatif",
                "Sharing ide untuk konten pemasaran yang menarik.",
                oneWeekAgo,
            ),
        )
    ids["forumPOLPEM"] =
        forumDao.insertForum(
            ForumEntity(
                0,
                kelasPOLPEM,
                "Diskusi Teori Pemerintahan",
                "Forum untuk membahas berbagai teori pemerintahan.",
                oneMonthAgo,
            ),
        )
    ids["forumKEBPUB"] =
        forumDao.insertForum(
            ForumEntity(
                0,
                kelasKEBPUB,
                "Studi Kasus Kebijakan Publik",
                "Membahas studi kasus kebijakan publik terkini.",
                twoWeeksAgo,
            ),
        )
    return ids
}
