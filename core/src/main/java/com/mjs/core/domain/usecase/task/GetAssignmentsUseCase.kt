package com.mjs.core.domain.usecase.task

import com.mjs.core.domain.repository.IRepository

class GetAssignmentsUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(kelasId: Int) = repository.getAssignmentsByClass(kelasId)
}
