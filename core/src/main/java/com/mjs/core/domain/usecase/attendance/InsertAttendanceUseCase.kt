package com.mjs.core.domain.usecase.attendance

import com.mjs.core.data.source.local.entity.AttendanceEntity
import com.mjs.core.domain.repository.IRepository

class InsertAttendanceUseCase(
    private val repository: IRepository,
) {
    suspend operator fun invoke(attendance: AttendanceEntity) = repository.insertAttendance(attendance)
}
