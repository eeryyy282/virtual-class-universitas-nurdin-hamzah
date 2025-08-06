package com.mjs.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
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
}
