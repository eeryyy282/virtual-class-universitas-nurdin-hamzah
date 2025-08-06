package com.mjs.core.domain.usecase.task

import com.mjs.core.data.source.local.entity.AssignmentEntity
import com.mjs.core.domain.repository.IRepository

class InsertAssignmentUseCase(
    private val repository: IRepository,
) {
    suspend operator fun invoke(assignment: AssignmentEntity) = repository.insertAssignment(assignment)
}
