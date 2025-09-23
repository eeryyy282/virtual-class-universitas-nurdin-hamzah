package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.attendance.AttendanceEntity
import com.mjs.core.data.source.local.room.dao.AttendanceDao
import java.util.Date

internal suspend fun populateAttendance(
    attendanceDao: AttendanceDao,
    coursePML: String,
    courseBDT: String,
    courseKOMMAS: String,
    courseWEB: String,
    coursePOLPEM: String,
    courseKEBPUB: String,
    student1Id: Long,
    student2Id: Long,
    student3Id: Long,
    student4Id: Long,
    student5Id: Long,
    student6Id: Long,
    oneMonthAgo: Date,
    twoWeeksAgo: Date,
) {
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePML,
            student3Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePML,
            student1Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePML,
            student3Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePML,
            student1Id,
            twoWeeksAgo,
            "Sakit",
            "Surat dokter terlampir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseBDT,
            student1Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseBDT,
            student2Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseBDT,
            student1Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseBDT,
            student2Id,
            twoWeeksAgo,
            "Izin",
            "Acara keluarga",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseKOMMAS,
            student4Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseKOMMAS,
            student4Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseWEB,
            student3Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseWEB,
            student4Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePOLPEM,
            student5Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePOLPEM,
            student6Id,
            oneMonthAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePOLPEM,
            student5Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            coursePOLPEM,
            student6Id,
            twoWeeksAgo,
            "Alpha",
        ),
    )
    attendanceDao.insertAttendance(
        AttendanceEntity(
            0L,
            courseKEBPUB,
            student5Id,
            twoWeeksAgo,
            "Hadir",
        ),
    )
}
