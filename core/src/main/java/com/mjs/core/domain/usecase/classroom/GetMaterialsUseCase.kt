package com.mjs.core.domain.usecase.classroom

import com.mjs.core.domain.repository.IRepository

class GetMaterialsUseCase(
    private val repository: IRepository,
) {
    operator fun invoke(kelasId: Int) = repository.getMaterialsByClass(kelasId)
}
