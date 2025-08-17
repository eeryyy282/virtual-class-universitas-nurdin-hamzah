package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.room.dao.ClassroomDao

internal suspend fun populateEnrollments(
    classroomDao: ClassroomDao,
    mhs1Nim: Int,
    mhs2Nim: Int,
    mhs3Nim: Int,
    mhs4Nim: Int,
    mhs5Nim: Int,
    mhs6Nim: Int,
    kelasPML: String,
    kelasAI: String,
    kelasWEB: String,
    kelasJARKOM: String,
    kelasBDT: String,
    kelasAPSI: String,
    kelasMANPROSI: String,
    kelasKOMMAS: String,
    kelasJURDIG: String,
    kelasPOLPEM: String,
    kelasKEBPUB: String,
    kelasPEMDA: String,
    oneMonthAgo: String,
    twoWeeksAgo: String,
    oneWeekAgo: String,
    threeDaysAgo: String,
) {
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs3Nim,
            kelasPML,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs3Nim,
            kelasAI,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs3Nim,
            kelasWEB,
            twoWeeksAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs3Nim,
            kelasJARKOM,
            oneWeekAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs1Nim,
            kelasBDT,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs1Nim,
            kelasAPSI,
            twoWeeksAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs1Nim,
            kelasPML,
            oneWeekAgo,
            "approved",
        ),
    ) // Cross-major
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs2Nim,
            kelasBDT,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs2Nim,
            kelasMANPROSI,
            oneWeekAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs4Nim,
            kelasKOMMAS,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs4Nim,
            kelasJURDIG,
            twoWeeksAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs4Nim,
            kelasWEB,
            oneWeekAgo,
            "approved",
        ),
    ) // Cross-major
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs5Nim,
            kelasPOLPEM,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs5Nim,
            kelasKEBPUB,
            twoWeeksAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs6Nim,
            kelasPOLPEM,
            oneMonthAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs6Nim,
            kelasPEMDA,
            oneWeekAgo,
            "approved",
        ),
    )
    classroomDao.insertEnrollment(
        EnrollmentEntity(
            0,
            mhs5Nim,
            kelasJARKOM,
            threeDaysAgo,
            "approved",
        ),
    )
}
