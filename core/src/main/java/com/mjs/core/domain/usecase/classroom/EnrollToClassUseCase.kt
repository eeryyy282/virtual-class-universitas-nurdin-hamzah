package com.mjs.core.domain.usecase.classroom

import com.mjs.core.data.source.local.entity.EnrollmentEntity
import com.mjs.core.domain.repository.IRepository

class EnrollToClassUseCase(
    private val repository: IRepository,
) {
    suspend operator fun invoke(enrollment: EnrollmentEntity) = repository.enrollToClass(enrollment)
}
