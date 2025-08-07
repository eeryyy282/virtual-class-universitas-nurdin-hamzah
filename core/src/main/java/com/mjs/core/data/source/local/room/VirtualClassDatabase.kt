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

            val dosen1Nidn = "1122334455"
            val dosen2Nidn = "5544332211"
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

            val mahasiswa1Nim = "21111073"
            val mahasiswa2Nim = "21111074"
            val mahasiswa3Nim = "21111075"
            val mahasiswa4Nim = "21111076"
            val mahasiswa5Nim = "21111077"

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

            classroomDao.insertKelas(
                KelasEntity(
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

            classroomDao.insertKelas(
                KelasEntity(
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
            val todayClassId =
                classroomDao.getKelasIdByNameAndNidn(todayClassName, todayClassNidn) ?: 0

            val todayDosen1ClassName = "Etika Profesi TI"
            classroomDao.insertKelas(
                KelasEntity(
                    namaKelas = todayDosen1ClassName,
                    deskripsi = "Membahas etika dan profesionalisme dalam bidang Teknologi Informasi.",
                    nidn = dosen1Nidn,
                    jadwal = "$currentDayName, 09:00 - 10:40 WIB",
                    semester = "Ganjil 2023/2024",
                    credit = 2,
                    category = "Teknik Informatika",
                    classImage = null,
                    ruang = "Ruang Teori 1",
                ),
            )

            val kelas1Id =
                classroomDao.getKelasIdByNameAndNidn("Pemrograman Mobile Lanjut", dosen1Nidn) ?: 0
            val kelas2Id =
                classroomDao.getKelasIdByNameAndNidn("Basis Data Terdistribusi", dosen2Nidn) ?: 0
            val kelas3Id =
                classroomDao.getKelasIdByNameAndNidn("Kecerdasan Buatan", dosen1Nidn) ?: 0

            if (kelas1Id != 0) {
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
            }
            if (kelas2Id != 0) {
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
            }
            if (kelas3Id != 0) {
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
            }

            if (todayClassId != 0) {
                classroomDao.insertEnrollment(
                    EnrollmentEntity(
                        nim = mahasiswa1Nim,
                        kelasId = todayClassId,
                        tanggalDaftar = currentDate,
                        status = "Aktif",
                    ),
                )
            }

            if (kelas1Id != 0) {
                classroomDao.insertMaterial(
                    MaterialEntity(
                        kelasId = kelas1Id,
                        judulMateri = "Pengenalan Kotlin untuk Android",
                        deskripsi = "Materi dasar mengenai bahasa pemrograman Kotlin dan penggunaannya dalam pengembangan Android.",
                        attachment = "https://example.com/materi/kotlin_dasar.pdf",
                        tanggalUpload = oneWeekAgo,
                        tipe = "pdf",
                    ),
                )
                classroomDao.insertMaterial(
                    MaterialEntity(
                        kelasId = kelas1Id,
                        judulMateri = "Jetpack Compose Layouting",
                        deskripsi = "Video tutorial mengenai cara membuat layout dinamis dengan Jetpack Compose.",
                        attachment = "https://youtube.com/watch?v=compose_layout_tutorial",
                        tanggalUpload = twoDaysAgo,
                        tipe = "video",
                    ),
                )
            }
            if (kelas2Id != 0) {
                classroomDao.insertMaterial(
                    MaterialEntity(
                        kelasId = kelas2Id,
                        judulMateri = "Konsep CAP Theorem dalam Basis Data",
                        deskripsi = "Penjelasan mendalam mengenai CAP Theorem dan implikasinya dalam sistem basis data terdistribusi.",
                        attachment = "https://example.com/materi/cap_theorem.pdf",
                        tanggalUpload = oneWeekAgo,
                        tipe = "pdf",
                    ),
                )
            }

            var assignment1K1Id = 0
            var assignment1K2Id = 0
            if (kelas1Id != 0) {
                taskDao.insertAssignment(
                    AssignmentEntity(
                        kelasId = kelas1Id,
                        judulTugas = "Tugas 1: Aplikasi Kalkulator Sederhana",
                        deskripsi =
                            "Buatlah aplikasi kalkulator sederhana menggunakan Kotlin dan " +
                                "Jetpack Compose yang dapat melakukan operasi dasar (tambah, kurang, kali, bagi).",
                        tanggalMulai = oneWeekAgo,
                        tanggalSelesai = currentDate,
                        attachment = "https://example.com/tugas/kalkulator_compose.pdf",
                    ),
                )
                assignment1K1Id = taskDao.getAssignmentIdByTitleAndClassId(
                    "Tugas 1: Aplikasi Kalkulator Sederhana",
                    kelas1Id,
                ) ?: 0
            }
            if (kelas2Id != 0) {
                taskDao.insertAssignment(
                    AssignmentEntity(
                        kelasId = kelas2Id,
                        judulTugas = "Tugas 1: Desain Skema Basis Data E-commerce",
                        deskripsi =
                            "Rancang skema basis data relasional untuk platform e-commerce" +
                                " sederhana. Sertakan entitas, atribut, dan relasinya.",
                        tanggalMulai = oneWeekAgo,
                        tanggalSelesai = inOneWeek,
                        attachment = null,
                    ),
                )
                assignment1K2Id =
                    taskDao.getAssignmentIdByTitleAndClassId(
                        "Tugas 1: Desain Skema Basis Data E-commerce",
                        kelas2Id,
                    )
                        ?: 0
            }

            if (assignment1K1Id != 0) {
                taskDao.insertSubmission(
                    SubmissionEntity(
                        assignmentId = assignment1K1Id,
                        nim = mahasiswa1Nim,
                        submissionDate = twoDaysAgo,
                        attachment = "https://example.com/submission/juzairi_kalkulator.zip",
                        grade = 90,
                        feedback = "Kerja bagus! UI sudah responsif dan fungsionalitas lengkap.",
                    ),
                )
                taskDao.insertSubmission(
                    SubmissionEntity(
                        assignmentId = assignment1K1Id,
                        nim = mahasiswa2Nim,
                        submissionDate = oneDayAgo,
                        attachment = "https://example.com/submission/ahmad_kalkulator.zip",
                        grade = 85,
                        feedback = "Cukup baik, namun perhatikan penanganan error untuk pembagian dengan nol.",
                    ),
                )
            }
            if (assignment1K2Id != 0) {
                taskDao.insertSubmission(
                    SubmissionEntity(
                        assignmentId = assignment1K2Id,
                        nim = mahasiswa1Nim,
                        submissionDate = currentDate,
                        attachment = "https://example.com/submission/juzairi_desain_db.txt",
                        grade = null,
                        feedback = null,
                    ),
                )
            }

            var forum1K1Id = 0
            if (kelas1Id != 0) {
                forumDao.insertForum(
                    ForumEntity(
                        kelasId = kelas1Id,
                        judulForum = "Diskusi Umum Pemrograman Mobile Lanjut",
                        deskripsi = "Tempat bertanya dan berdiskusi seputar materi Pemrograman Mobile Lanjut yang belum dipahami.",
                        tanggalDibuat = oneWeekAgo,
                    ),
                )
                forum1K1Id = forumDao.getForumIdByTitleAndClassId(
                    "Diskusi Umum Pemrograman Mobile Lanjut",
                    kelas1Id,
                ) ?: 0
            }
            var forum1K3Id = 0
            if (kelas3Id != 0) {
                forumDao.insertForum(
                    ForumEntity(
                        kelasId = kelas3Id,
                        judulForum = "Tanya Jawab Seputar Kecerdasan Buatan",
                        deskripsi = "Forum diskusi untuk materi dan implementasi Kecerdasan Buatan.",
                        tanggalDibuat = twoDaysAgo,
                    ),
                )
                forum1K3Id =
                    forumDao.getForumIdByTitleAndClassId(
                        "Tanya Jawab Seputar Kecerdasan Buatan",
                        kelas3Id,
                    ) ?: 0
            }

            if (forum1K1Id != 0) {
                forumDao.insertPost(
                    PostEntity(
                        forumId = forum1K1Id,
                        userId = dosen1Nidn,
                        userRole = "dosen",
                        isiPost =
                            "Selamat datang di forum diskusi kelas Pemrograman Mobile Lanjut! " +
                                "Silakan ajukan pertanyaan jika ada materi yang kurang jelas atau ingin berdiskusi lebih lanjut.",
                        tanggalPost = oneWeekAgo,
                    ),
                )
                forumDao.insertPost(
                    PostEntity(
                        forumId = forum1K1Id,
                        userId = mahasiswa1Nim,
                        userRole = "mahasiswa",
                        isiPost =
                            "Terima kasih, Bu Amelia. Saya ingin bertanya lebih lanjut mengenai " +
                                "state management di Jetpack Compose, khususnya perbedaan antara `remember` dan `rememberSaveable`.",
                        tanggalPost = twoDaysAgo,
                    ),
                )
                forumDao.insertPost(
                    PostEntity(
                        forumId = forum1K1Id,
                        userId = dosen1Nidn,
                        userRole = "dosen",
                        isiPost =
                            "Pertanyaan bagus, @$mahasiswa1Nim. `remember` akan menyimpan state selama" +
                                " composable masih dalam komposisi dan tidak akan bertahan saat terjadi re-creation (misalnya rotasi layar). Sedangkan `rememberSaveable` akan menyimpan state bahkan saat terjadi re-creation, karena ia menyimpan data dalam Bundle. Gunakan `rememberSaveable` untuk state yang penting untuk dipertahankan.",
                        tanggalPost = oneDayAgo,
                    ),
                )
                forumDao.insertPost(
                    PostEntity(
                        forumId = forum1K1Id,
                        userId = mahasiswa2Nim,
                        userRole = "mahasiswa",
                        isiPost =
                            "Saya masih bingung dengan implementasi Navigation Component di " +
                                "Jetpack Compose. Apakah ada contoh sederhana yang bisa dibagikan?",
                        tanggalPost = currentDate,
                    ),
                )
            }
            if (forum1K3Id != 0) {
                forumDao.insertPost(
                    PostEntity(
                        forumId = forum1K3Id,
                        userId = dosen1Nidn,
                        userRole = "dosen",
                        isiPost =
                            "Mari kita diskusikan implementasi algoritma A* untuk pencarian" +
                                " jalur. Siapa yang bisa menjelaskan konsep dasarnya?",
                        tanggalPost = twoDaysAgo,
                    ),
                )
                forumDao.insertPost(
                    PostEntity(
                        forumId = forum1K3Id,
                        userId = mahasiswa2Nim,
                        userRole = "mahasiswa",
                        isiPost =
                            "Algoritma A* menggunakan fungsi heuristik untuk memperkirakan " +
                                "biaya dari node saat ini ke tujuan, dikombinasikan dengan biaya aktual dari awal ke node saat ini (g(n) + h(n)).",
                        tanggalPost = oneDayAgo,
                    ),
                )
            }

            if (kelas1Id != 0) {
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
            }
            if (kelas2Id != 0) {
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
            }

            if (kelas1Id != 0) {
                attendanceDao.updateAttendanceStreak(
                    AttendanceStreakEntity(
                        nim = mahasiswa1Nim,
                        kelasId = kelas1Id,
                        currentStreak = 2,
                        longestStreak = 5,
                        lastAttendedDate = twoDaysAgo,
                    ),
                )
                attendanceDao.updateAttendanceStreak(
                    AttendanceStreakEntity(
                        nim = mahasiswa2Nim,
                        kelasId = kelas1Id,
                        currentStreak = 0,
                        longestStreak = 3,
                        lastAttendedDate = oneWeekAgo,
                    ),
                )
                attendanceDao.updateAttendanceStreak(
                    AttendanceStreakEntity(
                        nim = mahasiswa5Nim,
                        kelasId = kelas1Id,
                        currentStreak = 1,
                        longestStreak = 1,
                        lastAttendedDate = currentDate,
                    ),
                )
            }
            if (kelas2Id != 0) {
                attendanceDao.updateAttendanceStreak(
                    AttendanceStreakEntity(
                        nim = mahasiswa1Nim,
                        kelasId = kelas2Id,
                        currentStreak = 1,
                        longestStreak = 1,
                        lastAttendedDate = oneWeekAgo,
                    ),
                )
                attendanceDao.updateAttendanceStreak(
                    AttendanceStreakEntity(
                        nim = mahasiswa4Nim,
                        kelasId = kelas2Id,
                        currentStreak = 1,
                        longestStreak = 1,
                        lastAttendedDate = currentDate,
                    ),
                )
            }
        }
    }
}
