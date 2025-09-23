package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.room.dao.ForumDao
import java.util.Date

internal suspend fun populateForumsAndGetIds(
    forumDao: ForumDao,
    coursePML: String,
    courseAI: String,
    courseBDT: String,
    courseKOMMAS: String,
    coursePOLPEM: String,
    courseKEBPUB: String,
    oneMonthAgo: Date,
    twoWeeksAgo: Date,
    threeDaysAgo: Date,
    oneWeekAgo: Date,
): Map<String, Long> {
    val ids = mutableMapOf<String, Long>()
    ids["forumPML"] =
        forumDao.insertForum(
            ForumEntity(
                0L,
                coursePML,
                "Diskusi Error Jetpack Compose",
                "Tempat diskusi jika ada error saat development dengan Jetpack Compose.",
                oneMonthAgo,
            ),
        )
    ids["forumAI"] =
        forumDao.insertForum(
            ForumEntity(
                0L,
                courseAI,
                "Sharing Paper AI Terbaru",
                "Mari berbagi paper atau riset terbaru di bidang AI.",
                twoWeeksAgo,
            ),
        )
    ids["forumBDT"] =
        forumDao.insertForum(
            ForumEntity(
                0L,
                courseBDT,
                "Tanya Jawab Kuis Basis Data",
                "Diskusi soal-soal kuis basis data terdistribusi.",
                threeDaysAgo,
            ),
        )
    ids["forumKOMMAS"] =
        forumDao.insertForum(
            ForumEntity(
                0L,
                courseKOMMAS,
                "Ide Konten Kreatif",
                "Sharing ide untuk konten pemasaran yang menarik.",
                oneWeekAgo,
            ),
        )
    ids["forumPOLPEM"] =
        forumDao.insertForum(
            ForumEntity(
                0L,
                coursePOLPEM,
                "Diskusi Teori Pemerintahan",
                "Forum untuk membahas berbagai teori pemerintahan.",
                oneMonthAgo,
            ),
        )
    ids["forumKEBPUB"] =
        forumDao.insertForum(
            ForumEntity(
                0L,
                courseKEBPUB,
                "Studi Kasus Kebijakan Publik",
                "Membahas studi kasus kebijakan publik terkini.",
                twoWeeksAgo,
            ),
        )
    return ids
}
