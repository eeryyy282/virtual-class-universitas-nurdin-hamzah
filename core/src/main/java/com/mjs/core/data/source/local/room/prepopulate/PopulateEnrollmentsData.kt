package com.mjs.core.data.source.local.room.prepopulate

import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.data.source.local.room.dao.CourseDao
import java.util.Date

internal suspend fun populateEnrollments(
    courseDao: CourseDao,
    student1Id: Long,
    student2Id: Long,
    student3Id: Long,
    student4Id: Long,
    student5Id: Long,
    student6Id: Long,
    coursePML: String,
    courseAI: String,
    courseWEB: String,
    courseJARKOM: String,
    courseBDT: String,
    courseAPSI: String,
    courseMANPROSI: String,
    courseKOMMAS: String,
    courseJURDIG: String,
    coursePOLPEM: String,
    courseKEBPUB: String,
    coursePEMDA: String,
    oneMonthAgo: Date,
    twoWeeksAgo: Date,
    oneWeekAgo: Date,
    threeDaysAgo: Date,
) {
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student3Id,
            coursePML,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student3Id,
            courseAI,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student3Id,
            courseWEB,
            twoWeeksAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student3Id,
            courseJARKOM,
            oneWeekAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student1Id,
            courseBDT,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student1Id,
            courseAPSI,
            twoWeeksAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student1Id,
            coursePML,
            oneWeekAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student2Id,
            courseBDT,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student2Id,
            courseMANPROSI,
            oneWeekAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student4Id,
            courseKOMMAS,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student4Id,
            courseJURDIG,
            twoWeeksAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student4Id,
            courseWEB,
            oneWeekAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student5Id,
            coursePOLPEM,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student5Id,
            courseKEBPUB,
            twoWeeksAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student6Id,
            coursePOLPEM,
            oneMonthAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student6Id,
            coursePEMDA,
            oneWeekAgo,
            "approved",
        ),
    )
    courseDao.insertEnrollment(
        EnrollmentEntity(
            0L,
            student5Id,
            courseJARKOM,
            threeDaysAgo,
            "approved",
        ),
    )
}
