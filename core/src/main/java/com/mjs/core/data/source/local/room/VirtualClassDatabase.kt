package com.mjs.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.entity.AttendanceStreakEntity
import com.mjs.core.data.source.local.entity.DosenEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.KelasEntity
import com.mjs.core.data.source.local.entity.MahasiswaEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.room.dao.AttendanceDao
import com.mjs.core.data.source.local.room.dao.AuthDao
import com.mjs.core.data.source.local.room.dao.ClassroomDao
import com.mjs.core.data.source.local.room.dao.ForumDao
import com.mjs.core.data.source.local.room.dao.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Database(
    entities = [
        MahasiswaEntity::class, DosenEntity::class,
        KelasEntity::class, EnrollmentEntity::class, MaterialEntity::class,
        AssignmentEntity::class, SubmissionEntity::class, ForumEntity::class,
        PostEntity::class, AttendanceEntity::class, AttendanceStreakEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class VirtualClassDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao

    abstract fun classroomDao(): ClassroomDao

    abstract fun taskDao(): TaskDao

    abstract fun forumDao(): ForumDao

    abstract fun attendanceDao(): AttendanceDao

    @Suppress("DEPRECATION")
    class PrepopulateCallback(
        private val databaseProvider: () -> VirtualClassDatabase,
    ) : Callback() {
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch {
                populateDatabaseInternal(databaseProvider())
            }
        }

        suspend fun populateDatabaseInternal(database: VirtualClassDatabase) {
            val authDao = database.authDao()
            val classroomDao = database.classroomDao()
            val taskDao = database.taskDao()
            val forumDao = database.forumDao()
            val attendanceDao = database.attendanceDao()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val oneDayAgo =
                dateFormat.format(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
            val twoDaysAgo =
                dateFormat.format(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2)))
            val oneWeekAgo =
                dateFormat.format(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)))
            val inOneWeek =
                dateFormat.format(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))

            val dosen1Nidn = 1122334455
            val dosen2Nidn = 2000000002
            authDao.registerDosen(
                DosenEntity(
                    nidn = dosen1Nidn,
                    nama = "Dr. Amelia Rahman",
                    email = "amelia.rahman@example.com",
                    password = "password123",
                    fotoProfil = null,
                ),
            )
            authDao.registerDosen(
                DosenEntity(
                    nidn = dosen2Nidn,
                    nama = "Prof. Dr. Dian Kusuma",
                    email = "dian.kusuma@example.com",
                    password = "password456",
                    fotoProfil = null,
                ),
            )

            val mahasiswa1Nim = 21111073
            val mahasiswa2Nim = 21111074
            val mahasiswa3Nim = 21111075
            val mahasiswa4Nim = 21111076
            val mahasiswa5Nim = 21111077

            val mahasiswaData =
                listOf(
                    MahasiswaEntity(
                        mahasiswa1Nim,
                        "Muhammad Juzairi Safitli",
                        "airiagustus82@gmail.com",
                        "12345678",
                        null,
                        "Junaidi Surya, M.Kom.",
                    ),
                    MahasiswaEntity(
                        mahasiswa2Nim,
                        "Ahmad Kurniawan",
                        "ahmad.k@example.com",
                        "password111",
                        null,
                        "Ahmad Nawir Ihsam",
                    ),
                    MahasiswaEntity(
                        mahasiswa3Nim,
                        "Putri Lestari",
                        "putri.l@example.com",
                        "password222",
                        null,
                        "Budiyanto Siregar",
                    ),
                    MahasiswaEntity(
                        mahasiswa4Nim,
                        "Budi Prasetyo",
                        "budi.p@example.com",
                        "password333",
                        null,
                        "Ahmad Dahlan",
                    ),
                    MahasiswaEntity(
                        mahasiswa5Nim,
                        "Siti Nurhaliza",
                        "siti.n@example.com",
                        "password444",
                        null,
                        "Burton Suhagar",
                    ),
                )
            mahasiswaData.forEach { authDao.registerMahasiswa(it) }

            val kelas1Id = "PML23001"
            val kelas2Id = "BDT23002"
            val kelas3Id = "KCB23003"

            classroomDao.insertKelas(
                KelasEntity(
                    kelasId = kelas1Id,
                    namaKelas = "Pemrograman Mobile Lanjut",
                    deskripsi = "Mempelajari pengembangan aplikasi mobile Android dengan Kotlin dan Jetpack Compose.",
                    nidn = dosen1Nidn,
                    jadwal = "Senin, 10:00 - 12:30 WIB",
                    semester = "Ganjil 2023/2024",
                    credit = 4,
                    category = "Teknik Informatika",
                    classImage = null,
                    ruang = "Ruang 4.4",
                ),
            )
            classroomDao.insertKelas(
                KelasEntity(
                    kelasId = kelas2Id,
                    namaKelas = "Basis Data Terdistribusi",
                    deskripsi = "Konsep dan implementasi basis data terdistribusi, NoSQL, dan Big Data.",
                    nidn = dosen2Nidn,
                    jadwal = "Selasa, 13:00 - 15:30 WIB",
                    semester = "Ganjil 2023/2024",
                    credit = 4,
                    category = "Sistem Informasi",
                    classImage = null,
                    ruang = "Laboratorium Grafis",
                ),
            )
            classroomDao.insertKelas(
                KelasEntity(
                    kelasId = kelas3Id,
                    namaKelas = "Kecerdasan Buatan",
                    deskripsi = "Pengantar Kecerdasan Buatan, meliputi search, knowledge representation, dan machine learning.",
                    nidn = dosen1Nidn,
                    jadwal = "Rabu, 08:00 - 10:30 WIB",
                    semester = "Ganjil 2023/2024",
                    credit = 3,
                    category = "Teknik Informatika",
                    classImage = null,
                    ruang = "Ruang 3.2",
                ),
            )

            val calendar = Calendar.getInstance()
            val currentDayName = SimpleDateFormat("EEEE", Locale("id", "ID")).format(calendar.time)

            val todayClassNidn = dosen2Nidn
            val todayClassName = "Pemrograman Python"
            val todayClassId = "PYT23101"
            classroomDao.insertKelas(
                KelasEntity(
                    kelasId = todayClassId,
                    namaKelas = todayClassName,
                    deskripsi = "Belajar mengenai dasar-dasar pemrograman Python dan aplikasinya.",
                    nidn = todayClassNidn,
                    jadwal = "$currentDayName, 14:00 - 16:00 WIB",
                    semester = "Ganjil 2023/2024",
                    credit = 2,
                    category = "Teknik Informatika",
                    classImage = null,
                    ruang = "Aula Kampus",
                ),
            )

            val todayDosen1ClassName = "Etika Profesi TI"
            val todayDosen1ClassNidn = dosen1Nidn
            val todayDosen1ClassId = "EPT23102"
            classroomDao.insertKelas(
                KelasEntity(
                    kelasId = todayDosen1ClassId,
                    namaKelas = todayDosen1ClassName,
                    deskripsi = "Membahas etika dan profesionalisme dalam bidang Teknologi Informasi.",
                    nidn = todayDosen1ClassNidn,
                    jadwal = "$currentDayName, 09:00 - 10:40 WIB",
                    semester = "Ganjil 2023/2024",
                    credit = 2,
                    category = "Teknik Informatika",
                    classImage = null,
                    ruang = "Ruang Teori 1",
                ),
            )

            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa1Nim,
                    kelasId = kelas1Id,
                    tanggalDaftar = oneWeekAgo,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa2Nim,
                    kelasId = kelas1Id,
                    tanggalDaftar = oneWeekAgo,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa5Nim,
                    kelasId = kelas1Id,
                    tanggalDaftar = currentDate,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa1Nim,
                    kelasId = kelas2Id,
                    tanggalDaftar = oneWeekAgo,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa3Nim,
                    kelasId = kelas2Id,
                    tanggalDaftar = currentDate,
                    status = "Nonaktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa4Nim,
                    kelasId = kelas2Id,
                    tanggalDaftar = currentDate,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa2Nim,
                    kelasId = kelas3Id,
                    tanggalDaftar = oneDayAgo,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa4Nim,
                    kelasId = kelas3Id,
                    tanggalDaftar = currentDate,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa1Nim,
                    kelasId = todayClassId,
                    tanggalDaftar = currentDate,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa1Nim,
                    kelasId = todayDosen1ClassId,
                    tanggalDaftar = currentDate,
                    status = "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    nim = mahasiswa1Nim,
                    kelasId = kelas3Id,
                    tanggalDaftar = oneDayAgo,
                    status = "Aktif",
                ),
            )

            classroomDao.insertMaterial(
                MaterialEntity(
                    kelasId = kelas1Id,
                    judulMateri = "Pengenalan Kotlin untuk Android",
                    deskripsi = "Materi dasar...",
                    attachment = "https://example.com/materi/kotlin_dasar.pdf",
                    tanggalUpload = oneWeekAgo,
                    tipe = "pdf",
                ),
            )
            classroomDao.insertMaterial(
                MaterialEntity(
                    kelasId = kelas1Id,
                    judulMateri = "Jetpack Compose Layouting",
                    deskripsi = "Video tutorial...",
                    attachment = "https://youtube.com/watch?v=compose_layout_tutorial",
                    tanggalUpload = twoDaysAgo,
                    tipe = "video",
                ),
            )
            classroomDao.insertMaterial(
                MaterialEntity(
                    kelasId = kelas2Id,
                    judulMateri = "Konsep CAP Theorem dalam Basis Data",
                    deskripsi = "Penjelasan mendalam...",
                    attachment = "https://example.com/materi/cap_theorem.pdf",
                    tanggalUpload = oneWeekAgo,
                    tipe = "pdf",
                ),
            )

            val assignmentK1T1 =
                AssignmentEntity(
                    kelasId = kelas1Id,
                    judulTugas = "Tugas 1: Aplikasi Kalkulator Sederhana",
                    deskripsi = "Buatlah aplikasi kalkulator...",
                    tanggalMulai = oneWeekAgo,
                    tanggalSelesai = currentDate,
                    attachment = "https://example.com/tugas/kalkulator_compose.pdf",
                )
            // Mendapatkan ID setelah insert jika diperlukan untuk foreign key, atau handle dengan cara lain jika ID tidak auto-generate.
            // Untuk data dummy, kita akan mengasumsikan assignmentId yang berurutan jika kita memerlukannya nanti.
            // Namun, karena SubmissionEntity.assignmentId adalah Int dan AssignmentEntity.assignmentId adalah Int (autoGenerate),
            // kita akan menggunakan ID dummy yang telah ditetapkan sebelumnya.
            taskDao.insertAssignment(assignmentK1T1) // asumsikan insert mengembalikan Long (id)

            val assignmentK2T1 =
                AssignmentEntity(
                    kelasId = kelas2Id,
                    judulTugas = "Tugas 1: Desain Skema Basis Data E-commerce",
                    deskripsi = "Rancang skema basis data...",
                    tanggalMulai = oneWeekAgo,
                    tanggalSelesai = inOneWeek,
                    attachment = null,
                )
            taskDao.insertAssignment(assignmentK2T1)

            val assignmentTodayPython =
                AssignmentEntity(
                    kelasId = todayClassId,
                    judulTugas = "Tugas Python: Variabel dan Tipe Data",
                    deskripsi = "Jelaskan konsep variabel, tipe data, dan operator dalam Python dengan contoh.",
                    tanggalMulai = currentDate,
                    tanggalSelesai = inOneWeek,
                    attachment = null,
                )
            taskDao.insertAssignment(assignmentTodayPython)

            val assignmentTodayEtika =
                AssignmentEntity(
                    kelasId = todayDosen1ClassId,
                    judulTugas = "Studi Kasus Etika TI",
                    deskripsi = "Analisis sebuah studi kasus terkait pelanggaran etika dalam penggunaan Teknologi Informasi.",
                    tanggalMulai = currentDate,
                    tanggalSelesai = inOneWeek,
                    attachment = "https://example.com/kasus/etika_ti_studi.pdf",
                )
            taskDao.insertAssignment(assignmentTodayEtika)

            // Untuk Submission, kita membutuhkan assignmentId yang valid.
            // Idealnya, insertAssignment akan mengembalikan ID yang baru dibuat.
            // Untuk data dummy dan asumsi bahwa DAO mengembalikan ID:
            // Jika DAO tidak mengembalikan ID, maka kita harus menggunakan ID tetap (misalnya, dengan tidak menggunakan autoGenerate)
            // atau melakukan query untuk ID terakhir. Demi kesederhanaan, kita akan menggunakan ID dummy yang sudah ada.
            // Namun, lebih baik jika DAO mengembalikan ID.
            // Kita akan menggunakan ID dummy tetap untuk konsistensi dengan kode sebelumnya,
            // meskipun ini berarti assignmentId tidak di-link secara dinamis dari hasil insert di atas.
            val dummyAssignmentIdK1T1 = 1
            val dummyAssignmentIdK2T1 = 2
            val dummyAssignmentIdTodayPython = 3
            // val dummyAssignmentIdTodayEtika = 4 // (jika diperlukan)

            taskDao.insertSubmission(
                SubmissionEntity(
                    assignmentId = dummyAssignmentIdK1T1, // Seharusnya ID dari insertedAssignmentK1T1
                    nim = mahasiswa1Nim,
                    submissionDate = twoDaysAgo,
                    attachment = "https://example.com/submission/juzairi_kalkulator.zip",
                    grade = 90,
                    feedback = "Kerja bagus! UI sudah responsif dan fungsionalitas lengkap.",
                ),
            )
            taskDao.insertSubmission(
                SubmissionEntity(
                    assignmentId = dummyAssignmentIdK1T1, // Seharusnya ID dari insertedAssignmentK1T1
                    nim = mahasiswa2Nim,
                    submissionDate = oneDayAgo,
                    attachment = "https://example.com/submission/ahmad_kalkulator.zip",
                    grade = 85,
                    feedback = "Cukup baik, namun perhatikan penanganan error untuk pembagian dengan nol.",
                ),
            )
            taskDao.insertSubmission(
                SubmissionEntity(
                    assignmentId = dummyAssignmentIdK2T1, // Seharusnya ID dari insertedAssignmentK2T1
                    nim = mahasiswa1Nim,
                    submissionDate = currentDate,
                    attachment = "https://example.com/submission/juzairi_desain_db.txt",
                    grade = null,
                    feedback = null,
                ),
            )
            taskDao.insertSubmission(
                SubmissionEntity(
                    assignmentId = dummyAssignmentIdTodayPython, // Seharusnya ID dari insertedAssignmentTodayPython
                    nim = mahasiswa1Nim,
                    submissionDate = currentDate,
                    attachment = "https://example.com/submission/juzairi_python_dasar.zip",
                    grade = null,
                    feedback = null,
                ),
            )

            val forumK1F1 =
                ForumEntity(
                    kelasId = kelas1Id,
                    judulForum = "Diskusi Umum Pemrograman Mobile Lanjut",
                    deskripsi = "Tempat bertanya...",
                    tanggalDibuat = oneWeekAgo,
                )
            forumDao.insertForum(forumK1F1) // asumsikan insert mengembalikan Long (id)

            val forumK3F1 =
                ForumEntity(
                    kelasId = kelas3Id,
                    judulForum = "Tanya Jawab Seputar Kecerdasan Buatan",
                    deskripsi = "Forum diskusi...",
                    tanggalDibuat = twoDaysAgo,
                )
            forumDao.insertForum(forumK3F1)

            // Sama seperti Assignment, lebih baik menggunakan ID yang dikembalikan oleh DAO.
            // Untuk konsistensi dengan kode sebelumnya, kita pakai ID dummy.
            val dummyForumIdK1F1 = 1
            val dummyForumIdK3F1 = 2

            forumDao.insertPost(
                PostEntity(
                    forumId = dummyForumIdK1F1, // Seharusnya ID dari insertedForumK1F1
                    userId = dosen1Nidn,
                    userRole = "dosen",
                    isiPost = "Selamat datang di forum diskusi...",
                    tanggalPost = oneWeekAgo,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    forumId = dummyForumIdK1F1, // Seharusnya ID dari insertedForumK1F1
                    userId = mahasiswa1Nim,
                    userRole = "mahasiswa",
                    isiPost = "Terima kasih, Bu Amelia. Saya ingin bertanya...",
                    tanggalPost = twoDaysAgo,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    forumId = dummyForumIdK1F1,
                    userId = dosen1Nidn,
                    userRole = "dosen",
                    isiPost = "Pertanyaan bagus, @${mahasiswaData.find { it.nim == mahasiswa1Nim }?.nama}. `remember` akan...",
                    tanggalPost = oneDayAgo,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    forumId = dummyForumIdK1F1, // Seharusnya ID dari insertedForumK1F1
                    userId = mahasiswa2Nim,
                    userRole = "mahasiswa",
                    isiPost = "Saya masih bingung dengan implementasi Navigation Component...",
                    tanggalPost = currentDate,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    forumId = dummyForumIdK3F1, // Seharusnya ID dari insertedForumK3F1
                    userId = dosen1Nidn,
                    userRole = "dosen",
                    isiPost = "Mari kita diskusikan implementasi algoritma A*...",
                    tanggalPost = twoDaysAgo,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    forumId = dummyForumIdK3F1, // Seharusnya ID dari insertedForumK3F1
                    userId = mahasiswa2Nim,
                    userRole = "mahasiswa",
                    isiPost = "Algoritma A* menggunakan fungsi heuristik...",
                    tanggalPost = oneDayAgo,
                ),
            )

            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas1Id,
                    nim = mahasiswa1Nim,
                    tanggalHadir = oneWeekAgo,
                    status = "Hadir",
                    keterangan = null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas1Id,
                    nim = mahasiswa1Nim,
                    tanggalHadir = twoDaysAgo,
                    status = "Hadir",
                    keterangan = null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas1Id,
                    nim = mahasiswa2Nim,
                    tanggalHadir = oneWeekAgo,
                    status = "Hadir",
                    keterangan = null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas1Id,
                    nim = mahasiswa2Nim,
                    tanggalHadir = twoDaysAgo,
                    status = "Izin",
                    keterangan = "Acara keluarga mendadak.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas1Id,
                    nim = mahasiswa5Nim,
                    tanggalHadir = currentDate,
                    status = "Hadir",
                    keterangan = null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas2Id,
                    nim = mahasiswa1Nim,
                    tanggalHadir = oneWeekAgo,
                    status = "Hadir",
                    keterangan = null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = kelas2Id,
                    nim = mahasiswa4Nim,
                    tanggalHadir = currentDate,
                    status = "Hadir",
                    keterangan = null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = todayClassId,
                    nim = mahasiswa1Nim,
                    tanggalHadir = currentDate,
                    status = "Hadir",
                    keterangan = "Mengikuti kelas Python hari ini.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    kelasId = todayDosen1ClassId,
                    nim = mahasiswa1Nim,
                    tanggalHadir = currentDate,
                    status = "Hadir",
                    keterangan = "Mengikuti kelas Etika TI hari ini.",
                ),
            )

            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa1Nim.toString(), // Diubah ke String
                    kelasId = kelas1Id,
                    currentStreak = 2,
                    longestStreak = 5,
                    lastAttendedDate = twoDaysAgo,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa2Nim.toString(), // Diubah ke String
                    kelasId = kelas1Id,
                    currentStreak = 0,
                    longestStreak = 3,
                    lastAttendedDate = oneWeekAgo,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa5Nim.toString(), // Diubah ke String
                    kelasId = kelas1Id,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastAttendedDate = currentDate,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa1Nim.toString(), // Diubah ke String
                    kelasId = kelas2Id,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastAttendedDate = oneWeekAgo,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa4Nim.toString(), // Diubah ke String
                    kelasId = kelas2Id,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastAttendedDate = currentDate,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa1Nim.toString(), // Diubah ke String
                    kelasId = todayClassId,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastAttendedDate = currentDate,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    nim = mahasiswa1Nim.toString(), // Diubah ke String
                    kelasId = todayDosen1ClassId,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastAttendedDate = currentDate,
                ),
            )
        }
    }
}
