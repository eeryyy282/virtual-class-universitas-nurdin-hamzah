package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.data.source.local.room.dao.AttendanceDao

internal suspend fun populateAttendance(
    attendanceDao: AttendanceDao,
    kelasPML: String,
    kelasBDT: String,
    kelasKOMMAS: String,
    kelasWEB: String,
    kelasPOLPEM: String,
    kelasKEBPUB: String,
    mhs1Nim: Int,
    mhs2Nim: Int,
    mhs3Nim: Int,
    mhs4Nim: Int,
    mhs5Nim: Int,
    mhs6Nim: Int,
    oneMonthAgo: String,
    twoWeeksAgo: String,
) {
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPML,
            mhs3Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPML,
            mhs1Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPML,
            mhs3Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPML,
            mhs1Nim,
            twoWeeksAgo,
            "Sakit",
            "Surat dokter terlampir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasBDT,
            mhs1Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasBDT,
            mhs2Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasBDT,
            mhs1Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasBDT,
            mhs2Nim,
            twoWeeksAgo,
            "Izin",
            "Acara keluarga",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasKOMMAS,
            mhs4Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasKOMMAS,
            mhs4Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasWEB,
            mhs3Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasWEB,
            mhs4Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPOLPEM,
            mhs5Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPOLPEM,
            mhs6Nim,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPOLPEM,
            mhs5Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasPOLPEM,
            mhs6Nim,
            twoWeeksAgo,
            "Alpha",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0,
            kelasKEBPUB,
            mhs5Nim,
            twoWeeksAgo,
            "Hadir",
        ),
    )
}
