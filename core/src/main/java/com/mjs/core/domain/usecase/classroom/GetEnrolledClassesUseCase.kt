package com.mjs.core.domain.usecase.classroom

import com.mjs.core.domain.repository.IRepository

class GetEnrolledClassesUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(nim: String) = repository.getEnrolledClasses(nim)
}
