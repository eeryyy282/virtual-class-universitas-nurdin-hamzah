package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.AttendanceStreakEntity
import com.mjs.core.data.source.local.room.dao.AttendanceDao

internal suspend fun populateAttendanceStreaks(
    attendanceDao: AttendanceDao,
    mhs1Nim: Int,
    mhs3Nim: Int,
    mhs4Nim: Int,
    mhs5Nim: Int,
    mhs6Nim: Int,
    kelasPML: String,
    kelasBDT: String,
    kelasKOMMAS: String,
    kelasPOLPEM: String,
    twoWeeksAgo: String,
    oneMonthAgo: String,
) {
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0,
            mhs3Nim,
            kelasPML,
            2,
            5,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0,
            mhs1Nim,
            kelasBDT,
            3,
            3,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0,
            mhs4Nim,
            kelasKOMMAS,
            4,
            4,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0,
            mhs5Nim,
            kelasPOLPEM,
            2,
            3,
            twoWeeksAgo,
        ),
    )
    attendanceDao.insertAttendanceStreak(
        AttendanceStreakEntity(
            0,
            mhs6Nim,
            kelasPOLPEM,
            1,
            2,
            oneMonthAgo,
        ),
    )
}
