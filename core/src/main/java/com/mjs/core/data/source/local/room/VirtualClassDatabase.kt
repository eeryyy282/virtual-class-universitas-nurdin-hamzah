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
import com.mjs.core.data.source.local.room.prepopulate.populateAssignmentsAndGetIds
import com.mjs.core.data.source.local.room.prepopulate.populateAttendance
import com.mjs.core.data.source.local.room.prepopulate.populateAttendanceStreaks
import com.mjs.core.data.source.local.room.prepopulate.populateDosen
import com.mjs.core.data.source.local.room.prepopulate.populateEnrollments
import com.mjs.core.data.source.local.room.prepopulate.populateForumsAndGetIds
import com.mjs.core.data.source.local.room.prepopulate.populateKelas
import com.mjs.core.data.source.local.room.prepopulate.populateMahasiswa
import com.mjs.core.data.source.local.room.prepopulate.populateMaterials
import com.mjs.core.data.source.local.room.prepopulate.populatePosts
import com.mjs.core.data.source.local.room.prepopulate.populateSubmissions
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

        private fun getDate(
            dateFormat: SimpleDateFormat,
            timeUnit: Int,
            amount: Int,
            referenceDate: Date = Date(),
        ): String {
            val cal = Calendar.getInstance()
            cal.time = referenceDate
            cal.add(
                timeUnit,
                amount,
            )
            return dateFormat.format(cal.time)
        }

        suspend fun populateDatabaseInternal(database: VirtualClassDatabase) {
            val authDao = database.authDao()
            val classroomDao = database.classroomDao()
            val taskDao = database.taskDao()
            val forumDao = database.forumDao()
            val attendanceDao = database.attendanceDao()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val today = Date()
            val currentDate = getDate(dateFormat, Calendar.DAY_OF_YEAR, 0, today)
            val yesterday = getDate(dateFormat, Calendar.DAY_OF_YEAR, -1, today)
            val threeDaysAgo = getDate(dateFormat, Calendar.DAY_OF_YEAR, -3, today)
            val oneWeekAgo = getDate(dateFormat, Calendar.DAY_OF_YEAR, -7, today)
            val twoWeeksAgo = getDate(dateFormat, Calendar.DAY_OF_YEAR, -14, today)
            val oneMonthAgo = getDate(dateFormat, Calendar.MONTH, -1, today)
            val inThreeDays = getDate(dateFormat, Calendar.DAY_OF_YEAR, 3, today)
            val inOneWeek = getDate(dateFormat, Calendar.DAY_OF_YEAR, 7, today)
            val inTwoWeeks = getDate(dateFormat, Calendar.DAY_OF_YEAR, 14, today)
            val inOneMonth = getDate(dateFormat, Calendar.MONTH, 1, today)

            val roleDosen = "dosen"
            val roleMahasiswa = "mahasiswa"

            val dosen1Nidn = 112233445
            val dosen2Nidn = 223344556
            val dosen3Nidn = 334455667
            val dosen4Nidn = 445566778
            val dosen5Nidn = 556677889
            // Mahasiswa NIMS
            val mhs1Nim = 21111073
            val mhs2Nim = 22021002
            val mhs3Nim = 22032003
            val mhs4Nim = 23041004
            val mhs5Nim = 23052005
            val mhs6Nim = 23052006
            // Kelas IDs
            val kelasPML = "IF-401"
            val kelasAI = "IF-503"
            val kelasWEB = "IF-305"
            val kelasJARKOM = "IF-301"
            val kelasGRAFKOM = "IF-405"
            val kelasBDT = "SI-302"
            val kelasAPSI = "SI-401"
            val kelasMANPROSI = "SI-501"
            val kelasKOMMAS = "IK-201"
            val kelasJURDIG = "IK-305"
            val kelasPOLPEM = "IP-101"
            val kelasKEBPUB = "IP-205"
            val kelasPEMDA = "IP-303"

            val placeholderImageUrl =
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400"

            populateDosen(authDao, dosen1Nidn, dosen2Nidn, dosen3Nidn, dosen4Nidn, dosen5Nidn)
            populateMahasiswa(authDao, mhs1Nim, mhs2Nim, mhs3Nim, mhs4Nim, mhs5Nim, mhs6Nim)
            populateKelas(
                classroomDao,
                placeholderImageUrl,
                dosen1Nidn,
                dosen2Nidn,
                dosen3Nidn,
                dosen4Nidn,
                dosen5Nidn,
                kelasPML,
                kelasAI,
                kelasWEB,
                kelasJARKOM,
                kelasGRAFKOM,
                kelasBDT,
                kelasAPSI,
                kelasMANPROSI,
                kelasKOMMAS,
                kelasJURDIG,
                kelasPOLPEM,
                kelasKEBPUB,
                kelasPEMDA,
            )
            populateEnrollments(
                classroomDao,
                mhs1Nim,
                mhs2Nim,
                mhs3Nim,
                mhs4Nim,
                mhs5Nim,
                mhs6Nim,
                kelasPML,
                kelasAI,
                kelasWEB,
                kelasJARKOM,
                kelasBDT,
                kelasAPSI,
                kelasMANPROSI,
                kelasKOMMAS,
                kelasJURDIG,
                kelasPOLPEM,
                kelasKEBPUB,
                kelasPEMDA,
                oneMonthAgo,
                twoWeeksAgo,
                oneWeekAgo,
                threeDaysAgo,
            )
            populateMaterials(
                taskDao,
                kelasPML,
                kelasAI,
                kelasBDT,
                kelasAPSI,
                kelasKOMMAS,
                kelasWEB,
                kelasJARKOM,
                kelasPOLPEM,
                kelasKEBPUB,
                kelasPEMDA,
                oneMonthAgo,
                threeDaysAgo,
                twoWeeksAgo,
                oneWeekAgo,
            )

            val assignmentIds =
                populateAssignmentsAndGetIds(
                    taskDao,
                    kelasPML,
                    kelasAI,
                    kelasBDT,
                    kelasAPSI,
                    kelasKOMMAS,
                    kelasWEB,
                    kelasPOLPEM,
                    kelasKEBPUB,
                    kelasPEMDA,
                    oneMonthAgo,
                    inOneWeek,
                    twoWeeksAgo,
                    inTwoWeeks,
                    threeDaysAgo,
                    inOneMonth,
                    oneWeekAgo,
                    inThreeDays,
                )
            populateSubmissions(
                taskDao,
                assignmentIds["tugas1PML"]!!,
                assignmentIds["tugas1AI"]!!,
                assignmentIds["tugas1BDT"]!!,
                assignmentIds["tugas1KOMMAS"]!!,
                assignmentIds["tugas1POLPEM"]!!,
                assignmentIds["tugas1KEBPUB"]!!,
                mhs1Nim,
                mhs2Nim,
                mhs3Nim,
                mhs4Nim,
                mhs5Nim,
                mhs6Nim,
                yesterday,
                currentDate,
            )

            populateAttendance(
                attendanceDao,
                kelasPML,
                kelasBDT,
                kelasKOMMAS,
                kelasWEB,
                kelasPOLPEM,
                kelasKEBPUB,
                mhs1Nim,
                mhs2Nim,
                mhs3Nim,
                mhs4Nim,
                mhs5Nim,
                mhs6Nim,
                oneMonthAgo,
                twoWeeksAgo,
            )

            val forumIds =
                populateForumsAndGetIds(
                    forumDao,
                    kelasPML,
                    kelasAI,
                    kelasBDT,
                    kelasKOMMAS,
                    kelasPOLPEM,
                    kelasKEBPUB,
                    oneMonthAgo,
                    twoWeeksAgo,
                    threeDaysAgo,
                    oneWeekAgo,
                )
            populatePosts(
                forumDao,
                forumIds["forumPML"]!!,
                forumIds["forumAI"]!!,
                forumIds["forumBDT"]!!,
                forumIds["forumKOMMAS"]!!,
                forumIds["forumPOLPEM"]!!,
                forumIds["forumKEBPUB"]!!,
                dosen1Nidn,
                dosen2Nidn,
                dosen4Nidn,
                dosen5Nidn,
                mhs1Nim,
                mhs2Nim,
                mhs3Nim,
                mhs4Nim,
                mhs5Nim,
                mhs6Nim,
                roleDosen,
                roleMahasiswa,
                oneMonthAgo,
                twoWeeksAgo,
                threeDaysAgo,
                oneWeekAgo,
                currentDate,
                yesterday,
            )

            populateAttendanceStreaks(
                attendanceDao,
                mhs1Nim,
                mhs3Nim,
                mhs4Nim,
                mhs5Nim,
                mhs6Nim,
                kelasPML,
                kelasBDT,
                kelasKOMMAS,
                kelasPOLPEM,
                twoWeeksAgo,
                oneMonthAgo,
            )
        }
    }
}
