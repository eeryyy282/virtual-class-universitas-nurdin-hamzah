package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.room.dao.ForumDao
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal suspend fun populatePosts(
    forumDao: ForumDao,
    forumPMLId: Long,
    forumAIId: Long,
    forumBDTId: Long,
    forumKOMMASId: Long,
    forumPOLPEMId: Long,
    forumKEBPUBId: Long,
    dosen1Nidn: Int,
    dosen2Nidn: Int,
    dosen4Nidn: Int,
    dosen5Nidn: Int,
    mhs1Nim: Int,
    mhs2Nim: Int,
    mhs3Nim: Int,
    mhs4Nim: Int,
    mhs5Nim: Int,
    mhs6Nim: Int,
    roleDosen: String,
    roleMahasiswa: String,
    oneMonthAgo: String,
    twoWeeksAgo: String,
    threeDaysAgo: String,
    oneWeekAgo: String,
    currentDate: String,
    yesterday: String,
) {
    // Forum PML
    forumDao.insertPost(
        PostEntity(
            0,
            forumPMLId.toInt(),
            dosen1Nidn,
            roleDosen,
            "Selamat datang di forum diskusi Jetpack Compose. Silakan bertanya jika ada kendala.",
            oneMonthAgo,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumPMLId.toInt(),
            mhs3Nim,
            roleMahasiswa,
            "Saya mengalami error 'XYZ' saat menjalankan preview, ada solusi?",
            twentyDaysAgoFormat(),
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumPMLId.toInt(),
            mhs1Nim,
            roleMahasiswa,
            "Coba cek versi library Compose dan Android Studio nya mas, mungkin tidak kompatibel.",
            nineteenDaysAgoFormat(),
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumPMLId.toInt(),
            dosen1Nidn,
            roleDosen,
            "@Budi Hartono, bisa lampirkan screenshot errornya?",
            eighteenDaysAgoFormat(),
        ),
    )
    // Forum AI
    forumDao.insertPost(
        PostEntity(
            0,
            forumAIId.toInt(),
            dosen2Nidn,
            roleDosen,
            "Ada yang sudah membaca tentang Transformer model terbaru? Bagaimana implikasinya di computer vision?",
            twoWeeksAgo,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumAIId.toInt(),
            mhs3Nim,
            roleMahasiswa,
            "Saya baru baca sekilas Prof, sepertinya menarik untuk NLP dan CV.",
            tenDaysAgoFormat(),
        ),
    )
    // Forum BDT
    forumDao.insertPost(
        PostEntity(
            0,
            forumBDTId.toInt(),
            mhs1Nim,
            roleMahasiswa,
            "Untuk soal nomor 5 kuis kemarin, apakah ada yang punya jawaban berbeda?",
            threeDaysAgo,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumBDTId.toInt(),
            mhs2Nim,
            roleMahasiswa,
            "Saya juga agak bingung di soal itu, terutama bagian konsistensi data.",
            yesterday,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumBDTId.toInt(),
            dosen2Nidn,
            roleDosen,
            "Akan kita bahas di pertemuan berikutnya ya.",
            currentDate,
        ),
    )
    // Forum KOMMAS
    forumDao.insertPost(
        PostEntity(
            0,
            forumKOMMASId.toInt(),
            mhs4Nim,
            roleMahasiswa,
            "Bagaimana cara membuat konten video pendek yang viral ya?",
            oneWeekAgo,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumKOMMASId.toInt(),
            dosen4Nidn,
            roleDosen,
            "Fokus pada tren, musik yang sedang populer, dan storytelling yang singkat namun mengena.",
            sixDaysAgoFormat(),
        ),
    )
    // Forum POLPEM
    forumDao.insertPost(
        PostEntity(
            0,
            forumPOLPEMId.toInt(),
            dosen4Nidn,
            roleDosen,
            "Mari kita diskusikan relevansi teori negara integralistik dalam konteks Indonesia saat ini.",
            oneMonthAgo,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumPOLPEMId.toInt(),
            mhs5Nim,
            roleMahasiswa,
            "Menurut saya masih relevan Bu, terutama dalam menjaga persatuan.",
            twentyDaysAgoFormat(),
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumPOLPEMId.toInt(),
            mhs6Nim,
            roleMahasiswa,
            "Saya setuju dengan Eko, tapi bagaimana dengan potensi sentralisasi kekuasaan?",
            nineteenDaysAgoFormat(),
        ),
    )
    // Forum KEBPUB
    forumDao.insertPost(
        PostEntity(
            0,
            forumKEBPUBId.toInt(),
            dosen5Nidn,
            roleDosen,
            "Silakan share temuan menarik dari analisis kebijakan yang sudah kalian baca.",
            twoWeeksAgo,
        ),
    )
    forumDao.insertPost(
        PostEntity(
            0,
            forumKEBPUBId.toInt(),
            mhs5Nim,
            roleMahasiswa,
            "Saya menemukan studi kasus menarik tentang partisipasi publik dalam kebijakan di negara Skandinavia.",
            tenDaysAgoFormat(),
        ),
    )
}

private fun twentyDaysAgoFormat(): String {
    val cal = Calendar.getInstance()
    cal.add(
        Calendar.DAY_OF_YEAR,
        -20,
    )
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(cal.time)
}

private fun nineteenDaysAgoFormat(): String {
    val cal = Calendar.getInstance()
    cal.add(
        Calendar.DAY_OF_YEAR,
        -19,
    )
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(cal.time)
}

private fun eighteenDaysAgoFormat(): String {
    val cal = Calendar.getInstance()
    cal.add(
        Calendar.DAY_OF_YEAR,
        -18,
    )
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(cal.time)
}

private fun tenDaysAgoFormat(): String {
    val cal = Calendar.getInstance()
    cal.add(
        Calendar.DAY_OF_YEAR,
        -10,
    )
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(cal.time)
}

private fun sixDaysAgoFormat(): String {
    val cal = Calendar.getInstance()
    cal.add(
        Calendar.DAY_OF_YEAR,
        -6,
    )
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(cal.time)
}
