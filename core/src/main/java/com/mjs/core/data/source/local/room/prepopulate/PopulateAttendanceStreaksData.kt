package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.attendance.AttendanceStreakEntity
import com.mjs.core.data.source.local.room.dao.AttendanceDao
import java.util.Date

internal suspend fun populateAttendanceStreaks(
    attendanceDao: AttendanceDao,
    student1Id: Long,
    student3Id: Long,
    student4Id: Long,
    student5Id: Long,
    student6Id: Long,
    coursePML: String,
    courseBDT: String,
    courseKOMMAS: String,
    coursePOLPEM: String,
    twoWeeksAgo: Date,
    oneMonthAgo: Date,
) {
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0L,
            student3Id,
            coursePML,
            2,
            5,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0L,
            student1Id,
            courseBDT,
            3,
            3,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0L,
            student4Id,
            courseKOMMAS,
            4,
            4,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0L,
            student5Id,
            coursePOLPEM,
            2,
            3,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0L,
            student6Id,
            coursePOLPEM,
            1,
            2,
            oneMonthAgo,
        ),
    )
}
