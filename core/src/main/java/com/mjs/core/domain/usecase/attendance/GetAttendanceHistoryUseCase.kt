package com.mjs.core.domain.usecase.attendance

import com.mjs.core.domain.repository.IRepository

class GetAttendanceHistoryUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(
        nim: String,
        kelasId: Int,
    ) = repository.getAttendanceHistory(nim, kelasId)
}
