package com.mjs.core.domain.usecase

import com.mjs.core.domain.usecase.attendance.GetAttendanceHistoryUseCase
import com.mjs.core.domain.usecase.attendance.InsertAttendanceUseCase
import com.mjs.core.domain.usecase.auth.GetDosenUseCase
import com.mjs.core.domain.usecase.auth.GetMahasiswaUseCase
import com.mjs.core.domain.usecase.auth.RegisterUseCase
import com.mjs.core.domain.usecase.classroom.EnrollToClassUseCase
import com.mjs.core.domain.usecase.classroom.GetAllKelasUseCase
import com.mjs.core.domain.usecase.classroom.GetEnrolledClassesUseCase
import com.mjs.core.domain.usecase.classroom.GetMaterialsUseCase
import com.mjs.core.domain.usecase.forum.GetForumsUseCase
import com.mjs.core.domain.usecase.forum.GetPostsUseCase
import com.mjs.core.domain.usecase.forum.InsertPostUseCase
import com.mjs.core.domain.usecase.task.GetAssignmentsUseCase
import com.mjs.core.domain.usecase.task.InsertAssignmentUseCase
import com.mjs.core.domain.usecase.task.InsertSubmissionUseCase

data class VirtualClassUseCase(
    val getMahasiswaUseCase: GetMahasiswaUseCase,
    val getDosenUseCase: GetDosenUseCase,
    val registerUseCase: RegisterUseCase,
    val getAllKelasUseCase: GetAllKelasUseCase,
    val getEnrolledClassesUseCase: GetEnrolledClassesUseCase,
    val enrollToClassUseCase: EnrollToClassUseCase,
    val getMaterialsUseCase: GetMaterialsUseCase,
    val getAssignmentsUseCase: GetAssignmentsUseCase,
    val insertAssignmentUseCase: InsertAssignmentUseCase,
    val insertSubmissionUseCase: InsertSubmissionUseCase,
    val getForumsUseCase: GetForumsUseCase,
    val getPostsUseCase: GetPostsUseCase,
    val insertPostUseCase: InsertPostUseCase,
    val getAttendanceHistoryUseCase: GetAttendanceHistoryUseCase,
    val insertAttendanceUseCase: InsertAttendanceUseCase,
)
