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

    class PrepopulateCallback(
        private val authDaoProvider: () -> AuthDao,
    ) : Callback() {
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch {
                populateDatabaseInternal(authDaoProvider())
            }
        }

        suspend fun populateDatabaseInternal(authDao: AuthDao) {
            authDao.registerDosen(
                DosenEntity(
                    nidn = "1234567890",
                    nama = "Dr. Budi Santoso",
                    email = "budi.santoso@example.com",
                    password = "password123",
                ),
            )
            authDao.registerDosen(
                DosenEntity(
                    nidn = "0987654321",
                    nama = "Prof. Siti Aminah",
                    email = "siti.aminah@example.com",
                    password = "password456",
                ),
            )
            authDao.registerMahasiswa(
                MahasiswaEntity(
                    nim = "21111073",
                    nama = "Muhammad Juzairi Safitli",
                    email = "airiagustus82@gmail.com",
                    password = "12345678",
                ),
            )
        }
    }
}
