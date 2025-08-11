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
            val calendar = Calendar.getInstance()

            fun getDate(
                timeUnit: Int,
                amount: Int,
                referenceDate: Date = Date(),
            ): String {
                val cal = Calendar.getInstance()
                cal.time = referenceDate
                cal.add(timeUnit, amount)
                return dateFormat.format(cal.time)
            }

            val today = Date()
            val currentDate = getDate(Calendar.DAY_OF_YEAR, 0, today)
            val yesterday = getDate(Calendar.DAY_OF_YEAR, -1, today)
            val threeDaysAgo = getDate(Calendar.DAY_OF_YEAR, -3, today)
            val oneWeekAgo = getDate(Calendar.DAY_OF_YEAR, -7, today)
            val twoWeeksAgo = getDate(Calendar.DAY_OF_YEAR, -14, today)
            val oneMonthAgo = getDate(Calendar.MONTH, -1, today)
            getDate(Calendar.MONTH, -2, today)

            val tomorrow = getDate(Calendar.DAY_OF_YEAR, 1, today)
            val inThreeDays = getDate(Calendar.DAY_OF_YEAR, 3, today)
            val inOneWeek = getDate(Calendar.DAY_OF_YEAR, 7, today)
            val inTwoWeeks = getDate(Calendar.DAY_OF_YEAR, 14, today)
            val inOneMonth = getDate(Calendar.MONTH, 1, today)
            getDate(Calendar.MONTH, 2, today)

            val roleDosen = "dosen"
            val roleMahasiswa = "mahasiswa"

            val dosen1Nidn = 1122334455 // Dr. Arini Larasati
            val dosen2Nidn = 2233445566.toInt() // Prof. Bambang Wijaya
            val dosen3Nidn = 3344556677.toInt() // Chandra Kusuma, M.Sc.
            authDao.registerDosen(
                DosenEntity(
                    dosen1Nidn,
                    "Dr. Arini Larasati, S.Kom., M.Cs.",
                    "arini.larasati@example.ac.id",
                    "pass1122",
                    "https://images.unsplash.com/photo-1573496799652-408c2ac9fe98?w=400",
                ),
            )
            authDao.registerDosen(
                DosenEntity(
                    dosen2Nidn,
                    "Prof. Bambang Wijaya, Ph.D.",
                    "bambang.wijaya@example.ac.id",
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

            val mhs1Nim = 21111073 // Muhammad Juzairi Safitli (Sangat Aktif)
            val mhs2Nim = 22021002 // Yuni Azira (Aktif)
            val mhs3Nim = 22032003 // Budi Hartono (Cukup Aktif)
            val mhs4Nim = 23041004 // Rina Amelia (Kurang Aktif)
            val mhs5Nim = 23052005 // Eko Prasetyo (Baru Mendaftar)
            authDao.registerMahasiswa(
                MahasiswaEntity(
                    mhs1Nim,
                    "Muhammad Juzairi Safitli",
                    "juzairi.safitli@student.example.ac.id",
                    "pass2111",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                    "Dr. Arini Larasati, S.Kom., M.Cs.",
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
                ),
            )
            authDao.registerMahasiswa(
                MahasiswaEntity(
                    mhs5Nim,
                    "Eko Prasetyo",
                    "eko.prasetyo@student.example.ac.id",
                    "pass2305",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
                    "Prof. Bambang Wijaya, Ph.D.",
                ),
            )

            val currentDayName = SimpleDateFormat("EEEE", Locale("id", "ID")).format(calendar.time)
            val kelasPML = "IF-401"
            val kelasBDT = "SI-302"
            val kelasAI = "IF-503"
            val kelasWEB = "IF-305"
            val kelasSTAT = "DS-201"
            val kelasPYTHON = "UM-101"
            val kelasETIKA = "UM-102"
            val placeholderImageUrl =
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400"

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
                    kelasAI,
                    "Kecerdasan Buatan Lanjutan",
                    "Pembahasan mendalam tentang algoritma machine learning, deep learning, natural language processing, dan computer vision.",
                    dosen1Nidn,
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
                    dosen3Nidn,
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
                    kelasSTAT,
                    "Statistika Dasar untuk Sains Data",
                    "Pengantar konsep statistika deskriptif dan inferensial yang esensial untuk analisis data.",
                    dosen2Nidn,
                    "Jumat, 09:00 - 11:30 WIB",
                    "Semester Ganjil 2024/2025",
                    3,
                    "Sains Data",
                    "https://images.unsplash.com/photo-1504868584819-f8e8b4b6d7e3?w=400",
                    "Ruang Tutorial B205",
                ),
            )
            classroomDao.insertKelas(
                KelasEntity(
                    kelasPYTHON,
                    "Workshop Pemrograman Python",
                    "Sesi intensif satu hari untuk mempelajari dasar-dasar Python dan aplikasinya dalam skrip sederhana.",
                    dosen3Nidn,
                    "$currentDayName, 09:00 - 16:00 WIB",
                    "Workshop Mingguan",
                    1,
                    "Umum",
                    placeholderImageUrl,
                    "Aula Utama",
                ),
            )
            classroomDao.insertKelas(
                KelasEntity(
                    kelasETIKA,
                    "Seminar Etika Digital",
                    "Diskusi mengenai tantangan etis dalam penggunaan teknologi digital, privasi data, dan tanggung jawab siber.",
                    dosen1Nidn,
                    "$currentDayName, 13:00 - 15:00 WIB",
                    "Seminar Bulanan",
                    0,
                    "Umum",
                    placeholderImageUrl,
                    "Ruang Seminar Lt. 5",
                ),
            )

            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs1Nim,
                    kelasPML,
                    oneMonthAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs1Nim,
                    kelasAI,
                    oneMonthAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs1Nim,
                    kelasPYTHON,
                    oneWeekAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs1Nim,
                    kelasETIKA,
                    oneWeekAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs2Nim,
                    kelasPML,
                    oneMonthAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs2Nim,
                    kelasBDT,
                    oneMonthAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs2Nim,
                    kelasSTAT,
                    twoWeeksAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs3Nim,
                    kelasBDT,
                    oneMonthAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs3Nim,
                    kelasWEB,
                    twoWeeksAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs3Nim,
                    kelasPYTHON,
                    threeDaysAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs4Nim,
                    kelasPML,
                    oneMonthAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs4Nim,
                    kelasWEB,
                    oneMonthAgo,
                    "Nonaktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs4Nim,
                    kelasETIKA,
                    oneWeekAgo,
                    "Aktif",
                ),
            )
            classroomDao.insertEnrollment(
                EnrollmentEntity(
                    0,
                    mhs5Nim,
                    kelasPML,
                    threeDaysAgo,
                    "Aktif",
                ),
            )

            classroomDao.insertMaterial(
                MaterialEntity(
                    0,
                    kelasPML,
                    "Slide 1: Pengantar Kotlin & Android Studio",
                    "Materi presentasi untuk pertemuan pertama, mencakup setup environment dan dasar Kotlin.",
                    oneMonthAgo,
                    "application/pdf",
                    "PML_S01_Intro_Kotlin_Android_Studio.pdf",
                ),
            )
            classroomDao.insertMaterial(
                MaterialEntity(
                    0,
                    kelasPML,
                    "Video Tutorial: Layouting dengan Jetpack Compose",
                    "Link ke playlist video tutorial mengenai berbagai teknik layouting di Jetpack Compose.",
                    twoWeeksAgo,
                    "text/html",
                    "https://youtube.com/playlist?list=PML_Compose_Layouts",
                ),
            )
            classroomDao.insertMaterial(
                MaterialEntity(
                    0,
                    kelasPML,
                    "Contoh Kode: Navigasi & State Management",
                    "Repository GitHub berisi contoh implementasi navigasi dan state management sederhana.",
                    oneWeekAgo,
                    "application/zip",
                    "PML_Example_NavState.zip",
                ),
            )
            classroomDao.insertMaterial(
                MaterialEntity(
                    0,
                    kelasBDT,
                    "Jurnal: CAP Theorem Revisited",
                    "Artikel ilmiah terbaru yang membahas relevansi CAP Theorem dalam arsitektur modern.",
                    threeDaysAgo,
                    "application/pdf",
                    "BDT_Jurnal_CAP_Revisited.pdf",
                ),
            )
            classroomDao.insertMaterial(
                MaterialEntity(
                    0,
                    kelasPYTHON,
                    "Modul Workshop Python",
                    "Modul praktikum lengkap untuk workshop pemrograman Python.",
                    yesterday,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "Workshop_Python_Modul.docx",
                ),
            )

            val assignmentPML1 = 1
            taskDao.insertAssignment(
                AssignmentEntity(
                    assignmentPML1,
                    kelasPML,
                    "Tugas 1: Aplikasi Kalkulator Kustom",
                    "Rancang dan implementasikan aplikasi kalkulator dengan tema visual kustom menggunakan Jetpack Compose. Fitur minimal: +, -, *, /.",
                    oneWeekAgo,
                    inOneWeek,
                    "Spek_Tugas1_Kalkulator_PML.pdf",
                ),
            )
            val assignmentBDT1 = 2
            taskDao.insertAssignment(
                AssignmentEntity(
                    assignmentBDT1,
                    kelasBDT,
                    "Studi Kasus: Desain Partisi Database E-commerce",
                    "Analisis kebutuhan dan rancang strategi partisi database untuk platform e-commerce skala besar.",
                    oneWeekAgo,
                    inThreeDays,
                    null,
                ),
            )
            val assignmentAI1 = 3
            taskDao.insertAssignment(
                AssignmentEntity(
                    assignmentAI1,
                    kelasAI,
                    "Implementasi Algoritma KNN",
                    "Implementasikan algoritma K-Nearest Neighbors untuk klasifikasi dataset Iris dari awal (tanpa library ML).",
                    twoWeeksAgo,
                    threeDaysAgo,
                    "Dataset_Iris_KNN.csv",
                ),
            )
            val assignmentPYTHON1 = 4
            taskDao.insertAssignment(
                AssignmentEntity(
                    assignmentPYTHON1,
                    kelasPYTHON,
                    "Proyek Mini: Web Scraper Sederhana",
                    "Buat skrip Python untuk melakukan scraping data judul berita dari satu halaman web berita.",
                    currentDate,
                    inTwoWeeks,
                    "Panduan_Proyek_WebScraper.txt",
                ),
            )
            val assignmentPML2 = 5
            taskDao.insertAssignment(
                AssignmentEntity(
                    assignmentPML2,
                    kelasPML,
                    "Kuis Pemahaman State Hoisting",
                    "Jawab pertanyaan kuis singkat mengenai konsep state hoisting di Jetpack Compose.",
                    threeDaysAgo,
                    tomorrow,
                    null,
                ),
            )
            val assignmentWEB1 = 6
            taskDao.insertAssignment(
                AssignmentEntity(
                    assignmentWEB1,
                    kelasWEB,
                    "Pengembangan API CRUD Sederhana",
                    "Buat API RESTful menggunakan Node.js dan Express untuk operasi CRUD pada entitas 'Produk'.",
                    oneWeekAgo,
                    inOneMonth,
                    "Spesifikasi_API_Produk_WEB.json",
                ),
            )

            taskDao.insertSubmission(
                SubmissionEntity(
                    0,
                    assignmentPML1,
                    mhs2Nim,
                    "YuniAzira_KalkulatorPML.zip",
                    currentDate,
                    0,
                    null,
                ),
            )

            taskDao.insertSubmission(
                SubmissionEntity(
                    0,
                    assignmentAI1,
                    mhs1Nim,
                    "Juzairi_KNN_AI_Late.py",
                    yesterday,
                    75,
                    "Implementasi cukup baik, namun terlambat 2 hari.",
                ),
            )
            taskDao.insertSubmission(
                SubmissionEntity(
                    0,
                    assignmentPML2,
                    mhs5Nim,
                    "Eko_KuisPML.txt",
                    currentDate,
                    90,
                    "Pemahaman yang baik sekali!",
                ),
            )

            val forumPMLTugas1 = 1
            forumDao.insertForum(
                ForumEntity(
                    forumPMLTugas1,
                    kelasPML,
                    "Diskusi & Tanya Jawab: Tugas 1 Aplikasi Kalkulator Kustom",
                    "Forum khusus untuk membahas kendala, pertanyaan, atau berbagi tips terkait Tugas 1 Pemrograman Mobile Lanjut.",
                    oneWeekAgo,
                ),
            )
            val forumPythonUmum = 2
            forumDao.insertForum(
                ForumEntity(
                    forumPythonUmum,
                    kelasPYTHON,
                    "Diskusi Umum Workshop Python",
                    "Area diskusi bebas untuk pertanyaan, sharing, atau feedback terkait materi Workshop Python.",
                    currentDate,
                ),
            )

            forumDao.insertPost(
                PostEntity(
                    0,
                    forumPMLTugas1,
                    dosen1Nidn,
                    roleDosen,
                    "Selamat mengerjakan Tugas 1. Jika ada yang kurang jelas mengenai spesifikasi atau penggunaan Jetpack Compose, jangan ragu untuk bertanya di sini. Perhatikan batas waktu pengumpulan.",
                    oneWeekAgo,
                ),
            )
            val postMhs1PML1Id = 2
            forumDao.insertPost(
                PostEntity(
                    postMhs1PML1Id,
                    forumPMLTugas1,
                    mhs1Nim,
                    roleMahasiswa,
                    "Ibu Arini, untuk bagian tema visual kustom, apakah kita diizinkan menggunakan library eksternal untuk color picker, atau harus implementasi manual?",
                    threeDaysAgo,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    0,
                    forumPMLTugas1,
                    dosen1Nidn,
                    roleDosen,
                    "Untuk Tugas 1 ini, diharapkan implementasi manual untuk komponen UI standar. Penggunaan library eksternal untuk utility seperti color picker diperbolehkan, namun sebutkan dalam laporan Anda.",
                    yesterday,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    0,
                    forumPMLTugas1,
                    mhs2Nim,
                    roleMahasiswa,
                    "Saya sudah submit, Bu. Apakah ada requirement khusus untuk format file ZIP? Saya hanya mengkompres seluruh project Android Studio.",
                    currentDate,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    0,
                    forumPMLTugas1,
                    dosen1Nidn,
                    roleDosen,
                    "Betul Yuni, kompresi seluruh project sudah cukup. Terima kasih atas konfirmasinya.",
                    currentDate,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    0,
                    forumPythonUmum,
                    dosen3Nidn,
                    roleDosen,
                    "Selamat datang di Workshop Python! Silakan perkenalkan diri dan jika ada pertanyaan awal, bisa disampaikan di sini.",
                    currentDate,
                ),
            )
            forumDao.insertPost(
                PostEntity(
                    0,
                    forumPythonUmum,
                    mhs3Nim,
                    roleMahasiswa,
                    "Terima kasih, Pak Chandra. Saya Budi, ingin bertanya mengenai perbedaan performa antara list comprehension dan loop for standar untuk kasus sederhana.",
                    currentDate,
                ),
            )

            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasPML,
                    mhs1Nim,
                    oneMonthAgo,
                    "Hadir",
                    "Mengikuti sesi penuh.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasPML,
                    mhs1Nim,
                    twoWeeksAgo,
                    "Hadir",
                    "Sedikit terlambat karena ada urusan mendadak.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasPML,
                    mhs1Nim,
                    oneWeekAgo,
                    "Izin",
                    "Sakit, surat dokter terlampir via email.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasBDT,
                    mhs2Nim,
                    oneMonthAgo,
                    "Hadir",
                    null,
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasBDT,
                    mhs2Nim,
                    twoWeeksAgo,
                    "Hadir",
                    "Aktif bertanya.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasBDT,
                    mhs2Nim,
                    oneWeekAgo,
                    "Absen",
                    "Tidak ada keterangan.",
                ),
            )
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    0,
                    kelasPML,
                    mhs4Nim,
                    oneMonthAgo,
                    "Hadir",
                    null,
                ),
            )

            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    0,
                    mhs1Nim,
                    kelasPML,
                    2,
                    3,
                    twoWeeksAgo,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    0,
                    mhs2Nim,
                    kelasBDT,
                    3,
                    5,
                    oneWeekAgo,
                ),
            )
            attendanceDao.updateAttendanceStreak(
                AttendanceStreakEntity(
                    0,
                    mhs4Nim,
                    kelasPML,
                    1,
                    1,
                    oneMonthAgo,
                ),
            )
        }
    }
}
