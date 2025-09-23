package com.mjs.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.data.source.local.entity.CourseEntity
import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.LecturerEntity
import com.mjs.core.data.source.local.entity.MaterialEntity
import com.mjs.core.data.source.local.entity.PostEntity
import com.mjs.core.data.source.local.entity.StudentEntity
import com.mjs.core.data.source.local.entity.SubmissionEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.data.source.local.entity.attendance.AttendanceStreakEntity
import com.mjs.core.data.source.local.room.dao.AssignmentDao
import com.mjs.core.data.source.local.room.dao.AttendanceDao
import com.mjs.core.data.source.local.room.dao.AuthDao
import com.mjs.core.data.source.local.room.dao.CourseDao
import com.mjs.core.data.source.local.room.dao.ForumDao
import com.mjs.core.data.source.local.room.dao.SubmissionDao
import com.mjs.core.data.source.local.room.prepopulate.populateAssignmentsAndGetIds
import com.mjs.core.data.source.local.room.prepopulate.populateAttendance
import com.mjs.core.data.source.local.room.prepopulate.populateAttendanceStreaks
import com.mjs.core.data.source.local.room.prepopulate.populateCourses
import com.mjs.core.data.source.local.room.prepopulate.populateEnrollments
import com.mjs.core.data.source.local.room.prepopulate.populateForumsAndGetIds
import com.mjs.core.data.source.local.room.prepopulate.populateLecturers
import com.mjs.core.data.source.local.room.prepopulate.populateMaterials
import com.mjs.core.data.source.local.room.prepopulate.populatePosts
import com.mjs.core.data.source.local.room.prepopulate.populateStudents
import com.mjs.core.data.source.local.room.prepopulate.populateSubmissions
import com.mjs.core.utils.TimeDateConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Database(
    entities = [
        StudentEntity::class, LecturerEntity::class,
        CourseEntity::class, EnrollmentEntity::class, MaterialEntity::class,
        AssignmentEntity::class, SubmissionEntity::class, ForumEntity::class,
        PostEntity::class, AttendanceEntity::class, AttendanceStreakEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(TimeDateConverters::class)
abstract class VirtualClassDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao

    abstract fun courseDao(): CourseDao

    abstract fun assignmentDao(): AssignmentDao

    abstract fun submissionDao(): SubmissionDao

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
            timeUnit: Int,
            amount: Int,
            referenceDate: Date = Date(),
        ): Date {
            val cal = Calendar.getInstance()
            cal.time = referenceDate
            cal.add(
                timeUnit,
                amount,
            )
            return cal.time
        }

        private fun createScheduleTime(
            dayOfWeek: Int,
            hour: Int,
            minute: Int,
        ): Date {
            val calendar = Calendar.getInstance()

            calendar.set(
                2024,
                Calendar.JULY,
                22,
                hour,
                minute,
                0,
            )
            while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            if (calendar.time.before(Date())) {
                calendar.add(
                    Calendar.DAY_OF_YEAR,
                    7 * ((Date().time - calendar.time.time) / (7 * 24 * 60 * 60 * 1000) + 1).toInt(),
                )
            }
            return calendar.time
        }

        suspend fun populateDatabaseInternal(database: VirtualClassDatabase) {
            val authDao = database.authDao()
            val courseDao = database.courseDao()
            val assignmentDao = database.assignmentDao()
            val submissionDao = database.submissionDao()
            val forumDao = database.forumDao()
            val attendanceDao = database.attendanceDao()

            val today = Date()
            val currentDate = getDate(Calendar.DAY_OF_YEAR, 0, today)
            val yesterday = getDate(Calendar.DAY_OF_YEAR, -1, today)
            val threeDaysAgo = getDate(Calendar.DAY_OF_YEAR, -3, today)
            val sixDaysAgo = getDate(Calendar.DAY_OF_YEAR, -6, today)
            val oneWeekAgo = getDate(Calendar.DAY_OF_YEAR, -7, today)
            val tenDaysAgo = getDate(Calendar.DAY_OF_YEAR, -10, today)
            val twoWeeksAgo = getDate(Calendar.DAY_OF_YEAR, -14, today)
            val eighteenDaysAgo = getDate(Calendar.DAY_OF_YEAR, -18, today)
            val nineteenDaysAgo = getDate(Calendar.DAY_OF_YEAR, -19, today)
            val twentyDaysAgo = getDate(Calendar.DAY_OF_YEAR, -20, today)
            val oneMonthAgo = getDate(Calendar.MONTH, -1, today)
            val inThreeDays = getDate(Calendar.DAY_OF_YEAR, 3, today)
            val inOneWeek = getDate(Calendar.DAY_OF_YEAR, 7, today)
            val inTwoWeeks = getDate(Calendar.DAY_OF_YEAR, 14, today)
            val inOneMonth = getDate(Calendar.MONTH, 1, today)

            // Define specific schedule Date objects
            val scheduleMonday1000 = createScheduleTime(Calendar.MONDAY, 10, 0)
            val scheduleWednesday0800 = createScheduleTime(Calendar.WEDNESDAY, 8, 0)
            val scheduleThursday1400 = createScheduleTime(Calendar.THURSDAY, 14, 0)

            val roleLecturer = "lecturer"
            val roleStudent = "student"

            val lecturer1Id: Long = 112233445L
            val lecturer2Id: Long = 223344556L
            val lecturer3Id: Long = 334455667L
            val lecturer4Id: Long = 445566778L
            val lecturer5Id: Long = 556677889L

            val student1Id: Long = 21111073L
            val student2Id: Long =
                22021002L
            val student3Id: Long = 22032003L
            val student4Id: Long = 23041004L
            val student5Id: Long = 23052005L
            val student6Id: Long = 23052006L

            val coursePML = "IF-401"
            val courseAI = "IF-503"
            val courseWEB =
                "IF-305"
            val courseJARKOM = "IF-301"
            val courseGRAFKOM = "IF-405"
            val courseBDT = "SI-302"
            val courseAPSI = "SI-401"
            val courseMANPROSI = "SI-501"
            val courseKOMMAS = "IK-201"
            val courseJURDIG = "IK-305"
            val coursePOLPEM = "IP-101"
            val courseKEBPUB =
                "IP-205"
            val coursePEMDA = "IP-303"

            val placeholderImageUrl =
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400"

            populateLecturers(
                authDao,
                lecturer1Id,
                lecturer2Id,
                lecturer3Id,
                lecturer4Id,
                lecturer5Id,
            )
            populateStudents(
                authDao,
                student1Id,
                student2Id,
                student3Id,
                student4Id,
                student5Id,
                student6Id,
            )
            populateCourses(
                courseDao,
                placeholderImageUrl,
                lecturer1Id,
                lecturer2Id,
                lecturer3Id,
                lecturer4Id,
                lecturer5Id,
                coursePML,
                courseAI,
                courseWEB,
                courseJARKOM,
                courseGRAFKOM,
                courseBDT,
                courseAPSI,
                courseMANPROSI,
                courseKOMMAS,
                courseJURDIG,
                coursePOLPEM,
                courseKEBPUB,
                coursePEMDA,
                scheduleMonday1000,
                scheduleWednesday0800,
                scheduleThursday1400,
            )
            populateEnrollments(
                courseDao,
                student1Id,
                student2Id,
                student3Id,
                student4Id,
                student5Id,
                student6Id,
                coursePML,
                courseAI,
                courseWEB,
                courseJARKOM,
                courseBDT,
                courseAPSI,
                courseMANPROSI,
                courseKOMMAS,
                courseJURDIG,
                coursePOLPEM,
                courseKEBPUB,
                coursePEMDA,
                oneMonthAgo,
                twoWeeksAgo,
                oneWeekAgo,
                threeDaysAgo,
            )
            populateMaterials(
                courseDao,
                coursePML,
                courseAI,
                courseBDT,
                courseAPSI,
                courseKOMMAS,
                courseWEB,
                courseJARKOM,
                coursePOLPEM,
                courseKEBPUB,
                coursePEMDA,
                oneMonthAgo,
                threeDaysAgo,
                twoWeeksAgo,
                oneWeekAgo,
            )

            val assignmentIds =
                populateAssignmentsAndGetIds(
                    assignmentDao,
                    coursePML,
                    courseAI,
                    courseBDT,
                    courseAPSI,
                    courseKOMMAS,
                    courseWEB,
                    coursePOLPEM,
                    courseKEBPUB,
                    coursePEMDA,
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
                submissionDao,
                assignmentIds["assignment1PML"]!!,
                assignmentIds["assignment1AI"]!!,
                assignmentIds["assignment1BDT"]!!,
                assignmentIds["assignment1KOMMAS"]!!,
                assignmentIds["assignment1POLPEM"]!!,
                assignmentIds["assignment1KEBPUB"]!!,
                student1Id,
                student2Id,
                student3Id,
                student4Id,
                student5Id,
                student6Id,
                yesterday,
                currentDate,
            )

            populateAttendance(
                attendanceDao,
                coursePML,
                courseBDT,
                courseKOMMAS,
                courseWEB,
                coursePOLPEM,
                courseKEBPUB,
                student1Id,
                student2Id,
                student3Id,
                student4Id,
                student5Id,
                student6Id,
                oneMonthAgo,
                twoWeeksAgo,
            )

            val forumIds =
                populateForumsAndGetIds(
                    forumDao,
                    coursePML,
                    courseAI,
                    courseBDT,
                    courseKOMMAS,
                    coursePOLPEM,
                    courseKEBPUB,
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
                lecturer1Id,
                lecturer2Id,
                lecturer4Id,
                lecturer5Id,
                student1Id,
                student2Id,
                student3Id,
                student4Id,
                student5Id,
                student6Id,
                roleLecturer,
                roleStudent,
                oneMonthAgo,
                twentyDaysAgo,
                nineteenDaysAgo,
                eighteenDaysAgo,
                twoWeeksAgo,
                tenDaysAgo,
                oneWeekAgo,
                sixDaysAgo,
                threeDaysAgo,
                yesterday,
                currentDate,
            )

            populateAttendanceStreaks(
                attendanceDao,
                student1Id,
                student3Id,
                student4Id,
                student5Id,
                student6Id,
                coursePML,
                courseBDT,
                courseKOMMAS,
                coursePOLPEM,
                twoWeeksAgo,
                oneMonthAgo,
            )
        }
    }
}
